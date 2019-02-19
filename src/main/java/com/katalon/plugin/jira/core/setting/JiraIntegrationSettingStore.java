package com.katalon.plugin.jira.core.setting;

import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_AUTH_ENCRYPTION_ENABLED;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_AUTH_PASSWORD;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_AUTH_SERVER_URL;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_AUTH_USER;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_AUTH_USERNAME;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_INTEGRATION_ENABLED;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_ATTACH_LOG;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_ATTACH_SCREENSHOT;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_JIRA_ISSUE_TYPE;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_JIRA_PROJECT;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_BDD_FIELD;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY;
import static com.katalon.plugin.jira.core.constant.StringConstants.PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.rest.client.api.domain.User;
import com.google.gson.reflect.TypeToken;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.preference.PluginPreference;
import com.katalon.plugin.jira.core.JiraAPIURL;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.entity.JiraField;
import com.katalon.plugin.jira.core.entity.JiraIssueType;
import com.katalon.plugin.jira.core.entity.JiraProject;
import com.katalon.plugin.jira.core.util.JsonUtil;

public class JiraIntegrationSettingStore {

    private PluginPreference delegate;

    public JiraIntegrationSettingStore(PluginPreference pluginPreference) {
        this.delegate = pluginPreference;
    }

    public boolean isIntegrationEnabled() throws IOException {
        return delegate.getBoolean(PREF_INTEGRATION_ENABLED, false);
    }

    public void enableIntegration(boolean enabled) throws IOException {
        delegate.setBoolean(PREF_INTEGRATION_ENABLED, enabled);
    }

    public String getUsername(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return delegate.getString(PREF_AUTH_USERNAME, StringUtils.EMPTY);
    }

    public void saveUsername(String username, boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        delegate.setString(PREF_AUTH_USERNAME, username);
    }

    public String getPassword(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return delegate.getString(PREF_AUTH_PASSWORD, StringUtils.EMPTY);
    }

    public void savePassword(String rawPassword, boolean encryptEnabled) throws IOException, GeneralSecurityException {
        delegate.setString(PREF_AUTH_PASSWORD, rawPassword);
    }

    public boolean isEncryptionEnabled() throws IOException {
        return delegate.getBoolean(PREF_AUTH_ENCRYPTION_ENABLED, false);
    }

    public void enableEncryption(boolean enabled) throws IOException {
        delegate.setBoolean(PREF_AUTH_ENCRYPTION_ENABLED, enabled);
    }

    public String getServerUrl(boolean encryptionEnabled) throws IOException, GeneralSecurityException {
        return JiraAPIURL.removeLastSplash(delegate.getString(PREF_AUTH_SERVER_URL, StringUtils.EMPTY));
    }

    public void saveServerUrl(String serverUrl, boolean encryptionEnabled)
            throws IOException, GeneralSecurityException {
        delegate.setString(PREF_AUTH_SERVER_URL, serverUrl);
    }

    public boolean isUseTestCaseNameAsSummaryEnabled() throws IOException {
        return delegate.getBoolean(PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY, true);
    }

    public void enableUseTestCaseNameAsSummary(boolean enabled) throws IOException {
        delegate.setBoolean(PREF_SUBMIT_USE_TEST_CASE_NAME_AS_SUMMARY, enabled);
    }

    public boolean isAttachScreenshotEnabled() throws IOException {
        return delegate.getBoolean(PREF_SUBMIT_ATTACH_SCREENSHOT, true);
    }

    public void enableAttachScreenshot(boolean enabled) throws IOException {
        delegate.setBoolean(PREF_SUBMIT_ATTACH_SCREENSHOT, enabled);
    }

    public boolean isSubmitTestResultAutomatically() throws IOException {
        return delegate.getBoolean(PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY, true);
    }

    public void enableSubmitTestResultAutomatically(boolean enabled) throws IOException {
        delegate.setBoolean(PREF_SUBMIT_TEST_RESULT_AUTOMATICALLY, enabled);
    }

    public boolean isAttachLogEnabled() throws IOException {
        return delegate.getBoolean(PREF_SUBMIT_ATTACH_LOG, true);
    }

    public void enableAttachLog(boolean enabled) throws IOException {
        delegate.setBoolean(PREF_SUBMIT_ATTACH_LOG, enabled);
    }

    public StoredJiraObject<JiraProject> getStoredJiraProject() throws IOException {
        StoredJiraObject<JiraProject> instance = new StoredJiraObject<>(null, null);
        String objectAsString = delegate.getString(PREF_SUBMIT_JIRA_PROJECT, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraProject>>() {}.getType();
            StoredJiraObject<JiraProject> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraProject(StoredJiraObject<JiraProject> storedJiraProject) throws IOException {
        delegate.setString(PREF_SUBMIT_JIRA_PROJECT, JsonUtil.toJson(storedJiraProject, false));
    }
    
    public StoredJiraObject<JiraField> getStoredJiraField() throws IOException {
        StoredJiraObject<JiraField> instance = new StoredJiraObject<>(null, null);
        String objectAsString = delegate.getString(PREF_SUBMIT_BDD_FIELD, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraField>>() {}.getType();
            StoredJiraObject<JiraField> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraField(StoredJiraObject<JiraField> storedJiraProject) throws IOException {
        delegate.setString(PREF_SUBMIT_BDD_FIELD, JsonUtil.toJson(storedJiraProject, false));
    }

    public StoredJiraObject<JiraIssueType> getStoredJiraIssueType() throws IOException {
        StoredJiraObject<JiraIssueType> instance = new StoredJiraObject<>(null, null);
        String objectAsString = delegate.getString(PREF_SUBMIT_JIRA_ISSUE_TYPE, StringUtils.EMPTY);
        try {
            Type collectionType = new TypeToken<StoredJiraObject<JiraIssueType>>() {}.getType();
            StoredJiraObject<JiraIssueType> storedObject = JsonUtil.fromJson(objectAsString, collectionType);
            return storedObject != null ? storedObject : instance;
        } catch (IllegalArgumentException e) {
            return instance;
        }
    }

    public void saveStoredJiraIssueType(StoredJiraObject<JiraIssueType> storedJiraIssueType) throws IOException {
        delegate.setString(PREF_SUBMIT_JIRA_ISSUE_TYPE, JsonUtil.toJson(storedJiraIssueType, false));
    }

    public User getJiraUser() throws IOException {
        return JsonUtil.fromJson(delegate.getString(PREF_AUTH_USER, StringUtils.EMPTY), User.class);
    }

    public void saveJiraUser(User user) throws IOException {
        delegate.setString(PREF_AUTH_USER, JsonUtil.toJson(user, false));
    }

    public JiraCredential getJiraCredential() throws IOException, GeneralSecurityException {
        JiraCredential credential = new JiraCredential();

        boolean authenticationEncrypted = isEncryptionEnabled();
        credential.setServerUrl(getServerUrl(authenticationEncrypted));
        credential.setUsername(getUsername(authenticationEncrypted));
        credential.setPassword(getPassword(authenticationEncrypted));
        return credential;
    }
    
    public void saveStore() throws ResourceException {
        delegate.save();
    }
}
