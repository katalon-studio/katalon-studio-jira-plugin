package com.katalon.plugin.jira.core.report;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.core.JiraComponent;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.constant.JiraIntegrationMessageConstants;
import com.katalon.plugin.jira.core.entity.JiraIssue;

import ch.qos.logback.classic.Logger;

public class JiraReportIntegration implements JiraComponent {

    Logger logger = (Logger) LoggerFactory.getLogger(JiraReportIntegration.class);

    private JiraReportService reportService = new JiraReportService();

    public void uploadTestSuiteResult(ReportEntity report, TestSuiteRecord suiteLog) throws Exception {
        logger.debug(JiraIntegrationMessageConstants.MSG_SEND_TEST_RESULT);

        List<TestCaseRecord> childRecords = suiteLog.getTestCaseRecords();
        File reportZipFile = null;
        for (int index = 0; index < childRecords.size(); index++) {
            TestCaseRecord child = childRecords.get(index);
            try {
                JiraIssue issue = JiraObjectToEntityConverter
                        .getJiraIssue(JiraComponent.getPlatformController(TestCaseController.class)
                                .getTestCase(JiraComponent.getCurrentProject(), child.getTestCaseId()));
                if (issue == null) {
                    continue;
                }
                if (reportZipFile == null || !reportZipFile.exists()) {
                    reportZipFile = reportService.zipReportFolder(new File(report.getFolderLocation()));
                }
                reportService.uploadTestCaseReport(child, issue, reportZipFile);
                logger.debug(MessageFormat.format(JiraIntegrationMessageConstants.MSG_SEND_TEST_RESULT_SENT,
                        issue.getKey()));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (reportZipFile != null && reportZipFile.exists()) {
            FileUtils.deleteQuietly(reportZipFile);
        }
    }
}
