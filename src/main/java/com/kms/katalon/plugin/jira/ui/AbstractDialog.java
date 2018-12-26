package com.kms.katalon.plugin.jira.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractDialog extends Dialog {

    protected Composite mainComposite;

    private String dialogTitle = StringUtils.EMPTY;

    private boolean hasSeparator;

    public AbstractDialog(Shell parentShell) {
        this(parentShell, true);
    }
    
    public AbstractDialog(Shell parentShell, boolean hasSeparator) {
        super(parentShell);
        this.hasSeparator = hasSeparator;
    }

    /**
     * Creates parent container that includes child container and horizontal line.
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        mainComposite = (Composite) super.createDialogArea(parent);
        mainComposite.setLayout(new GridLayout(1, false));

        Composite mainContainer = new Composite(mainComposite, SWT.NONE);
        mainContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
        mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createDialogContainer(mainContainer);

        if (hasSeparator) {
            Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        }

        return mainComposite;
    }

    @Override
    public void create() {
        super.create();
        setInput();
        registerControlModifyListeners();
    }

    /**
     * Used for children can register event listener of their control after creating them.
     */
    protected abstract void registerControlModifyListeners();

    /**
     * Used for children set value to their control after creating them.
     */
    protected abstract void setInput();

    /**
     * Let children create its control.
     * 
     * @param parent main container.
     * @return main area of children.
     */
    protected abstract Control createDialogContainer(Composite parent);

    /**
     * Creates shell without focusing to another shell ({@link SWT.PRIMARY_MODAL}), can resize, can close and has title.
     */
    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(arg | SWT.PRIMARY_MODAL | SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    /**
     * Set title for dialog
     * 
     * @param dialogTitle
     */
    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    /**
     * Get dialog title
     * 
     * @return Title of the dialog.
     */
    public String getDialogTitle() {
        return this.dialogTitle;
    }
    
    /**
     * @return if this dialog have available documentation
     */
    protected boolean hasDocumentation() {
        return false;
    }

    /**
     * Get documentation for this dialog
     * @return return empty for default
     */
    protected String getDocumentationUrl() {
        return "";
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
        if (!hasDocumentation()) {
            return super.createButtonBar(parent);
        }
        Composite bottomComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        bottomComposite.setLayout(layout);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Composite helpComposite = new Composite(bottomComposite, SWT.NONE);
        helpComposite.setLayout(new GridLayout(1, false));
        helpComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
//        new HelpCompositeForDialog(helpComposite, getDocumentationUrl());
        
        super.createButtonBar(bottomComposite);

        return bottomComposite;
    }
}
