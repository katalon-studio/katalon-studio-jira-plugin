package com.katalon.plugin.jira.core.issue;

import java.io.IOException;
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

public class NewIssueHTMLLinkProvider extends DefaultIssueHTMLLinkProvider {

    public NewIssueHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord,
            JiraIntegrationSettingStore settingStore) {
        super(testSuiteRecord, logRecord, settingStore);
    }

    public NewIssueHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord, int endStepIndex,
            JiraIntegrationSettingStore settingStore) {
        super(testSuiteRecord, logRecord, endStepIndex, settingStore);
    }

    public String getIssueUrl() throws IOException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_CREATE_ISSUE;
    }

    @Override
    public String getIssueUrlPrefix() throws IOException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_CREATE_ISSUE_PREFIX;
    }

    public List<NameValuePair> getIssueParameters() throws IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_PROJECT_ID,
                Long.toString(settingStore.getStoredJiraProject().getDefaultJiraObject().getId())));
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_ISSUE_TYPE,
                Long.toString(settingStore.getStoredJiraIssueType().getDefaultJiraObject().getId())));
//        if (settingStore.isUseTestCaseNameAsSummaryEnabled()) {
//            pairs.add(new BasicNameValuePair(JiraIssue.FIELD_SUMMARY, issueMetaData.getSummary()));
//        }
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_REPORTER, settingStore.getJiraUser().getName()));
//        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_DESCRIPTION, issueMetaData.getDescription()));
//        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_ENVIRONMENT, issueMetaData.getEnvironment()));
        return pairs;
    }
}
