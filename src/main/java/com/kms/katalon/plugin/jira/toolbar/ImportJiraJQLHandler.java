package com.kms.katalon.plugin.jira.toolbar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.katalon.platform.api.model.Folder;
import com.katalon.platform.api.model.TestCase;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.service.TestCaseManager;
import com.kms.katalon.platform.entity.impl.TestCaseImpl;
import com.kms.katalon.plugin.jira.api.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.plugin.jira.common.JiraIntegrationException;
import com.kms.katalon.plugin.jira.common.JiraObjectToEntityConverter;
import com.kms.katalon.plugin.jira.common.JiraUIComponent;
import com.kms.katalon.plugin.jira.constants.JiraIntegrationMessageConstants;
import com.kms.katalon.plugin.jira.entity.ImprovedIssue;
import com.kms.katalon.plugin.jira.entity.JiraFilter;
import com.kms.katalon.plugin.jira.entity.JiraIssue;
import com.kms.katalon.plugin.jira.setting.JiraIntegrationSettingStore;
import com.kms.katalon.plugin.jira.ui.ImportJiraJQLDialog;
import com.kms.katalon.plugin.jira.ui.IssueSelectionDialog;


public class ImportJiraJQLHandler implements JiraUIComponent {

//    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

//    @CanExecute
//    public boolean canExecute() {
//        try {
//                
////            return getCurrentProject() != null && getSettingStore().isIntegrationEnabled();
//        } catch (IOException e) {
//            //TODO
////            LoggerSingleton.logError(e);
//            return false;
//        }
//    }

    public void execute() {
        JiraIntegrationSettingStore settingStore = getSettingStore();
        boolean isIntegrationEnabled = false;
        try {
            isIntegrationEnabled = settingStore.isIntegrationEnabled();
        } catch (IOException ignored) {
        }
        if (!isIntegrationEnabled) {
            return;
        }
       
        Shell activeShell = Display.getCurrent().getActiveShell();
        ImportJiraJQLDialog dialog = new ImportJiraJQLDialog(activeShell);
        if (dialog.open() != ImportJiraJQLDialog.OK) {
            return;
        }
        JiraFilter filter = dialog.getFilter();
        
//        CommonDialogs dialogUtil = PlatformServices.getCommonDialogsUtil();
//        Folder selectedFolder;
//        try {
//            selectedFolder = dialogUtil.openTestCaseFolderSelectionDialog(activeShell);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        
        IssueSelectionDialog selectionDialog = new IssueSelectionDialog(activeShell, filter.getIssues());
        if (selectionDialog.open() != IssueSelectionDialog.OK) {
            return;
        }
        createTestCasesAsIssues(selectionDialog.getSelectedFolder(), selectionDialog.getSelectedIssues());
    }

    public void createTestCasesAsIssues(Folder folder, List<JiraIssue> issues) {
        if (folder == null || issues.isEmpty()) {
            return;
        }
        final TestCaseManager testCaseManager = ApplicationManager.getTestCaseManager();
        Job job = new Job(JiraIntegrationMessageConstants.JOB_TASK_IMPORTING_ISSUES) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(StringUtils.EMPTY, issues.size());
                List<TestCase> newTreeEntities = new ArrayList<>();
                try {
                    monitor.setTaskName(JiraIntegrationMessageConstants.JOB_SUB_TASK_FETCHING_KATALON_FIELD);
                    Optional<Field> katalonCommentField = getKatalonCommentField();
                    monitor.worked(1);

                    for (JiraIssue issue : issues) {
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                        try {
                            String newTestCaseName = testCaseManager.getAvailableTestCaseName(folder, issue.getKey());
                            monitor.setTaskName(MessageFormat.format(
                                    JiraIntegrationMessageConstants.JOB_SUB_TASK_IMPORTING_ISSUE,
                                    newTestCaseName));
//                            TestCaseEntity testCase = TestCaseFileServiceManager.newTestCaseWithoutSave(folder,
//                                    newTestCaseName);
                            TestCaseImpl testCase = new TestCaseImpl();
                            testCase.setDescription(getDescriptionFromIssue(issue));
                            String comment = getComment(katalonCommentField, issue);
                            testCase.setComment(comment);
                            if (StringUtils.isNotEmpty(comment)) {
                                String script = getScriptAsComment(comment);
                                testCase.setScriptContent(new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)));
                            }
                           
//                            TestCaseEntity testCase = testCaseController.newTestCaseWithoutSave(folder,
//                                    newTestCaseName);
//                            testCase.setDescription(getDescriptionFromIssue(issue));
//                            
//                            testCase.setComment(comment);
//
                            JiraObjectToEntityConverter.updateTestCase(issue, testCase);
//                            TestCaseFileServiceManager.saveNewTestCase(testCase);
//
//                            if (StringUtils.isNotEmpty(comment)) {
//                                GroovyGuiUtil.addContentToTestCase(testCase, getScriptAsComment(comment));
//                            }
//
//                            newTreeEntities.add(new TestCaseTreeEntity(testCase, folderTreeEntity));
                            testCaseManager.newTestCase(testCase);
                            monitor.worked(1);
                        } catch (Exception e) {
//                            LoggerSingleton.logError(e);
                            //TODO log 
                        }
                    }
                    return Status.OK_STATUS;
                } finally {
                    monitor.done();
//                    UISynchronizeService.syncExec(() -> {
//                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderTreeEntity);
//                        eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEMS, newTreeEntities.toArray());
//                    });
                }
            }

            private Optional<Field> getKatalonCommentField() {
                try {
                    return new JiraIntegrationAuthenticationHandler().getKatalonCustomField(getCredential());
                } catch (JiraIntegrationException | IOException e) {
//                    LoggerSingleton.logError(e);
                    //TODO log
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
                    return StringUtils.EMPTY;
                }
                return ObjectUtils.toString(customFields.get(customFieldId));
            }

            private String getScriptAsComment(String comment) {
                StringBuilder commentBuilder = new StringBuilder();
                Arrays.asList(StringUtils.split(comment, "\r\n")).forEach(line -> {
                    commentBuilder
                            .append(String.format("WebUI.comment('%s')\n", escapeGroovy(line)));
                });
                return commentBuilder.toString();
            }
        };
        job.setUser(true);
        job.schedule();

    }
    
    private String escapeGroovy(String rawString) {
        return StringUtils.isNotEmpty(rawString) ? StringEscapeUtils.escapeJava(rawString).replace("'", "\\'")
                : rawString;
    }

    private String getDescriptionFromIssue(JiraIssue issue) {
        return String.format("%s: %s\n%s: %s", JiraIntegrationMessageConstants.SUMMARY,
                StringUtils.defaultString(issue.getFields().getSummary()), JiraIntegrationMessageConstants.DESCRIPTION,
                StringUtils.defaultString(issue.getFields().getDescription()));
    }

//    private FolderEntity getFolder(FolderTreeEntity folderTreeEntity) {
//        try {
//            return folderTreeEntity.getObject();
//        } catch (Exception ignored) {}
//        return null;
//    }
//    
//    private Project getCurrentProject() {
//        return PlatformServices.
//    }
}
