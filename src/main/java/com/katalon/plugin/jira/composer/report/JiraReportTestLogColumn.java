package com.katalon.plugin.jira.composer.report;

import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;

public class JiraReportTestLogColumn /* extends TestLogIntegrationColumn */ {

    public TestCaseRecord getTestCaseRecord() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public TestSuiteRecord getTestSuiteRecord() {
        // TODO Auto-generated method stub
        return null;
    }

    public ReportEntity getReportEntity() {
        // TODO Auto-generated method stub
        return null;
    }

//    public JiraReportTestLogColumn(ReportEntity reportEntity) {
//        super(reportEntity);
//    }
//
//    @Override
//    public ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex) {
//        TreeViewerColumn tableViewerColumnIntegration = new TreeViewerColumn((TreeViewer) tableViewer, SWT.NONE);
//        TreeColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
//        tableViewerColumnIntegration.setLabelProvider(new JiraTestLogIssueLabelProvider(columnIndex, this));
//        tblclmnTCIntegration.setImage(getProductImage());
//        return tableViewerColumnIntegration;
//    }
//
//    @Override
//    public Image getProductImage() {
//        return ImageConstants.IMG_16_JIRA;
//    }
    
}
