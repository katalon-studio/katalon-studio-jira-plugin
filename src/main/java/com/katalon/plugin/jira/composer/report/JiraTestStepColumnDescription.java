package com.katalon.plugin.jira.composer.report;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.TestStepColumnDescription;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestStepRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.report.provider.JiraTestLogIssueLabelProvider;

public class JiraTestStepColumnDescription implements TestStepColumnDescription {

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Image getColumnImage(Display display) {
        return ImageConstants.IMG_16_JIRA;
    }

    @Override
    public CellDecorator<TestStepRecord> onCreateLabelProvider(ReportEntity reportEntity,
            TestSuiteRecord testSuiteRecord, TestCaseRecord testCaseRecord) {
        return new JiraTestLogIssueLabelProvider(new JiraTestStepRecordContext() {

            @Override
            public TestSuiteRecord getTestSuiteRecord() {
                return testSuiteRecord;
            }

            @Override
            public TestCaseRecord getTestCaseRecord() {
                return testCaseRecord;
            }

            @Override
            public ReportEntity getReportEntity() {
                return reportEntity;
            }
        });
    }

}
