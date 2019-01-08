package com.katalon.plugin.jira.composer.toolbar.dialog;

import java.io.IOException;

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
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.ui.UISynchronizeService;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.dialog.AbstractDialog;
import com.katalon.plugin.jira.composer.preference.JiraPreferenceInitializer;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.entity.JiraFilter;
import com.katalon.plugin.jira.core.util.PlatformUtil;

import ch.qos.logback.classic.Logger;


public class ImportJiraJQLDialog extends AbstractDialog implements JiraUIComponent {
    private Logger logger = (Logger) LoggerFactory.getLogger(ImportJiraJQLDialog.class);

    private Text text;

    private JiraFilter filter;

    public ImportJiraJQLDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
        text.setText(JiraPreferenceInitializer.getLastEditedJQL(getCurrentProject()));
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
        lblJiraJql.setText(ComposerJiraIntegrationMessageConstant.DIA_LBL_JIRA_JQL);

        text = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        return container;
    }

    @Override
    public String getDialogTitle() {
        return ComposerJiraIntegrationMessageConstant.DIA_TITLE_IMPORT_FROM_JQL;
    }

    @Override
    protected void okPressed() {
        Job job = new Job(ComposerJiraIntegrationMessageConstant.DIA_JOB_FETCH_ISSUES) {
            private String jql = StringUtils.EMPTY;

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                    jql = text.getText();
                    ImportJiraJQLDialog.this.getButton(OK).setEnabled(false);
                });
                try {
                    filter = new JiraIntegrationAuthenticationHandler().getJiraFilterByJql(getCredential(), jql);
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        updateJQLForNextUsage(jql);
                        ImportJiraJQLDialog.super.okPressed();
                    });

                    return Status.OK_STATUS;
                } catch (JiraIntegrationException | IOException e) {
                    PlatformUtil.getUIService(UISynchronizeService.class).syncExec(() -> {
                        MessageDialog.openWarning(getShell(), StringConstants.WARN, e.getMessage());
                        ImportJiraJQLDialog.this.getButton(OK).setEnabled(true);
                    });
                    return Status.CANCEL_STATUS;
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void updateJQLForNextUsage(String jql) {
        try {
            JiraPreferenceInitializer.saveLastEditedJQL(jql, getCurrentProject());
        } catch (JiraIntegrationException e) {
            logger.error(e.getMessage(), e);
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
        return ComposerJiraIntegrationMessageConstant.DIA_DOCUMENT_URL_IMPORT_TEST_CASE_FROM_JIRA;
    }
}
