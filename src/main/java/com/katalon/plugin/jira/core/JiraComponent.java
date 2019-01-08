package com.katalon.plugin.jira.core;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.preference.PluginPreference;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.entity.JiraIssueCollection;
import com.katalon.plugin.jira.core.entity.JiraReport;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

public interface JiraComponent {

    default ProjectEntity getCurrentProject() {
        return ApplicationManager.getInstance().getProjectManager().getCurrentProject();
    }

    default JiraIntegrationSettingStore getSettingStore() {
        try {
            PluginPreference pluginPreference = ApplicationManager.getInstance().getPreferenceManager().getPluginPreference(getCurrentProject().getId(),
                    StringConstants.JIRA_BUNDLE_ID);
            return pluginPreference != null ? new JiraIntegrationSettingStore(pluginPreference) : null;
        } catch (ResourceException e) {
            return null;
        }
    }

    default JiraCredential getCredential() throws IOException, JiraIntegrationException {
        try {
            return getSettingStore().getJiraCredential();
        } catch (GeneralSecurityException e) {
            throw new JiraIntegrationException(e);
        }
    }

    default void updateJiraReport(int index, TestCaseRecord logRecord, JiraIssueCollection jiraIssueCollection,
            ReportEntity reportEntity) throws JiraIntegrationException {
        JiraReport jiraReport = JiraObjectToEntityConverter.getJiraReport(reportEntity);
        jiraReport.getIssueCollectionMap().put(index, jiraIssueCollection);
        JiraObjectToEntityConverter.updateJiraReport(jiraReport, reportEntity);
    }

    default JiraIssueCollection getJiraIssueCollection(int index, TestCaseRecord logRecord,
            ReportEntity reportEntity) {
        return JiraObjectToEntityConverter.getOptionalJiraIssueCollection(reportEntity, index)
                .map(jiraIssue -> jiraIssue)
                .orElse(new JiraIssueCollection(logRecord.getTestCaseId()));
    }
}
