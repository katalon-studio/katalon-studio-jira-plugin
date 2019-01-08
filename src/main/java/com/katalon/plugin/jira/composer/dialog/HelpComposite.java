package com.katalon.plugin.jira.composer.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

public class HelpComposite extends Composite {

    public HelpComposite(Composite parent, String documentationUrl) {
        this(parent, documentationUrl, SWT.NONE);
    }

    public HelpComposite(Composite parent, String documentationUrl, int style) {
        super(parent, style);
        init(parent, documentationUrl);
    }

    protected void init(Composite parent, String documentationUrl) {
        setLayout(createLayout());
        setLayoutData(createGridData());
        ToolBar toolBar = new ToolBar(this, SWT.FLAT);
        Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
        toolBar.setCursor(cursor);
        toolBar.addDisposeListener(e -> cursor.dispose());
        new HelpToolItem(toolBar, documentationUrl);
    }

    protected GridData createGridData() {
        return new GridData(SWT.RIGHT, SWT.TOP, true, false);
    }

    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }
}
