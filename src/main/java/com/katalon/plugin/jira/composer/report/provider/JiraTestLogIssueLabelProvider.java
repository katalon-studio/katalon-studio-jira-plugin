package com.katalon.plugin.jira.composer.report.provider;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.report.TestStepRecord;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.report.JiraCreateIssueHandler;
import com.katalon.plugin.jira.composer.report.JiraTestStepRecordContext;
import com.katalon.plugin.jira.composer.report.dialog.progress.JiraIssueProgressResult;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.entity.JiraIssueCollection;

public class JiraTestLogIssueLabelProvider implements CellDecorator<TestStepRecord>, JiraUIComponent {

    private JiraTestStepRecordContext context;

    public JiraTestLogIssueLabelProvider(JiraTestStepRecordContext context) {
        this.context = context;
    }

    @Override
    public Image getImage(TestStepRecord record) {
        return isMainStep(record) ? ImageConstants.IMG_ISSUE_HOVER_OUT : null;
    }

    @Override
    public Image getHoveredImage(TestStepRecord record) {
        return isMainStep(record) ? ImageConstants.IMG_ISSUE_HOVER_IN : null;
    }

    @Override
    public String getToolTip(TestStepRecord record) {
        return isMainStep(record) ? ComposerJiraIntegrationMessageConstant.CLMN_TOOLTIP_CLICK_TO_QUICK_CREATE_ISSUE
                : null;
    }

    private boolean isMainStep(TestStepRecord stepRecord) {
        return context.getTestCaseRecord().getChildRecords().indexOf(stepRecord) >= 0;
    }

    @Override
    public void onMouseDownEvent(MouseEvent mouseEvent, TestStepRecord testStepLogRecord) {
        final Shell activeShell = mouseEvent.display.getActiveShell();
        Menu jiraIssueMenu = new Menu(activeShell);

        MenuItem newIssueItem = new MenuItem(jiraIssueMenu, SWT.PUSH);
        newIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_NEW_JIRA_ISSUE);
        newIssueItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JiraCreateIssueHandler handler = newCreateIssueHandler(activeShell);
                JiraIssueProgressResult result = handler.openNewIssueDialog(getIndexOfStepLogRecord(testStepLogRecord));
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
                JiraIssueProgressResult result = handler
                        .openCreateAsSubTaskDialog(getIndexOfStepLogRecord(testStepLogRecord));
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

        registerHiddenListener(mouseEvent, jiraIssueMenu);
    }

    private void addJiraIssueAndUpdateReport(Shell activeShell, JiraIssueProgressResult result) {
        try {
            JiraIssueCollection issueCollection = getJiraIssueCollection(context.getTestCaseRecord(),
                    context.getReportEntity());
            issueCollection.getIssues().add(result.getJiraIssue());
            updateJiraReport(context.getTestCaseRecord(), issueCollection, context.getReportEntity());
        } catch (JiraIntegrationException e) {
            MessageDialog.openError(activeShell, StringConstants.ERROR, e.getMessage());
        }
    }

    private void registerHiddenListener(MouseEvent mouseEvent, Menu jiraIssueMenu) {
        final Control control = (Control) mouseEvent.widget;
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

    private int getIndexOfStepLogRecord(TestStepRecord stepRecord) {
        return context.getTestCaseRecord().getChildRecords().indexOf(stepRecord) + 1;
    }

    private JiraCreateIssueHandler newCreateIssueHandler(final Shell activeShell) {
        return new JiraCreateIssueHandler(activeShell, context.getTestSuiteRecord(), context.getTestCaseRecord());
    }

    @Override
    public boolean showCursorOnHover(TestStepRecord stepRecord) {
        return isMainStep(stepRecord);
    }
}
