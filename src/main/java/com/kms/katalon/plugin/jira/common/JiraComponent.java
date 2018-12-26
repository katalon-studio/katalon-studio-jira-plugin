package com.kms.katalon.plugin.jira.common;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.katalon.platform.api.PlatformServices;
import com.katalon.platform.api.model.Project;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.plugin.jira.api.JiraCredential;
import com.kms.katalon.plugin.jira.setting.JiraIntegrationSettingStore;

public interface JiraComponent {
    default Project getCurrentProject() {
        return ApplicationManager.getProjectManager().getCurrentProject();
    }

    default JiraIntegrationSettingStore getSettingStore() {
        return new JiraIntegrationSettingStore(getCurrentProject().getFolderLocation());
    }

    default JiraCredential getCredential() throws IOException, JiraIntegrationException {
        try {
            return getSettingStore().getJiraCredential();
        } catch (GeneralSecurityException e) {
            throw new JiraIntegrationException(e);
        }
    }

//    default void updateJiraReport(int index, TestCaseLogRecord logRecord, JiraIssueCollection jiraIssueCollection,
//            ReportEntity reportEntity) throws JiraIntegrationException {
//        JiraReport jiraReport = JiraObjectToEntityConverter.getJiraReport(reportEntity);
//        jiraReport.getIssueCollectionMap().put(index, jiraIssueCollection);
//        JiraObjectToEntityConverter.updateJiraReport(jiraReport, reportEntity);
//    }
//
//    default JiraIssueCollection getJiraIssueCollection(int index, TestCaseLogRecord logRecord, ReportEntity reportEntity) {
//        return JiraObjectToEntityConverter
//                .getOptionalJiraIssueCollection(reportEntity, index)
//                .map(jiraIssue -> jiraIssue)
//                .orElse(new JiraIssueCollection(logRecord.getId()));
//    }
//}
}
