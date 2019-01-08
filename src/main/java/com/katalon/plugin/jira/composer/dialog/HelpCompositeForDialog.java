package com.katalon.plugin.jira.composer.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class HelpCompositeForDialog extends HelpComposite {

    public HelpCompositeForDialog(Composite parent, String documentationUrl) {
        super(parent, documentationUrl);
    }
    
    @Override
    protected GridData createGridData() {
        return new GridData(SWT.LEFT, SWT.CENTER, true, false);
    }

    @Override
    protected GridLayout createLayout() {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginBottom = 5;
        return layout;
    }

}
