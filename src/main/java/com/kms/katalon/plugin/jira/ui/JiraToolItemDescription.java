package com.kms.katalon.plugin.jira.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ui.toolbar.ToolItemWithMenuDescription;
import com.kms.katalon.plugin.jira.toolbar.ImportJiraJQLHandler;

public class JiraToolItemDescription implements ToolItemWithMenuDescription {

    @Override
    public Menu getMenu(Control arg0) {
        Menu menu = new Menu(arg0);
        MenuItem mniImportTestCaseFromJira = new MenuItem(menu, SWT.PUSH);
        mniImportTestCaseFromJira.setText("Import Test Case From JIRA");
        mniImportTestCaseFromJira.setToolTipText("Import Test Case From JIRA");
        mniImportTestCaseFromJira.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ImportJiraJQLHandler handler = new ImportJiraJQLHandler();
                handler.execute();
            }
        });
        return menu;
    }
    
    @Override
    public String iconUrl() {
        return "platform:/plugin/com.katalon.katalon-studio-jira-plugin/icons/jira_active_32x32.png";
    }

    @Override
    public String name() {
        return "JIRA";
    }

    @Override
    public String toolItemId() {
        return "com.kms.katalon.plugin.jira.jiraToolItemWithMenu";
    }

}
