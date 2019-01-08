package com.katalon.plugin.jira.composer.report.provider;

import java.util.Arrays;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.LogRecord;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.ui.viewer.CellLayoutInfo;
import com.katalon.platform.ui.viewer.DefaultCellLayoutInfo;
import com.katalon.platform.ui.viewer.HoveredImageColumnLabelProvider;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.report.JiraCreateIssueHandler;
import com.katalon.plugin.jira.composer.report.JiraReportTestLogColumn;
import com.katalon.plugin.jira.composer.report.dialog.progress.JiraIssueProgressResult;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.entity.JiraIssueCollection;

public class JiraTestLogIssueLabelProvider extends HoveredImageColumnLabelProvider<LogRecord>
        implements JiraUIComponent {

    private JiraReportTestLogColumn view;

    public JiraTestLogIssueLabelProvider(int columnIndex, JiraReportTestLogColumn view) {
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

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        final Shell activeShell = e.display.getActiveShell();
        Menu jiraIssueMenu = new Menu(activeShell);

        MenuItem newIssueItem = new MenuItem(jiraIssueMenu, SWT.PUSH);
        newIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_NEW_JIRA_ISSUE);
        newIssueItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JiraCreateIssueHandler handler = newCreateIssueHandler(activeShell);
                JiraIssueProgressResult result = handler.openNewIssueDialog(getIndexOfStepLogRecord(cell));
                if (handler.checkResult(result)) {
                    addJiraIssueAndUpdateReport(activeShell, result);
                }
            }
        });

        MenuItem newIssueAsSubTaskItem = new MenuItem(jiraIssueMenu, SWT.PUSH);
        newIssueAsSubTaskItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_AS_SUB_TASK);
        newIssueAsSubTaskItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JiraCreateIssueHandler handler = newCreateIssueHandler(activeShell);
                JiraIssueProgressResult result = handler.openCreateAsSubTaskDialog(getIndexOfStepLogRecord(cell));
                if (handler.checkResult(result)) {
                    addJiraIssueAndUpdateReport(activeShell, result);
                }
            }
        });

        MenuItem linkIssueItem = new MenuItem(jiraIssueMenu, SWT.PUSH);
        linkIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_LINK_TO_JIRA_ISSUE);
        linkIssueItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JiraCreateIssueHandler handler = newCreateIssueHandler(activeShell);
                JiraIssueProgressResult result = handler.openLinkIssueDialog();
                if (handler.checkResult(result)) {
                    addJiraIssueAndUpdateReport(activeShell, result);
                }
            }
        });

        registerHiddenListener(cell, jiraIssueMenu);
    }

    private JiraCreateIssueHandler newCreateIssueHandler(final Shell activeShell) {
        return new JiraCreateIssueHandler(activeShell, view.getTestSuiteRecord(), view.getTestCaseRecord());
    }

    private void addJiraIssueAndUpdateReport(Shell activeShell, JiraIssueProgressResult result) {
        try {
            TestCaseRecord testCaseLogRecord = view.getTestCaseRecord();
            ReportEntity reportEntity = view.getReportEntity();
            JiraIssueCollection issueCollection = getJiraIssueCollection(testCaseLogRecord, reportEntity);
            issueCollection.getIssues().add(result.getJiraIssue());
            updateJiraReport(testCaseLogRecord, issueCollection, reportEntity);
        } catch (JiraIntegrationException e) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, e.getMessage());
        }
    }

    private void registerHiddenListener(ViewerCell cell, Menu jiraIssueMenu) {
        final Control control = cell.getControl();
        Menu oldMenu = control.getMenu();
        control.setMenu(jiraIssueMenu);
        jiraIssueMenu.setVisible(true);
        jiraIssueMenu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuHidden(MenuEvent e) {
                control.setMenu(oldMenu);
                jiraIssueMenu.removeMenuListener(this);
            }
        });
    }

    private int getIndexOfStepLogRecord(ViewerCell cell) {
        return Arrays.asList(view.getTestCaseRecord().getChildRecords()).indexOf(cell.getElement()) + 1;
    }

    @Override
    protected Class<LogRecord> getElementType() {
        return LogRecord.class;
    }

    @Override
    protected Image getImage(LogRecord element) {
        return isMainStep(element) ? ImageConstants.IMG_ISSUE_HOVER_OUT : null;
    }

    @Override
    protected Image getHoveredImage(LogRecord element) {
        return isMainStep(element) ? ImageConstants.IMG_ISSUE_HOVER_IN : null;
    }

    @Override
    protected String getText(LogRecord element) {
        return StringUtils.EMPTY;
    }

    private boolean isMainStep(LogRecord logRecord) {
        return (logRecord instanceof TestCaseRecord)
                && ObjectUtils.equals(view.getTestCaseRecord(), 
                        view.getTestCaseRecord().getChildRecords().contains(logRecord));
    }

    @Override
    protected boolean shouldShowCursor(ViewerCell cell, Point currentMouseLocation) {
        return super.shouldShowCursor(cell, currentMouseLocation) && isMainStep((LogRecord) cell.getElement());
    }

    @Override
    protected String getElementToolTipText(LogRecord element) {
        return isMainStep(element) ? ComposerJiraIntegrationMessageConstant.CLMN_TOOLTIP_CLICK_TO_QUICK_CREATE_ISSUE
                : null;
    }
}
