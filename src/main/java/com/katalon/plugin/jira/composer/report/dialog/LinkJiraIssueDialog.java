package com.katalon.plugin.jira.composer.report.dialog;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LinkJiraIssueDialog extends TitleAreaDialog {
    private Text txtJiraIssueKey;
    
    private String issueKey;
    
    private String title;
    private String message;
    private String label;

    public LinkJiraIssueDialog(Shell parentShell, String title, String message, String label) {
        super(parentShell);
        this.title = title;
        this.message = message;
        this.label = label;

        setTitle(title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        Composite mainComposite = new Composite(composite, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(2, false);
        glMainComposite.marginWidth = 10;
        glMainComposite.marginHeight = 10;
        glMainComposite.horizontalSpacing = 15;
        mainComposite.setLayout(glMainComposite);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label lblLinkJIRAIssue = new Label(mainComposite, SWT.NONE);
        lblLinkJIRAIssue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblLinkJIRAIssue.setText(label);

        txtJiraIssueKey = new Text(mainComposite, SWT.BORDER);
        txtJiraIssueKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        setMessage(message, IMessageProvider.INFORMATION);
        createControlModifyListener();
        return composite;
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(OK).setEnabled(false);
    }

    protected void createControlModifyListener() {
        txtJiraIssueKey.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getButton(OK).setEnabled(StringUtils.isNotEmpty(txtJiraIssueKey.getText()));
            }
        });
    }
    
    @Override
    protected void okPressed() {
        issueKey = txtJiraIssueKey.getText();
        super.okPressed();
    }

    public String getIssueKey() {
        return issueKey;
    }
}
