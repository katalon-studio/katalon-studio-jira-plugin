package com.katalon.plugin.jira;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.osgi.service.event.Event;

import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.event.EventListener;
import com.katalon.platform.api.event.ExecutionEvent;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.execution.TestSuiteExecutionContext;
import com.katalon.platform.api.extension.EventListenerInitializer;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.core.JiraComponent;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.constant.JiraIntegrationMessageConstants;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.report.JiraReportService;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;
import com.katalon.plugin.jira.core.util.PlatformUtil;

public class JiraEventListenerInitializer implements EventListenerInitializer, JiraComponent {
    private ReportController reportController = PlatformUtil.getPlatformController(ReportController.class);
    private TestCaseController testCaseController = PlatformUtil.getPlatformController(TestCaseController.class);

    @Override
    public void registerListener(EventListener eventListener) {
        eventListener.on(Event.class, event -> {
            try {
                JiraIntegrationSettingStore preferences = getSettingStore();
                boolean isIntegrationEnabled = preferences.isIntegrationEnabled();
                if (!isIntegrationEnabled) {
                    return;
                }

                if (ExecutionEvent.TEST_SUITE_FINISHED_EVENT.equals(event.getTopic())) {
                    ExecutionEvent eventObject = (ExecutionEvent) event.getProperty("org.eclipse.e4.data");

                    updateExecutionResult((TestSuiteExecutionContext) eventObject
                            .getExecutionContext());
                }
            } catch (IOException | ResourceException ex) {
                ex.printStackTrace(System.err);
            }
        });
    }

    private void updateExecutionResult(TestSuiteExecutionContext testSuiteContext) throws ResourceException {
        JiraReportService reportService = new JiraReportService();
        ReportEntity report = reportController.getReport(getCurrentProject(), testSuiteContext.getReportId());
        System.out.println(JiraIntegrationMessageConstants.MSG_SEND_TEST_RESULT);

        TestSuiteRecord suiteLog = reportController.getTestSuiteRecord(getCurrentProject(), report);
        List<TestCaseRecord> childRecords = suiteLog.getTestCaseRecords();
        File reportZipFile = null;
        for (int index = 0; index < childRecords.size(); index++) {
            TestCaseRecord logRecord = childRecords.get(index);
            try {
                JiraIssue issue = JiraObjectToEntityConverter
                        .getJiraIssue(testCaseController.getTestCase(getCurrentProject(), logRecord.getTestCaseId()));
                if (issue == null) {
                    continue;
                }
                if (reportZipFile == null || !reportZipFile.exists()) {
                    reportZipFile = reportService.zipReportFolder(new File(report.getFileLocation()));
                }
                reportService.uploadTestCaseReport(logRecord, issue, reportZipFile);
                System.out.println(MessageFormat.format(JiraIntegrationMessageConstants.MSG_SEND_TEST_RESULT_SENT,
                        issue.getKey()));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        if (reportZipFile != null && reportZipFile.exists()) {
            FileUtils.deleteQuietly(reportZipFile);
        }
    }
}
