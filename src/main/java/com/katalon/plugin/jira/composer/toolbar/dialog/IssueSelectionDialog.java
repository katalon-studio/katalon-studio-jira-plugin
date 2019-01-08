package com.katalon.plugin.jira.composer.toolbar.dialog;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.controller.FolderController;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.ui.UISynchronizeService;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.dialog.AbstractDialog;
import com.katalon.plugin.jira.composer.report.provider.JiraIssueIDLabelProvider;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.util.PlatformUtil;

import ch.qos.logback.classic.Logger;

public class IssueSelectionDialog extends AbstractDialog {
    private Logger logger = (Logger) LoggerFactory.getLogger(IssueSelectionDialog.class);

    public static final int CLMN_IMPORTED_IDX = 0;

    public static final int CLMN_STATUS_IDX = 3;

    public static final int CLMN_SUMMARY_IDX = 2;

    public static final int CLMN_ISSUE_IDX = 1;

    private List<JiraIssue> issues;

    private TableViewer issueViewer;

    private FolderEntity selectedFolder;

    private JiraImportedColumnLabelProvider importedIssueProvider;

    private Map<Long, JiraIssue> existedIssues;

    public IssueSelectionDialog(Shell parentShell, FolderEntity selectedFolder, List<JiraIssue> issues) {
        super(parentShell);
        this.issues = issues;
        this.selectedFolder = selectedFolder;
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    private void refreshIssueTable() {
        Job job = new Job(ComposerJiraIntegrationMessageConstant.DIA_JOB_REFRESHING) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> getButton(OK).setEnabled(false));

                existedIssues = getExistedJiraIssues();
                importedIssueProvider.setExistedIssues(existedIssues);

                PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                    getButton(OK).setEnabled(!getSelectedIssues().isEmpty());

                    issueViewer.refresh();
                });
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    @Override
    protected void setInput() {
        try {
            issueViewer.setInput(issues);
            refreshIssueTable();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        createIssueTableViewer(container);

        return container;
    }

    private void createIssueTableViewer(Composite container) {
        issueViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = issueViewer.getTable();
        table.setHeaderVisible(true);

        TableViewerColumn tableViewerColumnImported = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnImported = tableViewerColumnImported.getColumn();
        tblclmnImported.setWidth(30);
        importedIssueProvider = new JiraImportedColumnLabelProvider(CLMN_IMPORTED_IDX);
        tableViewerColumnImported.setLabelProvider(importedIssueProvider);

        TableViewerColumn tableViewerColumnId = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnId = tableViewerColumnId.getColumn();
        tblclmnId.setWidth(80);
        tblclmnId.setText(ComposerJiraIntegrationMessageConstant.CM_ISSUE);
        tableViewerColumnId.setLabelProvider(new JiraIssueIDLabelProvider(CLMN_ISSUE_IDX));

        TableViewerColumn tableViewerColumnSummary = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnSummary = tableViewerColumnSummary.getColumn();
        tblclmnSummary.setWidth(250);
        tblclmnSummary.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_SUMMARY);
        tableViewerColumnSummary.setLabelProvider(new JiraImportedIssueLabelProvider(CLMN_SUMMARY_IDX));

        TableViewerColumn tableViewerColumnIssueType = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnType = tableViewerColumnIssueType.getColumn();
        tblclmnType.setWidth(150);
        tblclmnType.setText(StringConstants.STATUS);
        tableViewerColumnIssueType.setLabelProvider(new JiraImportedIssueLabelProvider(CLMN_STATUS_IDX));
        issueViewer.setContentProvider(ArrayContentProvider.getInstance());
        table.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(issueViewer, ToolTip.NO_RECREATE);
    }

    public List<JiraIssue> getSelectedIssues() {
        return issues.parallelStream()
                .filter(issue -> !existedIssues.containsKey(issue.getId()))
                .collect(Collectors.toList());
    }

    public Map<Long, JiraIssue> getExistedJiraIssues() {
        try {
            Map<Long, JiraIssue> existedIssues = new HashMap<>();
            List<TestCaseEntity> testCaseChildren = PlatformUtil.getPlatformController(FolderController.class)
                    .getChildTestCases(PlatformUtil.getCurrentProject(), selectedFolder)
                    .stream()
                    .filter(e -> e instanceof TestCaseEntity)
                    .map(e -> (TestCaseEntity) e)
                    .collect(Collectors.toList());
            testCaseChildren.forEach(child -> {
                JiraIssue jiraIssue = JiraObjectToEntityConverter.getJiraIssue(child);
                if (jiraIssue != null) {
                    existedIssues.put(jiraIssue.getId(), jiraIssue);
                }
            });
            return existedIssues;
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }

    @Override
    public String getDialogTitle() {
        return ComposerJiraIntegrationMessageConstant.DIA_TITLE_JIRA_ISSUES;
    }
}
