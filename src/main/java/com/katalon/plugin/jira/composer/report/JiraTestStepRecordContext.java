package com.katalon.plugin.jira.composer.report;

import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;

public interface JiraTestStepRecordContext {
    ReportEntity getReportEntity();
    
    TestSuiteRecord getTestSuiteRecord();
    
    TestCaseRecord getTestCaseRecord();
}
