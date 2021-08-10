package com.katalon.plugin.jira.composer.preference;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.domain.User;
import com.katalon.platform.api.exception.CryptoException;
import com.katalon.platform.api.exception.InvalidDataTypeFormatException;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.dialog.HelpCompositeForDialog;
import com.katalon.plugin.jira.composer.preference.JiraConnectionJob.JiraConnectionResult;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.entity.JiraField;
import com.katalon.plugin.jira.core.entity.JiraIssueType;
import com.katalon.plugin.jira.core.entity.JiraProject;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;
import com.katalon.plugin.jira.core.setting.StoredJiraObject;

public class JiraSettingsComposite implements JiraUIComponent {

    private static final String DOCUMENT_URL = ComposerJiraIntegrationMessageConstant.DOCUMENT_URL_JIRA_CLOUD_FETCH_CONTENT;

    private static final String API_TOKEN_DOCUMENT_URL = "https://confluence.atlassian.com/cloud/api-tokens-938839638.html";

    private Logger logger = LoggerFactory.getLogger(JiraSettingsComposite.class);

    private Composite container;

    private Composite enablerComposite;

    private Button chckEnableIntegration, chckAutoSubmitTestResult, chckEncrypt, chckEnableFetchingContentFromJiraCloud,
            btnFetchFields, btnConnect;

    private Composite mainComposite;

    private Text txtServerUrl, txtUsername, txtPassword;

    private Button chckUseTestCaseNameAsSumarry, chckAttachScreenshot, chckAttachLog;

    private JiraIntegrationSettingStore settingStore;

    private Combo cbbIssueTypes, cbbProjects, cbbFields;

    private DisplayedComboboxObject<JiraProject> displayedJiraProject;

    private DisplayedComboboxObject<JiraIssueType> displayedJiraIssueType;

    private DisplayedComboboxObject<JiraField> displayedJiraField;

    private List<JiraIssueType> allIssueTypes;

    private boolean projectScope;

    private User user;

    private Shell shell;

    private Link linkApiToken;

    private Group grpFetchOptions;

    public JiraSettingsComposite() {
        settingStore = getSettingStore();
    }

    public void registerControlModifyListeners() {
        chckEnableIntegration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                enableIntegrationComposite();
                enableFetchOptionsComposite(chckEnableIntegration.getSelection());
            }
        });
        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = getShell();
                JiraConnectionJob job = new JiraConnectionJob(shell, getEdittingCredential());
                JiraConnectionResult result = job.run();
                if (result.getError() != null) {
                    enableFetchOptionsComposite(false);
                    logger.error("Unable to connect to JIRA server", result.getError());
                    MessageDialog.openError(shell, StringConstants.ERROR, result.getError().getMessage());
                    return;
                }

                if (!result.isComplete()) {
                    return;
                }
                projectScope = true;
                user = result.getUser();
                allIssueTypes = Arrays.asList(result.getJiraIssueTypes().getStoredObject().getJiraObjects());

                displayedJiraProject = result.getJiraProjects().updateDefaultURIFrom(displayedJiraProject);
                updateCombobox(cbbProjects, displayedJiraProject);

                enableFetchOptionsComposite(true);
                if (grpFetchOptions.isEnabled()) {
                    displayedJiraField = result.getJiraFields().updateDefaultURIFrom(displayedJiraField);
                    updateCombobox(cbbFields, displayedJiraField);
                    new AutoCompleteComboInput(cbbFields).build();
                }

                MessageDialog.openInformation(shell, StringConstants.INFO,
                        MessageFormat.format(ComposerJiraIntegrationMessageConstant.PREF_MSG_ACCOUNT_CONNECTED,
                                result.getUser().getDisplayName()));
            }
        });
        btnFetchFields.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Shell shell = getShell();
                JiraConnectionJob job = new JiraConnectionJob(shell, getEdittingCredential());
                JiraConnectionResult result = job.run();
                displayedJiraField = result.getJiraFields().updateDefaultURIFrom(displayedJiraField);
            }
        });
        linkApiToken.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });

        cbbProjects.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (projectScope) {
                    int selection = cbbProjects.getSelectionIndex();
                    JiraProject selectedJiraProject = displayedJiraProject.getStoredObject()
                            .getJiraObjects()[selection];
                    displayedJiraIssueType = getIssueTypeForProject(selectedJiraProject);
                    updateCombobox(cbbIssueTypes, displayedJiraIssueType);
                }
            }
        });
        btnFetchFields.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = getShell();
                JiraConnectionJob job = new JiraConnectionJob(shell, getEdittingCredential());
                JiraConnectionResult result = job.fetchFields();
                if (result.getError() != null) {
                    logger.error("Unable to fetch fields from JIRA", result.getError());
                    MessageDialog.openError(shell, StringConstants.ERROR, result.getError().getMessage());
                    return;
                }

                if (!result.isComplete()) {
                    return;
                }

                displayedJiraField = result.getJiraFields().updateDefaultURIFrom(displayedJiraField);
                updateCombobox(cbbFields, displayedJiraField);
                new AutoCompleteComboInput(cbbFields).build();
            }
        });
    }

    private DisplayedComboboxObject<JiraIssueType> getIssueTypeForProject(JiraProject project) {
        List<JiraIssueType> jiraIssueTypes = new ArrayList<>();
        URI defaultURI = null;
        Map<String, JiraIssueType> globalJiraIssueTypes = new HashMap<>();
        for (JiraIssueType issueType : allIssueTypes) {
            if (issueType.getScope() == null || issueType.getScope().getProject() == null) {
                globalJiraIssueTypes.put(issueType.getName(), issueType);
                continue;
            }
            if (!issueType.getScope().getProject().getId().equals(project.getId())) {
                continue;
            }
            jiraIssueTypes.add(issueType);
            if ("Bug".equals(issueType.getName())) {
                defaultURI = issueType.getSelf();
            }
        }
        if (jiraIssueTypes.isEmpty()) {
            for (Entry<String, JiraIssueType> global : globalJiraIssueTypes.entrySet()) {
                JiraIssueType issueType = global.getValue();
                if ("Bug".equals(issueType.getName())) {
                    defaultURI = issueType.getSelf();
                }
                jiraIssueTypes.add(issueType);
            }
        }
        return new DisplayedComboboxObject<>(
                new StoredJiraObject<>(defaultURI, jiraIssueTypes.toArray(new JiraIssueType[0])));
    }

    private void maskPasswordField() {
        txtPassword.setEchoChar("\u2022".charAt(0));
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    private Shell getShell() {
        return shell;
    }

    public static void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursiveSetEnabled(c, enabled);
                c.setEnabled(enabled);
            }
        } else {
            ctrl.setEnabled(enabled);
        }
    }

    public void initializeData() {
        try {
            chckEnableIntegration.setSelection(settingStore.isIntegrationEnabled());
            enableIntegrationComposite();

            boolean encryptionEnabled = settingStore.isEncryptionEnabled();
            txtServerUrl.setText(settingStore.getServerUrl(encryptionEnabled));
            txtUsername.setText(settingStore.getUsername(encryptionEnabled));
            txtPassword.setText(settingStore.getPassword(encryptionEnabled));
            chckEncrypt.setSelection(encryptionEnabled);
            maskPasswordField();

            chckUseTestCaseNameAsSumarry.setSelection(settingStore.isUseTestCaseNameAsSummaryEnabled());
            chckAttachScreenshot.setSelection(settingStore.isAttachScreenshotEnabled());
            chckAttachLog.setSelection(settingStore.isAttachLogEnabled());
            chckAutoSubmitTestResult.setSelection(settingStore.isSubmitTestResultAutomatically());

            displayedJiraProject = new DisplayedComboboxObject<>(settingStore.getStoredJiraProject());
            updateCombobox(cbbProjects, displayedJiraProject);

            StoredJiraObject<JiraIssueType> storedJiraIssueType = settingStore.getStoredJiraIssueType();

            projectScope = settingStore.isProjectScopeEnable();
            if (storedJiraIssueType != null && storedJiraIssueType.getJiraObjects() != null) {
                if (projectScope) {
                    allIssueTypes = Arrays.asList(storedJiraIssueType.getJiraObjects());
                    if (allIssueTypes != null && !allIssueTypes.isEmpty() && cbbProjects.getSelectionIndex() >= 0) {
                        int selectionProjectIndex = cbbProjects.getSelectionIndex();
                        displayedJiraIssueType = getIssueTypeForProject(
                                displayedJiraProject.getStoredObject().getJiraObjects()[selectionProjectIndex]);
                        displayedJiraIssueType.getStoredObject()
                                .setDefaultURI(storedJiraIssueType.getDefaultProjectURI());
                        updateCombobox(cbbIssueTypes, displayedJiraIssueType);
                    }
                } else {
                    displayedJiraIssueType = new DisplayedComboboxObject<>(settingStore.getStoredJiraIssueType());
                    updateCombobox(cbbIssueTypes, displayedJiraIssueType);
                }
            }

            enableFetchOptionsComposite(chckEnableIntegration.getSelection());
            displayedJiraField = new DisplayedComboboxObject<>(settingStore.getStoredJiraCloudField());
            if (grpFetchOptions.isEnabled()) {
                chckEnableFetchingContentFromJiraCloud
                        .setSelection(settingStore.isEnableFetchingContentFromJiraCloud());
                updateCombobox(cbbFields, displayedJiraField);
                new AutoCompleteComboInput(cbbFields).build();
            }
            user = settingStore.getJiraUser();
        } catch (IOException | GeneralSecurityException | InvalidDataTypeFormatException | CryptoException e) {
            MessageDialog.openError(mainComposite.getShell(), StringConstants.ERROR, e.getMessage());
        }
    }

    private void updateCombobox(Combo combobox, DisplayedComboboxObject<?> displayedJiraObject) {
        combobox.setItems(displayedJiraObject.getNames());
        int defaultProjectIndex = displayedJiraObject.getDefaultObjectIndex();
        if (defaultProjectIndex >= 0) {
            combobox.select(defaultProjectIndex);
            combobox.notifyListeners(SWT.Selection, new Event());
        }
    }

    public Composite createContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        enablerComposite = new Composite(container, SWT.NONE);
        enablerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        enablerComposite.setLayout(new GridLayout());

        chckEnableIntegration = new Button(enablerComposite, SWT.CHECK);
        chckEnableIntegration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        chckEnableIntegration.setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_ENABLE_INTEGRATION);

        mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glMainComposite = new GridLayout(1, false);
        glMainComposite.marginWidth = 0;
        glMainComposite.marginHeight = 0;
        mainComposite.setLayout(glMainComposite);

        createAuthenticationGroup();

        createSubmitOptionsGroup();

        createFetchCustomFieldGroup();

        return container;
    }

    private void createAuthenticationGroup() {
        Group grpAuthentication = new Group(mainComposite, SWT.NONE);
        grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpAuthentication = new GridLayout(2, false);
        glGrpAuthentication.horizontalSpacing = 15;
        grpAuthentication.setLayout(glGrpAuthentication);
        grpAuthentication.setText(ComposerJiraIntegrationMessageConstant.PREF_TITLE_AUTHENTICATION);

        Label lblServerUrl = new Label(grpAuthentication, SWT.NONE);
        lblServerUrl.setText(ComposerJiraIntegrationMessageConstant.PREF_LBL_SERVER_URL);

        txtServerUrl = new Text(grpAuthentication, SWT.BORDER);
        txtServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblUsername = new Label(grpAuthentication, SWT.NONE);
        lblUsername.setText(ComposerJiraIntegrationMessageConstant.PREF_LBL_USERNAME);

        txtUsername = new Text(grpAuthentication, SWT.BORDER);
        txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        linkApiToken = new Link(grpAuthentication, SWT.NONE);
        linkApiToken.setText(
                String.format("%s/<a href=\"%s\">%s</a>:", ComposerJiraIntegrationMessageConstant.PREF_LBL_PASSWORD,
                        API_TOKEN_DOCUMENT_URL, ComposerJiraIntegrationMessageConstant.PREF_LBL_API_TOKEN));

        Composite passwordComposite = new Composite(grpAuthentication, SWT.NONE);
        passwordComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glPassword = new GridLayout(1, false);
        glPassword.marginWidth = 0;
        glPassword.marginHeight = 0;
        passwordComposite.setLayout(glPassword);

        txtPassword = new Text(passwordComposite, SWT.BORDER);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        chckEncrypt = new Button(grpAuthentication, SWT.CHECK);
        chckEncrypt.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 2, 1));
        chckEncrypt.setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_ENCRYPT_PASSWORD);

        btnConnect = new Button(grpAuthentication, SWT.NONE);
        btnConnect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnConnect.setText(ComposerJiraIntegrationMessageConstant.PREF_LBL_CONNECT);
        new Label(grpAuthentication, SWT.NONE);
    }

    private void createSubmitOptionsGroup() {
        Group grpSubmitOptions = new Group(mainComposite, SWT.NONE);
        grpSubmitOptions.setText(ComposerJiraIntegrationMessageConstant.PREF_TITLE_SUBMIT_OPTIONS);
        grpSubmitOptions.setLayout(new GridLayout(1, false));
        grpSubmitOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite projectAndIssueComposite = new Composite(grpSubmitOptions, SWT.NONE);
        projectAndIssueComposite.setLayout(new GridLayout(2, false));
        projectAndIssueComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblDefaultJiraProject = new Label(projectAndIssueComposite, SWT.NONE);
        lblDefaultJiraProject.setText(ComposerJiraIntegrationMessageConstant.PREF_LBL_DF_JIRA_PROJECT);

        cbbProjects = new Combo(projectAndIssueComposite, SWT.READ_ONLY);
        cbbProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDefaultJiraIssue = new Label(projectAndIssueComposite, SWT.NONE);
        lblDefaultJiraIssue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDefaultJiraIssue.setText(ComposerJiraIntegrationMessageConstant.PREF_LBL_DF_JIRA_ISSUE_TYPE);

        cbbIssueTypes = new Combo(projectAndIssueComposite, SWT.READ_ONLY);
        cbbIssueTypes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite submitOptionsComposite = new Composite(grpSubmitOptions, SWT.NONE);
        submitOptionsComposite.setLayout(new GridLayout(1, false));
        submitOptionsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        chckUseTestCaseNameAsSumarry = new Button(submitOptionsComposite, SWT.CHECK);
        chckUseTestCaseNameAsSumarry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        chckUseTestCaseNameAsSumarry
                .setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_USE_TEST_CASE_NAME_AS_SUMMARY);

        chckAttachScreenshot = new Button(submitOptionsComposite, SWT.CHECK);
        chckAttachScreenshot.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        chckAttachScreenshot.setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_ATTACH_SCREENSHOT_TO_JIRA_TICKET);

        chckAttachLog = new Button(submitOptionsComposite, SWT.CHECK);
        chckAttachLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        chckAttachLog.setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_ATTACH_LOG_TO_JIRA_TICKET);

        chckAutoSubmitTestResult = new Button(submitOptionsComposite, SWT.CHECK);
        chckAutoSubmitTestResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        chckAutoSubmitTestResult.setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_AUTO_SUBMIT_TEST_RESULT);
    }

    private void createFetchCustomFieldGroup() {
        grpFetchOptions = new Group(mainComposite, SWT.NONE);
        grpFetchOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glGrpFetchOptions = new GridLayout(1, false);
        glGrpFetchOptions.horizontalSpacing = 15;
        grpFetchOptions.setLayout(glGrpFetchOptions);
        grpFetchOptions.setText(ComposerJiraIntegrationMessageConstant.PREF_TITLE_FETCH_OPTIONS);

        chckEnableFetchingContentFromJiraCloud = new Button(grpFetchOptions, SWT.CHECK);
        chckEnableFetchingContentFromJiraCloud.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
        chckEnableFetchingContentFromJiraCloud
                .setText(ComposerJiraIntegrationMessageConstant.PREF_CHCK_ENABLE_FETCHING_OPTIONS);

        Composite customFieldsComposite = new Composite(grpFetchOptions, SWT.FILL);
        GridLayout glCustomFieldsComposite = new GridLayout(4, false);
        customFieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        customFieldsComposite.setLayout(glCustomFieldsComposite);

        Label lblCustomField = new Label(customFieldsComposite, SWT.LEFT);
        lblCustomField.setText(ComposerJiraIntegrationMessageConstant.LBL_CUSTOM_FIELD);

        Composite helpComposite = new Composite(customFieldsComposite, SWT.NONE);
        helpComposite.setLayout(new GridLayout(1, false));
        new HelpCompositeForDialog(helpComposite, DOCUMENT_URL);

        cbbFields = new Combo(customFieldsComposite, SWT.NONE);
        cbbFields.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnFetchFields = new Button(customFieldsComposite, SWT.RIGHT);
        btnFetchFields.setText(ComposerJiraIntegrationMessageConstant.PREF_LBL_FETCH_CUSTOM_FIELDS);
    }

    public boolean okPressed() {
        try {
            settingStore.enableIntegration(chckEnableIntegration.getSelection());

            boolean encryptionEnable = chckEncrypt.getSelection();
            settingStore.saveEncryptionMigrated(true);
            settingStore.saveServerUrl(getTrimedValue(txtServerUrl), encryptionEnable);
            settingStore.saveUsername(getTrimedValue(txtUsername), encryptionEnable);
            settingStore.savePassword(txtPassword.getText(), encryptionEnable);
            settingStore.saveJiraUser(user);
            settingStore.enableEncryption(encryptionEnable);

            settingStore.enableUseTestCaseNameAsSummary(chckUseTestCaseNameAsSumarry.getSelection());
            settingStore.enableAttachScreenshot(chckAttachScreenshot.getSelection());
            settingStore.enableAttachLog(chckAttachLog.getSelection());
            settingStore.enableSubmitTestResultAutomatically(chckAutoSubmitTestResult.getSelection());

            displayedJiraProject.setDefaultObjectIndex(cbbProjects.getSelectionIndex());
            settingStore.saveStoredJiraProject(displayedJiraProject.getStoredObject());

            int issueTypeSelection = cbbIssueTypes.getSelectionIndex();
            if (issueTypeSelection >= 0 && allIssueTypes != null) {
                displayedJiraIssueType.setDefaultObjectIndex(issueTypeSelection);
                JiraIssueType selectedIssueType = displayedJiraIssueType.getStoredObject()
                        .getJiraObjects()[issueTypeSelection];

                StoredJiraObject<JiraIssueType> storedJiraIssueType = new StoredJiraObject<JiraIssueType>(
                        selectedIssueType.getSelf(), allIssueTypes.toArray(new JiraIssueType[0]));
                settingStore.saveStoredJiraIssueType(storedJiraIssueType);
            }

            settingStore.enableAddProjectScrope(projectScope);

            settingStore.enableFetchingContentFromJiraCloud(chckEnableFetchingContentFromJiraCloud.getSelection());

            displayedJiraField.setDefaultObjectIndex(cbbFields.getSelectionIndex());
            settingStore.saveStoredJiraCloudField(displayedJiraField.getStoredObject());

            settingStore.saveStore();
            return true;
        } catch (IOException | GeneralSecurityException | ResourceException | CryptoException e) {
            logger.error(ComposerJiraIntegrationMessageConstant.ERROR_UNABLE_TO_SAVE_JIRA_SETTING, e);
            MessageDialog.openError(mainComposite.getShell(), StringConstants.ERROR, e.getMessage());
            return false;
        }
    }

    private JiraCredential getEdittingCredential() {
        JiraCredential credential = new JiraCredential();
        credential.setServerUrl(getTrimedValue(txtServerUrl));
        credential.setUsername(getTrimedValue(txtUsername));
        credential.setPassword(txtPassword.getText());
        return credential;
    }

    private String getTrimedValue(Text text) {
        return StringUtils.defaultString(text.getText()).trim();
    }

    private void enableIntegrationComposite() {
        recursiveSetEnabled(mainComposite, chckEnableIntegration.getSelection());
    }

    private void enableFetchOptionsComposite(boolean enable) {
        String serverUrl = txtServerUrl.getText();
        boolean isJiraCloud = serverUrl.contains(".atlassian.net") || serverUrl.contains(".jira.com");
        recursiveSetEnabled(grpFetchOptions, enable && isJiraCloud);
    }
}
