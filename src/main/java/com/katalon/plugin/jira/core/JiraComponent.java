package com.katalon.plugin.jira.core;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.io.FileUtils;

import com.katalon.platform.api.exception.CryptoException;
import com.katalon.platform.api.exception.InvalidDataTypeFormatException;
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
            ProjectEntity currentProject = getCurrentProject();
            File oldSettingFile = new File(currentProject.getFolderLocation(),
                    "settings/internal/com.kms.katalon.integration.jira.properties");
            File newSettingFile = new File(currentProject.getFolderLocation(),
                    "settings/external/com.katalon.katalon-studio-jira-plugin.properties");
            if (oldSettingFile.exists() && !newSettingFile.exists()) {
                FileUtils.copyFile(oldSettingFile, newSettingFile);
            }
            PluginPreference pluginPreference = ApplicationManager.getInstance()
                    .getPreferenceManager()
                    .getPluginPreference(currentProject.getId(), StringConstants.JIRA_BUNDLE_ID);
            if (pluginPreference == null) {
                return null;
            }

            return new JiraIntegrationSettingStore(pluginPreference);
        } catch (ResourceException | IOException e) {
            return null;
        }
    }

    default JiraCredential getCredential() throws IOException, JiraIntegrationException {
        try {
            return getSettingStore().getJiraCredential();
        } catch (GeneralSecurityException | InvalidDataTypeFormatException | CryptoException e) {
            throw new JiraIntegrationException(e);
        }
    }

    default void updateJiraReport(int index, TestCaseRecord logRecord, JiraIssueCollection jiraIssueCollection,
            ReportEntity reportEntity) throws JiraIntegrationException {
        JiraReport jiraReport = JiraObjectToEntityConverter.getJiraReport(reportEntity);
        jiraReport.getIssueCollectionMap().put(index, jiraIssueCollection);
        JiraObjectToEntityConverter.updateJiraReport(jiraReport, reportEntity);
    }

    default JiraIssueCollection getJiraIssueCollection(int index, TestCaseRecord logRecord, ReportEntity reportEntity) {
        return JiraObjectToEntityConverter.getOptionalJiraIssueCollection(reportEntity, index)
                .map(jiraIssue -> jiraIssue)
                .orElse(new JiraIssueCollection(logRecord.getTestCaseId()));
    }
}
