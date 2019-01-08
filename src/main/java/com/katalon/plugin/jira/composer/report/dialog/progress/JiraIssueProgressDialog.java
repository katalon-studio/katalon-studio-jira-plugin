package com.katalon.plugin.jira.composer.report.dialog.progress;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.JiraProgressDialog;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraInvalidURLException;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.report.JiraReportService;

public abstract class JiraIssueProgressDialog extends JiraProgressDialog implements JiraUIComponent {
    protected TestCaseRecord logRecord;

    protected TestSuiteRecord testSuiteRecord;

    protected String issueKey;

    private JiraReportService reportService;

    public JiraIssueProgressDialog(Shell parent, String issueKey, TestSuiteRecord testSuiteRecord,
            TestCaseRecord logRecord) {
        super(parent);
        this.testSuiteRecord = testSuiteRecord;
        this.logRecord = logRecord;
        this.issueKey = issueKey;
        this.reportService = new JiraReportService();
    }

    protected void uploadTestCaseLog(TestCaseRecord logRecord, JiraIssue issue)
            throws IOException, JiraIntegrationException {
        reportService.uploadTestCaseLog(testSuiteRecord, logRecord, issue);
    }

    protected void linkWithTestCaseJiraIssue(TestCaseRecord logRecord, JiraIssue issue)
            throws JiraIntegrationException, IOException {
        reportService.linkIssues(logRecord, issue);
    }

    protected void retrieveJiraIssue(JiraIntegrationAuthenticationHandler handler, JiraIssueProgressResult result)
            throws IOException, JiraIntegrationException {
        try {
            JiraIssue jiraIssue = handler.getJiraIssue(getCredential(), issueKey);
            result.setJiraIssue(jiraIssue);
        } catch (JiraInvalidURLException e) {
            throw new JiraIntegrationException(ComposerJiraIntegrationMessageConstant.JOB_MSG_INVALID_JIRA_ISSUE_KEY);
        }
    }
}
