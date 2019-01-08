package com.katalon.plugin.jira.composer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class JiraProgressDialog extends ProgressMonitorDialog {

    public JiraProgressDialog(Shell parent) {
        super(parent);
    }

    protected void checkCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException();
        }
    }

    public abstract JiraProgressResult run();
}
