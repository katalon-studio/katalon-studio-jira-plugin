package com.katalon.plugin.jira.core.issue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

public abstract class DefaultIssueHTMLLinkProvider implements IssueHTMLLinkProvider {

    protected IssueMetaDataProvider issueMetaData;

    public IssueMetaDataProvider getIssueMetaData() {
        return issueMetaData;
    }

    protected JiraIntegrationSettingStore settingStore;

    protected DefaultIssueHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord testCaseRecord,
            JiraIntegrationSettingStore settingStore) {
        this.settingStore = settingStore;
        this.issueMetaData = new IssueMetaDataProvider(testSuiteRecord, testCaseRecord);
    }

    protected DefaultIssueHTMLLinkProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord, int endStepIndex,
            JiraIntegrationSettingStore settingStore) {
        this.settingStore = settingStore;
        this.issueMetaData = new IssueMetaDataProvider(testSuiteRecord, logRecord, endStepIndex);
    }

    @Override
    public final String getHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return new URIBuilder(getIssueUrl()).addParameters(getIssueParameters()).build().toString();
    }

    protected List<NameValuePair> getIssueParameters() throws IOException {
        return Collections.emptyList();
    }

    @Override
    public String getLoginHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_LOGIN;
    }

    @Override
    public String getDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_DASHBOARD;
    }

    public String getSecureDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_SECURE;
    }
    
    @Override
    public String getDefaultDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException {
        return settingStore.getServerUrl(settingStore.isEncryptionEnabled()) + StringConstants.HREF_DEFAULT_DASHBOARD;
    }
}
