package com.kms.katalon.plugin.jira.ui;

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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.katalon.platform.api.PlatformServices;
import com.katalon.platform.api.dialogs.CommonDialogs;
import com.katalon.platform.api.model.Folder;
import com.katalon.platform.api.model.Project;
import com.katalon.platform.api.model.TestCase;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.service.FolderManager;
import com.katalon.platform.api.service.ProjectManager;
import com.katalon.platform.api.service.UISynchronizeService;
import com.kms.katalon.plugin.jira.common.JiraObjectToEntityConverter;
import com.kms.katalon.plugin.jira.constants.JiraIntegrationMessageConstants;
import com.kms.katalon.plugin.jira.entity.JiraIssue;

public class IssueSelectionDialog extends AbstractDialog {
    public static final int CLMN_IMPORTED_IDX = 0;

    public static final int CLMN_STATUS_IDX = 3;

    public static final int CLMN_SUMMARY_IDX = 2;

    public static final int CLMN_ISSUE_IDX = 1;
    
    private ProjectManager projectManager = ApplicationManager.getProjectManager();
    
    private FolderManager folderManager = ApplicationManager.getFolderManager();
    
    private CommonDialogs commonDialogsUtil = ApplicationManager.getCommonDialogs();
    
    private UISynchronizeService uiSynchronizeService = ApplicationManager.getUISynchronizeService();

    private List<JiraIssue> issues;

    private TableViewer issueViewer;

//    private TreeEntitySelectionComposite folderTreeComposite;

//    private FolderTreeEntity selectedFolder;

    private JiraImportedColumnLabelProvider importedIssueProvider;
    
    private Map<Long, JiraIssue> existedIssues;
    
    private Folder selectedFolder;

    public IssueSelectionDialog(Shell parentShell, List<JiraIssue> issues) {
        super(parentShell);
        this.issues = issues;
    }

    @Override
    protected void registerControlModifyListeners() {
//        TreeViewer treeViewer = folderTreeComposite.getTreeViewer();
//        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//
//            @Override
//            public void selectionChanged(SelectionChangedEvent event) {
//                selectedFolderChanged();
//            }
//        });
//
//        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
//            @Override
//            public void doubleClick(DoubleClickEvent event) {
//                Object firstElement = treeViewer.getStructuredSelection().getFirstElement();
//                treeViewer.setExpandedState(firstElement, !treeViewer.getExpandedState(firstElement));
//            }
//        });
    }

    private void refreshIssueTable() {
        Job job = new Job(JiraIntegrationMessageConstants.DIA_JOB_REFRESHING) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                uiSynchronizeService.syncExec(() -> getButton(OK).setEnabled(false));

                existedIssues = getExistedJiraIssues();
                importedIssueProvider.setExistedIssues(existedIssues);

                uiSynchronizeService.syncExec(() -> {
                    getButton(OK).setEnabled(!getSelectedIssues().isEmpty());

                    issueViewer.refresh();
                });
                return Status.OK_STATUS;
            }

        };
        job.schedule();
    }

    private void selectedFolderChanged() {
//        selectedFolder = (FolderTreeEntity) folderTreeComposite.getTreeViewer()
//                .getStructuredSelection()
//                .getFirstElement();
        refreshIssueTable();
    }

    @Override
    protected void setInput() {
        try {
            Project project = projectManager.getCurrentProject();
            Folder testCaseRoot = folderManager.getTestCaseRoot(project);
            selectedFolder = testCaseRoot;
//            FolderEntity testCaseRoot = FolderController.getInstance()
//                    .getTestCaseRoot(ProjectController.getInstance().getCurrentProject());
//
//            selectedFolder = TreeEntityUtil.createSelectedTreeEntityHierachy(testCaseRoot, testCaseRoot);
//            folderTreeComposite.setInput(new Object[] { selectedFolder });
//            folderTreeComposite.getTreeViewer().setSelection(new StructuredSelection(selectedFolder));
            selectedFolderChanged();

            issueViewer.setInput(issues);
        } catch (Exception e) {
//            LoggerSingleton.logError(e);
            //TODO log
        }
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        SashForm container = new SashForm(parent, SWT.NONE);

        createTestCaseFolderSelectionComposite(container);

        createIssueTableViewer(container);
        container.setWeights(new int[] { 3, 7 });

        return container;
    }

    private void createTestCaseFolderSelectionComposite(Composite container) {
        Composite folderSelectionComposite = new Composite(container, SWT.NONE);
        GridLayout leftCompositeLayout = new GridLayout(2, false);
        leftCompositeLayout.marginHeight = 0;
        folderSelectionComposite.setLayout(leftCompositeLayout);

        Label lblFolderSelection = new Label(folderSelectionComposite, SWT.NONE);
        lblFolderSelection.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        lblFolderSelection.setText(JiraIntegrationMessageConstants.DIA_LBL_CHOOSE_DESTINATION);
        
        Text txtFolderLocation = new Text(folderSelectionComposite, SWT.NONE);
        txtFolderLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        Button btnChooseFolder = new Button(folderSelectionComposite, SWT.NONE);
        btnChooseFolder.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        btnChooseFolder.setText(JiraIntegrationMessageConstants.BTN_CHOOSE_TEST_CASE_FOLDER);
        btnChooseFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    selectedFolder = commonDialogsUtil.showTestCaseFolderSelectionDialog(
                            Display.getCurrent().getActiveShell(), null);
                    selectedFolderChanged();
                } catch (Exception e1) {
                    // TODO log here
                }
            }
        });

//        EntityProvider contentProvider = new EntityProvider();
//        folderTreeComposite = new TreeEntitySelectionComposite(folderSelectionComposite, SWT.BORDER, contentProvider,
//                new FolderEntityTreeViewerFilter(contentProvider), new EntityLabelProvider());
//        folderTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        GridLayout treeLayout = new GridLayout(1, false);
//        treeLayout.marginWidth = 0;
//        treeLayout.marginHeight = 0;
//        folderTreeComposite.setLayout(treeLayout);
//
//        folderTreeComposite.getTreeViewer().setAutoExpandLevel(TreeViewer.ALL_LEVELS);
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
        tblclmnId.setText(JiraIntegrationMessageConstants.CM_ISSUE);
        tableViewerColumnId.setLabelProvider(new JiraIssueIDLabelProvider(CLMN_ISSUE_IDX));

        TableViewerColumn tableViewerColumnSummary = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnSummary = tableViewerColumnSummary.getColumn();
        tblclmnSummary.setWidth(250);
        tblclmnSummary.setText(JiraIntegrationMessageConstants.DIA_LBL_SUMMARY);
        tableViewerColumnSummary.setLabelProvider(new JiraImportedIssueLabelProvider(CLMN_SUMMARY_IDX));

        TableViewerColumn tableViewerColumnIssueType = new TableViewerColumn(issueViewer, SWT.NONE);
        TableColumn tblclmnType = tableViewerColumnIssueType.getColumn();
        tblclmnType.setWidth(150);
        tblclmnType.setText(JiraIntegrationMessageConstants.STATUS);
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

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public Map<Long, JiraIssue> getExistedJiraIssues() {
        try {
//            FolderEntity folder = selectedFolder.getObject();
            Map<Long, JiraIssue> existedIssues = new HashMap<>();
            List<TestCase> testCaseChildren = folderManager.getChildTestCases(selectedFolder);
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
        return new Point(850, 400);
    }

    @Override
    public String getDialogTitle() {
        return JiraIntegrationMessageConstants.DIA_TITLE_JIRA_ISSUES;
    }
}
