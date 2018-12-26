package com.kms.katalon.plugin.jira.common;

import java.util.List;
import java.util.Optional;

import com.katalon.platform.api.model.HasIntegration;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.IntegrationType;
import com.katalon.platform.api.model.TestCase;
import com.kms.katalon.platform.entity.impl.IntegrationImpl;
import com.kms.katalon.plugin.jira.constants.StringConstants;
import com.kms.katalon.plugin.jira.entity.JiraIntegratedIssue;
import com.kms.katalon.plugin.jira.entity.JiraIntegratedObject;
import com.kms.katalon.plugin.jira.entity.JiraIssue;
import com.kms.katalon.plugin.jira.util.JsonUtil;

public class JiraObjectToEntityConverter {
    private static <T extends JiraIntegratedObject> Optional<T> getJiraObject(HasIntegration entity,
            Class<T> clazz) {
        List<Integration> integrations = entity.getIntegrations();
        if (integrations != null) {
            Integration jiraIntegration = integrations.stream()
                .filter(integration -> StringConstants.JIRA_NAME.equals(integration.getProductName()))
                .findAny()
                .orElse(null);
            if (jiraIntegration != null) {
                return Optional.of((T) JsonUtil
                        .fromJson(jiraIntegration.getProperties().get(StringConstants.INTEGRATED_VALUE_NAME), clazz));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

//    public static Optional<JiraReport> getOptionalJiraReport(ReportEntity report) {
//        return getJiraObject(report, JiraReport.class);
//    }
//
//    public static JiraReport getJiraReport(ReportEntity report) {
//        return getJiraObject(report, JiraReport.class).map(jiraReport -> jiraReport).orElse(new JiraReport());
//    }

    public static JiraIssue getJiraIssue(TestCase testCase) {
        return getJiraObject(testCase, JiraIntegratedIssue.class).map(integrated -> integrated.getJiraIssue())
                .orElse(null);
    }

    public static TestCase updateTestCase(JiraIssue issue, TestCase testCase)
            throws JiraIntegrationException {
        IntegrationImpl updatedIntegrationInfo = new IntegrationImpl();
        updatedIntegrationInfo.setProductName(StringConstants.JIRA_NAME);
        updatedIntegrationInfo.setType(IntegrationType.REPORT);
        updatedIntegrationInfo.setProperties(new JiraIntegratedIssue(issue).getIntegratedValue());
        
        Integration jiraIntegration = testCase.getIntegrations().stream()
                .filter(integration -> StringConstants.JIRA_NAME.equals(integration.getProductName()))
                .findAny()
                .orElse(null);
        if (jiraIntegration != null) {
            int index = testCase.getIntegrations().indexOf(jiraIntegration);
            testCase.getIntegrations().remove(index);
        }
        testCase.getIntegrations().add(Math.max(0, testCase.getIntegrations().size() - 1), updatedIntegrationInfo);
        return testCase;
    }
//
//    public static Optional<JiraIssueCollection> getOptionalJiraIssueCollection(ReportEntity report,
//            int testCaseLogIndex) {
//        return getOptionalJiraReport(report)
//                .map(jiraReport -> Optional.ofNullable(jiraReport.getIssueCollectionMap().get(testCaseLogIndex)))
//                .orElse(Optional.empty());
//    }
//
//    public static void updateJiraReport(JiraReport jiraReport, ReportEntity report) throws JiraIntegrationException {
//        IntegratedEntity jiraIntegratedEntity = report.getIntegratedEntity(StringConstants.JIRA_NAME);
//        if (jiraIntegratedEntity == null) {
//            jiraIntegratedEntity = new IntegratedEntity();
//            jiraIntegratedEntity.setProductName(StringConstants.JIRA_NAME);
//        }
//        jiraIntegratedEntity.setType(IntegratedType.REPORT);
//        jiraIntegratedEntity.setProperties(jiraReport.getIntegratedValue());
//        int index = report.getIntegratedEntities().indexOf(jiraIntegratedEntity);
//        if (index >= 0) {
//            report.getIntegratedEntities().remove(index);
//        }
//        report.getIntegratedEntities().add(Math.max(0, report.getIntegratedEntities().size() - 1),
//                jiraIntegratedEntity);
//        try {
//            ReportController.getInstance().updateReport(report);
//        } catch (Exception e) {
//            throw new JiraIntegrationException(e);
//        }
//    }
}
