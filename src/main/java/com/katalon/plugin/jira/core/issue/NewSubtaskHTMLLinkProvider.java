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

public class NewSubtaskHTMLLinkProvider extends DefaultIssueHTMLLinkProvider {
    private JiraIssue parentIssue;

    public NewSubtaskHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord testCaseRecord,
            JiraIntegrationSettingStore settingStore, JiraIssue parentIssue) {
        super(testSuiteRecord, testCaseRecord, settingStore);
        this.parentIssue = parentIssue;
    }

    public NewSubtaskHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord, int endStepIndex,
            JiraIntegrationSettingStore settingStore, JiraIssue parentIssue) {
        super(testSuiteRecord, logRecord, endStepIndex, settingStore);
        this.parentIssue = parentIssue;
    }

    public String getIssueUrl() throws IOException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled())
                + StringConstants.HREF_CREATE_SUB_TASK_ISSUE;
    }

    @Override
    public String getIssueUrlPrefix() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled())
                + StringConstants.HREF_CREATE_SUB_TASK_ISSUE_PREFIX;
    }

    @Override
    public List<NameValuePair> getIssueParameters() throws IOException {
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair(JiraIssue.FIELD_PARENT_ISSUE_ID, Long.toString(parentIssue.getId())));
        return pairs;
    }

}
