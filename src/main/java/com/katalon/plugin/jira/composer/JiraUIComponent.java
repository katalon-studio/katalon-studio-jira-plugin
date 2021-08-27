package com.katalon.plugin.jira.composer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.Arrays;

import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.core.JiraComponent;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.entity.JiraIssueCollection;
import com.katalon.plugin.jira.core.util.PlatformUtil;

public interface JiraUIComponent extends JiraComponent {
    default String getHTMLIssueURLPrefix() throws IOException, GeneralSecurityException {
        return getSettingStore().getServerUrl(getSettingStore().isEncryptionEnabled())
                + StringConstants.HREF_BROWSE_ISSUE;
    }

    default URI getHTMLLink(JiraIssue jiraIssue) throws URISyntaxException, IOException, GeneralSecurityException {
        return new URI(getHTMLIssueURLPrefix() + "/" + jiraIssue.getKey());
    }

    default JiraIssueCollection getJiraIssueCollection(TestCaseRecord logRecord, ReportEntity reportEntity)
            throws JiraIntegrationException {
        int index = logRecord.getRecordIndex();
        return getJiraIssueCollection(index, logRecord, reportEntity);
    }

    default void updateJiraReport(TestCaseRecord logRecord, JiraIssueCollection jiraIssueCollection,
            ReportEntity reportEntity) throws JiraIntegrationException {
        int index = logRecord.getRecordIndex();
        if (index < 0) {
            throw new JiraIntegrationException(MessageFormat.format(
                    ComposerJiraIntegrationMessageConstant.ERROR_GET_JIRA_ISSUE_WITH_WRONG_TEST_CASE_INDEX,
                    logRecord.getName(), index));
        }
        updateJiraReport(index, logRecord, jiraIssueCollection, reportEntity);
    }
}
