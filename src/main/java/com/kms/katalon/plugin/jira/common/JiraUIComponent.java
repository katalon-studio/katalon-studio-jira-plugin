package com.kms.katalon.plugin.jira.common;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import com.kms.katalon.plugin.jira.constants.StringConstants;
import com.kms.katalon.plugin.jira.entity.JiraIssue;

public interface JiraUIComponent extends JiraComponent {
    default String getHTMLIssueURLPrefix() throws IOException, GeneralSecurityException {
        return getSettingStore().getServerUrl(getSettingStore().isEncryptionEnabled())
                + StringConstants.HREF_BROWSE_ISSUE;
    }

    default URI getHTMLLink(JiraIssue jiraIssue) throws URISyntaxException, IOException, GeneralSecurityException {
        return new URI(getHTMLIssueURLPrefix() + "/" + jiraIssue.getKey());
    }

//    default int getTestCaseLogRecordIndex(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
//        return LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity).getChildIndex(logRecord);
//    }
//
//    default JiraIssueCollection getJiraIssueCollection(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
//        int index = getTestCaseLogRecordIndex(logRecord, reportEntity);
//        return getJiraIssueCollection(index, logRecord, reportEntity);
//    }
//
//    default void updateJiraReport(TestCaseLogRecord logRecord, JiraIssueCollection jiraIssueCollection,
//            ReportEntity reportEntity) throws JiraIntegrationException {
//        int index = getTestCaseLogRecordIndex(logRecord, reportEntity);
//        updateJiraReport(index, logRecord, jiraIssueCollection, reportEntity);
//    }
}
