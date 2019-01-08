package com.katalon.plugin.jira;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;
import com.katalon.plugin.jira.composer.toolbar.handler.ImportJiraJQLHandler;
import com.katalon.plugin.jira.core.constant.StringConstants;

public class JiraPluginMenuItemDescription implements ToolItemWithMenuDescription {

    @Override
    public String toolItemId() {
        return "com.katalon.plugin.jira.JiraPluginMenuItemDescription";
    }

    @Override
    public String name() {
        return "JIRA";
    }

    @Override
    public String iconUrl() {
        return "platform:/plugin/" + StringConstants.JIRA_BUNDLE_ID + "/icons/jira_active_32x24.png";
    }

    @Override
    public Menu getMenu(Control parent) {
        Menu menu = new Menu(parent);
        MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
        menuItem.setText("Import from JIRA JQL");
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ImportJiraJQLHandler importJiraJQLHandler = new ImportJiraJQLHandler();
                importJiraJQLHandler.execute(e.widget.getDisplay().getActiveShell());
            }
        });
        return menu;
    }
}
