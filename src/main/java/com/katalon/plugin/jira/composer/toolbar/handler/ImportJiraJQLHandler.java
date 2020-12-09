package com.katalon.plugin.jira.composer.toolbar.handler;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.DateTimeZone;
import org.joda.time.tz.UTCProvider;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.katalon.platform.api.controller.FeatureFileController;
import com.katalon.platform.api.controller.FolderController;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.SystemFileEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.ui.DialogActionService;
import com.katalon.platform.api.ui.TestExplorerActionService;
import com.katalon.platform.api.ui.UISynchronizeService;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.PreferenceConstants;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.toolbar.dialog.ImportJiraJQLDialog;
import com.katalon.plugin.jira.composer.toolbar.dialog.ImportJiraJQLDialog.ImportJiraJQLResult;
import com.katalon.plugin.jira.composer.toolbar.dialog.IssueSelectionDialog;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.entity.ImprovedIssue;
import com.katalon.plugin.jira.core.entity.JiraField;
import com.katalon.plugin.jira.core.entity.JiraFilter;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;
import com.katalon.plugin.jira.core.setting.StoredJiraObject;
import com.katalon.plugin.jira.core.util.PlatformUtil;

public class ImportJiraJQLHandler implements JiraUIComponent {
    
    private boolean ableToGetCustomFieldContentFromJiraCloud;

    public void execute(Shell activeShell) {
        ImportJiraJQLDialog dialog = new ImportJiraJQLDialog(activeShell);
        if (dialog.open() != ImportJiraJQLDialog.OK) {
            return;
        }
        ImportJiraJQLResult result = dialog.getResult();
        JiraFilter filter = result.getJiraFilter();
        try {
            FolderEntity folder = PlatformUtil.getUIService(DialogActionService.class)
                    .showTestCaseFolderSelectionDialog(activeShell, "Test Case Folder Selection");

            if (folder != null) {
                IssueSelectionDialog selectionDialog = new IssueSelectionDialog(activeShell, folder,
                        filter.getIssues());
                if (selectionDialog.open() != IssueSelectionDialog.OK) {
                    return;
                }
                createTestCasesAsIssues(result, folder, selectionDialog.getSelectedIssues());
            }
        } catch (PlatformException e) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, e.getMessage());
        }
    }

    public void createTestCasesAsIssues(ImportJiraJQLResult result, FolderEntity folder, List<JiraIssue> issues) {
        if (folder == null || issues.isEmpty()) {
            return;
        }
        final TestCaseController testCaseController = PlatformUtil.getPlatformController(TestCaseController.class);
        final ProjectEntity currentProject = getCurrentProject();

        Job job = new Job(ComposerJiraIntegrationMessageConstant.JOB_TASK_IMPORTING_ISSUES) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(StringUtils.EMPTY, issues.size());
                JiraRestClient restClient = null;
                try {
                    monitor.setTaskName(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_FETCHING_KATALON_FIELD);
                    JiraCredential credential = getCredential();
                    Optional<Field> katalonCommentField = getKatalonCommentField(credential);
                    monitor.worked(1);
                    List<TestCaseEntity> testCases = new ArrayList<>();

                    JiraRestClientFactory clientFactory = new AsynchronousJiraRestClientFactory();
                    restClient = clientFactory.createWithBasicHttpAuthentication(
                            URI.create(credential.getServerUrl()), credential.getUsername(),
                            credential.getPassword());
                    DateTimeZone.setProvider(new UTCProvider());

                    ableToGetCustomFieldContentFromJiraCloud = true;
                    for (JiraIssue issue : issues) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        String validatedTestCaseName = StringUtils
                                .defaultString(issue.getKey() + " " + issue.getFields().getSummary());
                        validatedTestCaseName = validateName(folder, validatedTestCaseName);
                        String newTestCaseName = testCaseController.getAvailableTestCaseName(currentProject, folder, validatedTestCaseName);
                        monitor.setTaskName(MessageFormat.format(
                                ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_IMPORTING_ISSUE,
                                newTestCaseName));
                        String description = getDescriptionFromIssue(issue);
                        String katalonCustomFieldValue = getComment(katalonCommentField, issue);
                        TestCaseEntity testCase = testCaseController.newTestCase(currentProject, folder,
                                new NewTestCaseIssueDescription(newTestCaseName, description, katalonCustomFieldValue));

                        testCase = JiraObjectToEntityConverter.updateTestCase(issue, testCase);

                        String testCaseScript = getScriptForComment(katalonCustomFieldValue);
                        
                        if (result.isLinkToBddFeatureFile()) {
                            FeatureFileController featureController = PlatformUtil
                                    .getPlatformController(FeatureFileController.class);
                            FolderEntity featureFolder = PlatformUtil.getPlatformController(FolderController.class)
                                    .getFolder(currentProject, "Include/features");
                            String featureFileName = featureController.getAvailableFeatureFileName(currentProject,
                                    featureFolder, testCase.getName() + ".feature");
                            SystemFileEntity systemFile = featureController.newFeatureFile(currentProject,
                                    featureFolder, featureFileName);
                            FileUtils.write(systemFile.getFile(), katalonCustomFieldValue);

                            testCaseScript = getScriptForFeatureFile(systemFile);
                        }

                        FileUtils.write(testCase.getScriptFile(), testCaseScript, true);
                        testCases.add(testCase);
                        monitor.worked(1);
                    }
                    if (!ableToGetCustomFieldContentFromJiraCloud && result.isLinkToBddFeatureFile()) {
                        String serverUrl = credential.getServerUrl();
                        boolean isJiraCloud = serverUrl.contains(".atlassian.net") || serverUrl.contains(".jira.com");
                        if (isJiraCloud) {
                            PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                                MessageDialog.openError(null, StringConstants.ERROR,
                                        ComposerJiraIntegrationMessageConstant.ERROR_CUSTOM_FIELD_NOT_FOUND);
                            });
                        }
                    }
                    TestExplorerActionService explorerActionService = PlatformUtil
                            .getUIService(TestExplorerActionService.class);
                    explorerActionService.refreshFolder(currentProject, folder);
                    explorerActionService.selectTestCases(currentProject, testCases);
                    return Status.OK_STATUS;
                } catch (PlatformException | JiraIntegrationException | IOException e) {
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        MessageDialog.openError(null, StringConstants.ERROR, e.getMessage());
                    });
                    return Status.CANCEL_STATUS;
                } finally {
                    if (restClient != null) {
                        try {
                            restClient.close();
                        } catch (IOException ignored) {}
                    }
                    monitor.done();
                }
            }

            private String validateName(FolderEntity parentFolder, String name) throws IndexOutOfBoundsException {
                /// Replace all unwanted characters for Test Case name
                name = name.replaceAll("[!$%#^&*+|~=`{}\\[\\]:\";'<>?\\/]", "");

                // /Project/Scripts/Parent Folder/Test Case Name/Script1602834971827.groovy
                int taken = parentFolder.getFileLocation().length() + PreferenceConstants.FILE_SEPAPRATOR_LENGTH
                        + PreferenceConstants.MAX_SUFFIX_LENGTH + PreferenceConstants.FILE_SEPAPRATOR_LENGTH
                        + PreferenceConstants.GROOVY_SCRIPT_FILE_NAME_LENGTH;
                int available = PreferenceConstants.MAX_FILE_PATH_LENGTH - taken;
                name = name.trim();
                name = name.length() > available ? name.substring(0, available) : name;
                return name.trim();
            }

            private Optional<Field> getKatalonCommentField(JiraCredential jiraCredential) throws IOException {
                try {
                    String serverUrl = jiraCredential.getServerUrl();
                    boolean isJiraCloud = serverUrl.contains(".atlassian.net") || serverUrl.contains(".jira.com");
                    if (isJiraCloud) {
                        JiraIntegrationSettingStore settingStore = getSettingStore();
                        if (settingStore.isEnableFetchingContentFromJiraCloud()) {
                            StoredJiraObject<JiraField> customField = settingStore.getStoredJiraCloudField();
                            return Optional.ofNullable(customField.getDefaultJiraObject());
                        }
                    }
                    return new JiraIntegrationAuthenticationHandler().getKatalonJiraServerCustomField(jiraCredential);
                } catch (JiraIntegrationException exception) {
                    return Optional.empty();
                }
            }

            private String getComment(Optional<Field> katalonField, JiraIssue issue) {
                if (!katalonField.isPresent()) {
                    return StringUtils.EMPTY;
                }
                ImprovedIssue fields = issue.getFields();
                if (fields == null) {
                    return StringUtils.EMPTY;
                }
                Map<String, Object> customFields = fields.getCustomFields();
                String customFieldId = katalonField.get().getId();
                if (!customFields.containsKey(customFieldId)) {
                    ableToGetCustomFieldContentFromJiraCloud = false;
                    return StringUtils.EMPTY;
                }
                Object jsonComment = customFields.get(customFieldId);
                return jsonComment != null ? jsonComment.toString() : StringUtils.EMPTY;
            }

            private String getScriptForFeatureFile(SystemFileEntity systemFile) {
                return String.format("CucumberKW.runFeatureFile('%s')\n", systemFile.getId());
            }

            private String getScriptForComment(String comment) {
                StringBuilder commentBuilder = new StringBuilder();
                Arrays.asList(StringUtils.split(comment, "\r\n")).forEach(line -> {
                    commentBuilder
                            .append(String.format("WebUI.comment('%s')\n", StringEscapeUtils.escapeJava(line)));
                });
                return commentBuilder.toString();
            }

            private String getDescriptionFromIssue(JiraIssue issue) {
                return String.format("%s: %s\n%s: %s", StringConstants.SUMMARY,
                        StringUtils.defaultString(issue.getFields().getSummary()), StringConstants.DESCRIPTION,
                        StringUtils.defaultString(issue.getFields().getDescription()));
            }

        };
        job.setUser(true);
        job.schedule();
    }

    private static class NewTestCaseIssueDescription implements TestCaseController.NewDescription {
        private final String name;

        private final String description;

        private final String comment;

        private final static String JIRA_INTEGRATION_TAG = "jira-integration";

        public NewTestCaseIssueDescription(String name, String description, String comment) {
            this.name = name;
            this.description = description;
            this.comment = comment;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getComment() {
            return comment;
        }

        @Override
        public String getTag() {
            return JIRA_INTEGRATION_TAG;
        }

    }
}
