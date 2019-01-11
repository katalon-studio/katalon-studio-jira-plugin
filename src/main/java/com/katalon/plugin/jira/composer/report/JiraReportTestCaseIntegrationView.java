package com.katalon.plugin.jira.composer.report;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.TestCaseRecordIntegrationView;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.ImageConstants;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.report.dialog.progress.JiraIssueProgressResult;
import com.katalon.plugin.jira.composer.report.provider.JiraIssueIDLabelProvider;
import com.katalon.plugin.jira.composer.report.provider.JiraIssueLabelProvider;
import com.katalon.plugin.jira.composer.toolbar.DropdownToolItemSelectionListener;
import com.katalon.plugin.jira.composer.util.ControlUtil;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.entity.JiraIssueCollection;
import com.katalon.plugin.jira.core.util.PlatformUtil;

public class JiraReportTestCaseIntegrationView implements JiraUIComponent, TestCaseRecordIntegrationView {

    public static final int CLMN_ID_IDEX = 0;

    public static final int CLMN_SUMMARY_IDEX = 1;

    public static final int CLMN_STATUS_IDEX = 2;

    private TableViewer tableViewer;

    private JiraIssueCollection jiraIssueCollection;

    private ToolItem tltmRemove, tltmEdit;

    private TestCaseRecord logRecord;

    private JiraCreateIssueHandler createIssueHandler;

    private Composite container;

    private ReportEntity reportEntity;

    private TestSuiteRecord suiteRecord;

    @Override
    public Control onCreateView(Composite parent, TestSuiteRecord testSuiteRecord, TestCaseRecord testCaseRecord)
            throws ResourceException {
        this.reportEntity = PlatformUtil.getPlatformController(ReportController.class)
                .getReport(PlatformUtil.getCurrentProject(), testSuiteRecord.getReportId());
        this.suiteRecord = testSuiteRecord;

        this.logRecord = testCaseRecord;
        return createContainer(parent);
    }

    // @Override
    public Composite createContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        createToolBar(container);

        createTableCompiste(container);

        registerControlModifyListeners();

        enableContainer();

        changeTestCase(logRecord);

        return container;
    }

    // @Override
    public void changeTestCase(TestCaseRecord testCaseLogRecord) {
        logRecord = testCaseLogRecord;
        enableContainer();
        if (logRecord == null) {
            return;
        }

        setInput(testCaseLogRecord);
    }

    private void setInput(TestCaseRecord testCaseLogRecord) {
        try {
            int index = suiteRecord.getTestCaseRecords().indexOf(testCaseLogRecord);
            Optional<JiraIssueCollection> optionalJiraIssueCollection = JiraObjectToEntityConverter
                    .getOptionalJiraIssueCollection(reportEntity, index);
            jiraIssueCollection = new JiraIssueCollection(testCaseLogRecord.getTestCaseId());
            if (optionalJiraIssueCollection.isPresent()) {
                jiraIssueCollection = optionalJiraIssueCollection.get();
            }
            tableViewer.setInput(jiraIssueCollection.getIssues());
            ReportController reportController = PlatformUtil.getPlatformController(ReportController.class);
            createIssueHandler = new JiraCreateIssueHandler(getShell(),
                    reportController.getTestSuiteRecord(PlatformUtil.getCurrentProject(), reportEntity), logRecord);
        } catch (ResourceException ex) {
            MessageDialog.openError(getShell(), StringConstants.ERROR, ex.getMessage());
        }
    }

    private void enableContainer() {
        ControlUtil.recursiveSetEnabled(container, logRecord != null);
    }

    // @Override
    public void createTableContextMenu(Menu parentMenu, ISelection selection) {
    }

    protected void registerControlModifyListeners() {
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection structuredSelection = tableViewer.getStructuredSelection();
                tltmRemove.setEnabled(!structuredSelection.isEmpty());
                tltmEdit.setEnabled(structuredSelection.size() == 1);
            }
        });

        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                jiraIssueCollection.getIssues().removeAll(tableViewer.getStructuredSelection().toList());
                tableViewer.refresh();

                saveJiraReport();
            }
        });

        tltmEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openEditIssueDialog();
            }
        });
    }

    private Shell getShell() {
        return container.getShell();
    }

    private void createTableCompiste(Composite composite) {
        Composite issueTableComposite = new Composite(composite, SWT.NONE);
        issueTableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableColumnLayout tableLayout = new TableColumnLayout();
        issueTableComposite.setLayout(tableLayout);

        tableViewer = new TableViewer(issueTableComposite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn tableViewerColumnID = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnID = tableViewerColumnID.getColumn();
        tblclmnID.setText(StringConstants.ID);
        tableViewerColumnID.setLabelProvider(new JiraIssueIDLabelProvider(CLMN_ID_IDEX));
        tableLayout.setColumnData(tblclmnID, new ColumnWeightData(20, 100));

        TableViewerColumn tableViewerColumnSummary = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnSummary = tableViewerColumnSummary.getColumn();
        tblclmnSummary.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_SUMMARY);
        tableViewerColumnSummary.setLabelProvider(new JiraIssueLabelProvider(CLMN_SUMMARY_IDEX));
        tableLayout.setColumnData(tblclmnSummary, new ColumnWeightData(50, 150));

        TableViewerColumn tableViewerColumnStatus = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnStatus = tableViewerColumnStatus.getColumn();
        tblclmnStatus.setText(StringConstants.STATUS);
        tableViewerColumnStatus.setLabelProvider(new JiraIssueLabelProvider(CLMN_STATUS_IDEX));
        tableLayout.setColumnData(tblclmnStatus, new ColumnWeightData(20, 150));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
    }

    private void createToolBar(Composite composite) {
        ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolItem tltmAdd = new ToolItem(toolBar, SWT.DROP_DOWN);
        tltmAdd.setText(StringConstants.ADD);
        tltmAdd.setImage(ImageConstants.IMG_16_ADD);
        tltmAdd.addSelectionListener(new DropdownToolItemSelectionListener() {

            @Override
            protected Menu getMenu() {
                Menu addMenu = new Menu(getShell());

                MenuItem newIssueItem = new MenuItem(addMenu, SWT.PUSH);
                newIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_NEW_JIRA_ISSUE);
                newIssueItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        openNewIssueDialog();
                    }
                });

                MenuItem newAsSubTaskItem = new MenuItem(addMenu, SWT.PUSH);
                newAsSubTaskItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_CREATE_AS_SUB_TASK);
                newAsSubTaskItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        openCreateAsSubTaskDialog();
                    }
                });

                MenuItem linkIssueItem = new MenuItem(addMenu, SWT.PUSH);
                linkIssueItem.setText(ComposerJiraIntegrationMessageConstant.DIA_ITEM_LINK_TO_JIRA_ISSUE);
                linkIssueItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        openLinkIssueDialog();
                    }
                });
                return addMenu;
            }
        });

        tltmEdit = new ToolItem(toolBar, SWT.NONE);
        tltmEdit.setImage(ImageConstants.IMG_16_EDIT);
        tltmEdit.setText(StringConstants.EDIT);
        tltmEdit.setEnabled(false);

        tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.setImage(ImageConstants.IMG_16_REMOVE);
        tltmRemove.setText(StringConstants.REMOVE);
        tltmRemove.setEnabled(false);
    }

    private int getNumSteps() {
        return logRecord.getChildRecords().size();
    }

    private void openEditIssueDialog() {
        JiraIssue oldIssue = (JiraIssue) tableViewer.getStructuredSelection().getFirstElement();
        JiraIssueProgressResult handlerResult = createIssueHandler.openEditIssueDialog(oldIssue);
        if (createIssueHandler.checkResult(handlerResult)) {
            Collections.replaceAll(jiraIssueCollection.getIssues(), oldIssue, handlerResult.getJiraIssue());
            tableViewer.refresh();
        }
    }

    public void openCreateAsSubTaskDialog() {
        JiraIssueProgressResult handlerResult = createIssueHandler.openCreateAsSubTaskDialog(getNumSteps());
        checkResultAndUpdateTable(handlerResult);
    }

    public void openLinkIssueDialog() {
        JiraIssueProgressResult handlerResult = createIssueHandler.openLinkIssueDialog();
        if (!createIssueHandler.checkResult(handlerResult)) {
            return;
        }
        JiraIssue newJiraIssue = handlerResult.getJiraIssue();
        addIssueToCollection(newJiraIssue);
        refreshAndSetSelection(newJiraIssue);
    }

    private JiraIssue addIssueToCollection(JiraIssue newIssue) {
        int index = indexInCollection(newIssue, jiraIssueCollection);
        List<JiraIssue> issues = jiraIssueCollection.getIssues();
        if (index >= 0) {
            issues.remove(index);
            issues.add(index, newIssue);
        } else {
            issues.add(newIssue);
        }
        return newIssue;
    }

    private int indexInCollection(JiraIssue newIssue, JiraIssueCollection issueCollection) {
        List<JiraIssue> listIssues = issueCollection.getIssues();
        for (int index = 0; index < listIssues.size(); index++) {
            if (newIssue.getKey().equals(listIssues.get(index).getKey())) {
                return index;
            }
        }
        return -1;
    }

    public void openNewIssueDialog() {
        JiraIssueProgressResult handlerResult = createIssueHandler.openNewIssueDialog(getNumSteps());
        checkResultAndUpdateTable(handlerResult);
    }

    private void checkResultAndUpdateTable(JiraIssueProgressResult result) {
        if (createIssueHandler.checkResult(result)) {
            JiraIssue newJiraIssue = result.getJiraIssue();
            jiraIssueCollection.getIssues().add(newJiraIssue);
            refreshAndSetSelection(newJiraIssue);
        }
    }

    private void refreshAndSetSelection(JiraIssue newJiraIssue) {
        tableViewer.refresh();
        tableViewer.setSelection(new StructuredSelection(newJiraIssue));

        saveJiraReport();
    }

    public JiraIssueCollection getJiraIssueCollection() {
        return jiraIssueCollection;
    }

    private void saveJiraReport() {
        try {
            updateJiraReport(logRecord, jiraIssueCollection, reportEntity);
        } catch (JiraIntegrationException e) {
            MessageDialog.openError(container.getShell(), StringConstants.ERROR, e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "JIRA";
    }
}
