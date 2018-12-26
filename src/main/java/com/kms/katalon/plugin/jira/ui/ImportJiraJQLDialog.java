package com.kms.katalon.plugin.jira.ui;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.katalon.platform.api.model.Project;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.service.UISynchronizeService;
import com.kms.katalon.plugin.jira.api.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.plugin.jira.common.JiraIntegrationException;
import com.kms.katalon.plugin.jira.common.JiraUIComponent;
import com.kms.katalon.plugin.jira.constants.JiraIntegrationMessageConstants;
import com.kms.katalon.plugin.jira.entity.JiraFilter;
import com.kms.katalon.plugin.jira.setting.JiraIntegrationSettingStore;

public class ImportJiraJQLDialog extends AbstractDialog implements JiraUIComponent {
    private Text text;

    private JiraFilter filter;
    
    private JiraIntegrationSettingStore settingStore;

    public ImportJiraJQLDialog(Shell parentShell) {
        super(parentShell);
        Project project = ApplicationManager.getProjectManager().getCurrentProject();
        settingStore = new JiraIntegrationSettingStore(project.getFolderLocation());
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
        String lastEditedJQL;
        try {
            lastEditedJQL = settingStore.getLastEditedJQL();
        } catch (GeneralSecurityException | IOException e) {
            //TODO log
            lastEditedJQL = StringUtils.EMPTY;
        }
        text.setText(lastEditedJQL);
        text.setFocus();
        text.selectAll();
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout containerLayout = new GridLayout(2, false);
        containerLayout.horizontalSpacing = 15;
        container.setLayout(containerLayout);

        Label lblJiraJql = new Label(container, SWT.NONE);
        lblJiraJql.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        lblJiraJql.setText(JiraIntegrationMessageConstants.DIA_LBL_JIRA_JQL);

        text = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        return container;
    }

    @Override
    public String getDialogTitle() {
        return JiraIntegrationMessageConstants.DIA_TITLE_IMPORT_FROM_JQL;
    }

    @Override
    protected void okPressed() {
        Job job = new Job(JiraIntegrationMessageConstants.DIA_JOB_FETCH_ISSUES) {
            private String jql = StringUtils.EMPTY;
            private UISynchronizeService uiSynchronizeService = ApplicationManager.getUISynchronizeService();

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                uiSynchronizeService.syncExec(() -> {
                    jql = text.getText();
                    ImportJiraJQLDialog.this.getButton(OK).setEnabled(false);
                });
                try {
                    filter = new JiraIntegrationAuthenticationHandler().getJiraFilterByJql(getCredential(), jql);
                    uiSynchronizeService.syncExec(() -> {
                        updateJQLForNextUsage(jql);
                        ImportJiraJQLDialog.super.okPressed();
                    });

                    return Status.OK_STATUS;
                } catch (JiraIntegrationException | IOException e) {
                    uiSynchronizeService.syncExec(() -> {
                        MessageDialog.openWarning(getShell(), JiraIntegrationMessageConstants.WARN, e.getMessage());
                        ImportJiraJQLDialog.this.getButton(OK).setEnabled(true);
                    });
                    //LoggerSingleton.logError(e);
                    //TODO log
                    return Status.CANCEL_STATUS;
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void updateJQLForNextUsage(String jql) {
        try {
            settingStore.saveLastEditedJQL(jql);
        } catch (IOException | GeneralSecurityException e) {
//            LoggerSingleton.logError(e);
            //TODO log
        }
    }

    public JiraFilter getFilter() {
        return filter;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 250);
    }
    
    @Override
    protected boolean hasDocumentation() {
        return true;
    }
    
    @Override
    protected String getDocumentationUrl() {
        return JiraIntegrationMessageConstants.DIA_DOCUMENT_URL_IMPORT_TEST_CASE_FROM_JIRA;
    }
}
