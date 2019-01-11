package com.katalon.plugin.jira.composer.report;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.report.provider.JiraTestCaseIssueLabelProvider;

public class JiraTestCaseColumnDescription implements ReportIntegrationViewDescription.TestCaseColumnDescription {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public Image getColumnImage(Display display) {
        return ImageConstants.IMG_16_JIRA;
    }

    @Override
    public CellDecorator<TestCaseRecord> onCreateLabelProvider(ReportEntity reportEntity,
            TestSuiteRecord testSuiteRecord) {
        return new JiraTestCaseIssueLabelProvider(new JiraTestCaseRecordContext() {

            @Override
            public ReportEntity getReport() {
                return reportEntity;
            }

            @Override
            public TestSuiteRecord getTestSuiteRecord() {
                return testSuiteRecord;
            }

        });
    }
}
