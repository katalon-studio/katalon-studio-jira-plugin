package com.katalon.plugin.jira.composer.report.provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.ui.viewer.CellLayoutInfo;
import com.katalon.platform.ui.viewer.DefaultCellLayoutInfo;
import com.katalon.platform.ui.viewer.HoveredImageColumnLabelProvider;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.report.JiraReportTestCaseColumn;
import com.katalon.plugin.jira.core.JiraIntegrationException;
// import com.kms.katalon.tracking.service.Trackings;

import ch.qos.logback.classic.Logger;

public class JiraTestCaseIssueLabelProvider extends HoveredImageColumnLabelProvider<TestCaseRecord>
        implements JiraUIComponent {

    private Logger logger = (Logger) LoggerFactory.getLogger(JiraTestCaseIssueLabelProvider.class);

    private JiraReportTestCaseColumn view;

    public JiraTestCaseIssueLabelProvider(int columnIndex, JiraReportTestCaseColumn view) {
        super(columnIndex);
        this.view = view;
    }

    @Override
    public CellLayoutInfo getCellLayoutInfo() {
        return new DefaultCellLayoutInfo() {
            @Override
            public int getLeftMargin() {
                return 5;
            }
        };
    }

    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        TestCaseRecord logRecord = (TestCaseRecord) cell.getElement();
        Shell activeShell = e.display.getActiveShell();

        ReportEntity reportEntity = getReportEntity();

        // int index = getTestCaseRecordIndex(logRecord, reportEntity);
        // JiraLinkedIssuesDialog dialog = new JiraLinkedIssuesDialog(activeShell,
        // getJiraIssueCollection(index, logRecord, reportEntity), logRecord);
        // Trackings.trackOpenLinkedJiraIssuesDialog();
        // if (dialog.open() != JiraLinkedIssuesDialog.OK || !dialog.isChanged()) {
        // return;
        // }
        //
        // try {
        // updateJiraReport(logRecord, dialog.getJiraIssueCollection(), reportEntity);
        // } catch (JiraIntegrationException ex) {
        // MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
        // }
    }

    private ReportEntity getReportEntity() {
        return null;
        //return view.getReportEntity();
    }

    @Override
    protected String getElementToolTipText(TestCaseRecord element) {
        return ComposerJiraIntegrationMessageConstant.TOOLTIP_CLICK_TO_MANAGE_JIRA_ISSUES;
    }

    @Override
    protected Class<TestCaseRecord> getElementType() {
        return TestCaseRecord.class;
    }

    @Override
    protected Image getImage(TestCaseRecord logRecord) {
        try {
            boolean hasIssue = getJiraIssueCollection(logRecord, getReportEntity()).getIssues().isEmpty();

            return hasIssue ? ImageConstants.IMG_ISSUE_HOVER_OUT : getHoveredImage(logRecord);
        } catch (JiraIntegrationException e) {
            logger.error("", e);
            return null;
        }
    }

    @Override
    protected String getText(TestCaseRecord element) {
        return StringUtils.EMPTY;
    }

    @Override
    protected Image getHoveredImage(TestCaseRecord element) {
        return ImageConstants.IMG_ISSUE_HOVER_IN;
    }
}
