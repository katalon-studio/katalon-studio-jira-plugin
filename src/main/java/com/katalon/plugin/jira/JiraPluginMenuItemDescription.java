package com.katalon.plugin.jira;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;
import com.katalon.platform.api.ui.DialogActionService;
import com.katalon.plugin.jira.composer.toolbar.handler.ImportJiraJQLHandler;
import com.katalon.plugin.jira.core.JiraComponent;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.util.PlatformUtil;

public class JiraPluginMenuItemDescription implements ToolItemWithMenuDescription, JiraComponent {

    private Menu menu;

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
    public void defaultEventHandler() {
        if (menu != null && !menu.isDisposed()) {
            menu.setVisible(true);
        }
    }

    @Override
    public Menu getMenu(Control parent) {
        menu = new Menu(parent);
        MenuItem importTestCaseMenuItem = new MenuItem(menu, SWT.PUSH);
        importTestCaseMenuItem.setText("Import Test Case from JIRA JQL");
        importTestCaseMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ImportJiraJQLHandler importJiraJQLHandler = new ImportJiraJQLHandler();
                importJiraJQLHandler.execute(e.widget.getDisplay().getActiveShell());
            }
        });

        MenuItem settingsTestCaseMenuItem = new MenuItem(menu, SWT.PUSH);
        settingsTestCaseMenuItem.setText("Settings");
        settingsTestCaseMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PlatformUtil.getUIService(DialogActionService.class)
                        .openPluginPreferencePage(StringConstants.JIRA_BUNDLE_ID);
            }
        });

        menu.addMenuListener(new MenuAdapter() {

            @Override
            public void menuShown(MenuEvent e) {

                try {
                    importTestCaseMenuItem.setEnabled(
                            PlatformUtil.getCurrentProject() != null && getSettingStore().isIntegrationEnabled());
                    settingsTestCaseMenuItem.setEnabled(PlatformUtil.getCurrentProject() != null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return menu;
    }
}
