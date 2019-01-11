package com.katalon.plugin.jira.composer.report.provider;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.report.JiraLinkedIssuesDialog;
import com.katalon.plugin.jira.composer.report.JiraTestCaseRecordContext;
import com.katalon.plugin.jira.core.JiraIntegrationException;

import ch.qos.logback.classic.Logger;

public class JiraTestCaseIssueLabelProvider implements JiraUIComponent, CellDecorator<TestCaseRecord> {

    private Logger logger = (Logger) LoggerFactory.getLogger(JiraTestCaseIssueLabelProvider.class);

    private JiraTestCaseRecordContext context;

    public JiraTestCaseIssueLabelProvider(JiraTestCaseRecordContext context) {
        this.context = context;
    }

    @Override
    public String getToolTip(TestCaseRecord record) {
        return ComposerJiraIntegrationMessageConstant.TOOLTIP_CLICK_TO_MANAGE_JIRA_ISSUES;
    }

    @Override
    public String getText(TestCaseRecord element) {
        return StringUtils.EMPTY;
    }

    @Override
    public Image getImage(TestCaseRecord record) {
        try {
            return getJiraIssueCollection(record, context.getReport()).getIssues().isEmpty()
                    ? ImageConstants.IMG_ISSUE_HOVER_OUT : ImageConstants.IMG_ISSUE_HOVER_IN;
        } catch (JiraIntegrationException e) {
            logger.error("", e);
            return null;
        }
    }

    @Override
    public Image getHoveredImage(TestCaseRecord record) {
        return ImageConstants.IMG_ISSUE_HOVER_IN;
    }

    @Override
    public void onMouseDownEvent(MouseEvent mouseEvent, TestCaseRecord testCaseEvent) {
        Shell activeShell = mouseEvent.display.getActiveShell();
        try {
            int index = context.getTestSuiteRecord().getTestCaseRecords().indexOf(testCaseEvent);
            JiraLinkedIssuesDialog dialog = new JiraLinkedIssuesDialog(activeShell,
                    getJiraIssueCollection(index, testCaseEvent, context.getReport()), context.getTestSuiteRecord(),
                    testCaseEvent);

            if (dialog.open() != JiraLinkedIssuesDialog.OK || !dialog.isChanged()) {
                return;
            }

            updateJiraReport(testCaseEvent, dialog.getJiraIssueCollection(), context.getReport());
        } catch (JiraIntegrationException ex) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
            logger.error("", ex);
        }
    }

    @Override
    public boolean showCursorOnHover(TestCaseRecord record) {
        return true;
    }
}
