package com.kms.katalon.plugin.jira.setting;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.rest.client.api.domain.User;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.plugin.jira.api.JiraAPIURL;
import com.kms.katalon.plugin.jira.api.JiraCredential;
import com.kms.katalon.plugin.jira.constants.StringConstants;
import com.kms.katalon.plugin.jira.entity.JiraIssueType;
import com.kms.katalon.plugin.jira.entity.JiraProject;
import com.kms.katalon.plugin.jira.util.JsonUtil;

public class JiraIntegrationSettingStore extends BundleSettingStore {

    public JiraIntegrationSettingStore(String projectDir) {
        super(projectDir, StringConstants.JIRA_PLUGIN_BUNDLE_ID, false);
    }

    public boolean isIntegrationEnabled() throws IOException {
        return getBoolean(StringConstants.PREF_INTEGRATION_ENABLED, false);
    }

    public void enableIntegration(boolean enabled) throws IOException {
        setProperty(StringConstants.PREF_INTEGRATION_ENABLED, enabled);
    }

    public String getUsername(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(StringConstants.PREF_AUTH_USERNAME, StringUtils.EMPTY, encryptionEnabled);
    }

    public void saveUsername(String username, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(StringConstants.PREF_AUTH_USERNAME, username, encryptionEnabled);
    }

    public String getPassword(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return getStringProperty(StringConstants.PREF_AUTH_PASSWORD, StringUtils.EMPTY, encryptionEnabled);
    }

    public void savePassword(String rawPassword, boolean encryptEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(StringConstants.PREF_AUTH_PASSWORD, rawPassword, encryptEnabled);
    }

    public boolean isEncryptionEnabled() throws IOException {
        return getBoolean(StringConstants.PREF_AUTH_ENCRYPTION_ENABLED, false);
    }

    public void enableEncryption(boolean enabled) throws IOException {
        setProperty(StringConstants.PREF_AUTH_ENCRYPTION_ENABLED, enabled);
    }

    public String getServerUrl(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        String decryptedServerUrl = getStringProperty(StringConstants.PREF_AUTH_SERVER_URL, StringUtils.EMPTY, encryptionEnabled);
        return JiraAPIURL.removeLastSplash(decryptedServerUrl);
    }

    public void saveServerUrl(String serverUrl, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        setStringProperty(StringConstants.PREF_AUTH_SERVER_URL, serverUrl, encryptionEnabled);
    }

    public boolean isUseTestCaseNameAsSummaryEnabled() throws IOException {
        return getBoolean(StringConstants.PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY, true);
    }

    public void enableUseTestCaseNameAsSummary(boolean enabled) throws IOException {
        setProperty(StringConstants.PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY, enabled);
    }

    public boolean isAttachScreenshotEnabled() throws IOException {
        return getBoolean(StringConstants.PREF_SUBMIT_ATTACH_SCREENSHOT, true);
    }

    public void enableAttachScreenshot(boolean enabled) throws IOException {
        setProperty(StringConstants.PREF_SUBMIT_ATTACH_SCREENSHOT, enabled);
    }

    public boolean isSubmitTestResultAutomatically() throws IOException {
        return getBoolean(StringConstants.PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY, true);
    }

    public void enableSubmitTestResultAutomatically(boolean enabled) throws IOException {
        setProperty(StringConstants.PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY, enabled);
    }

    public boolean isAttachLogEnabled() throws IOException {
        return getBoolean(StringConstants.PREF_SUBMIT_ATTACH_LOG, true);
    }

    public void enableAttachLog(boolean enabled) throws IOException {
        setProperty(StringConstants.PREF_SUBMIT_ATTACH_LOG, enabled);
    }

    public StoredJiraObject<JiraProject> getStoredJiraProject() throws IOException {
        StoredJiraObject<JiraProject> instance = new StoredJiraObject<>(null, null);
        String objectAsString = getString(StringConstants.PREF_SUBMIT_JIRA_PROJECT, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraProject>>() {}.getType();
            StoredJiraObject<JiraProject> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraProject(StoredJiraObject<JiraProject> storedJiraProject) throws IOException {
        setProperty(StringConstants.PREF_SUBMIT_JIRA_PROJECT, JsonUtil.toJson(storedJiraProject, false));
    }

    public StoredJiraObject<JiraIssueType> getStoredJiraIssueType() throws IOException {
        StoredJiraObject<JiraIssueType> instance = new StoredJiraObject<>(null, null);
        String objectAsString = getString(StringConstants.PREF_SUBMIT_JIRA_ISSUE_TYPE, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraIssueType>>() {}.getType();
            StoredJiraObject<JiraIssueType> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraIssueType(StoredJiraObject<JiraIssueType> storedJiraIssueType) throws IOException {
        setProperty(StringConstants.PREF_SUBMIT_JIRA_ISSUE_TYPE, JsonUtil.toJson(storedJiraIssueType, false));
    }

    public User getJiraUser() throws IOException {
        return JsonUtil.fromJson(getString(StringConstants.PREF_AUTH_USER, StringUtils.EMPTY), User.class);
    }

    public void saveJiraUser(User user) throws IOException {
        setProperty(StringConstants.PREF_AUTH_USER, JsonUtil.toJson(user, false));
    }

    public JiraCredential getJiraCredential() throws IOException, GeneralSecurityException {
        JiraCredential credential = new JiraCredential();

        boolean authenticationEncrypted = isEncryptionEnabled();
        credential.setServerUrl(getServerUrl(authenticationEncrypted));
        credential.setUsername(getUsername(authenticationEncrypted));
        credential.setPassword(getPassword(authenticationEncrypted));
        return credential;
    }
    
    public String getLastEditedJQL() throws GeneralSecurityException, IOException {
        return getStringProperty(StringConstants.PREF_LAST_EDITED_JIRA_JQL, StringUtils.EMPTY, false);
    }
    
    public void saveLastEditedJQL(String jql) throws GeneralSecurityException, IOException {
        setStringProperty(StringConstants.PREF_LAST_EDITED_JIRA_JQL, jql, false);
    }
}
