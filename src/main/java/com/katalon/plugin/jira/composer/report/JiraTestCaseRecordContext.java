package com.katalon.plugin.jira.composer.report;

import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestSuiteRecord;

public interface JiraTestCaseRecordContext {
    ReportEntity getReport();

    TestSuiteRecord getTestSuiteRecord();
}
