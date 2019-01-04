package com.katalon.plugin.jira.core.issue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

public class EditIssueHTMLLinkProvider extends DefaultIssueHTMLLinkProvider {
    private JiraIssue currentIssue;
    
    public EditIssueHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord testCaseRecord, JiraIntegrationSettingStore settingStore,
            JiraIssue jiraIssue) {
        super(testSuiteRecord, testCaseRecord, settingStore);
        this.currentIssue = jiraIssue;
    }

    @Override
    public String getIssueUrl() throws IOException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_EDIT_ISSUE;
    }

    @Override
    public List<NameValuePair> getIssueParameters() throws IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_ID, Long.toString(currentIssue.getId())));
        return pairs;
    }

    @Override
    public String getIssueUrlPrefix() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_EDIT_ISSUE_PREFIX;
    }
}
