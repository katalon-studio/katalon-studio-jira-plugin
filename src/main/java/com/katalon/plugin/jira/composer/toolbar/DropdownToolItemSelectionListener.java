package com.katalon.plugin.jira.composer.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public abstract class DropdownToolItemSelectionListener extends SelectionAdapter {
    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.detail == SWT.ARROW) {
            showDropdown(event);
            return;
        }
        centerWigetSelected(event);
    }

    protected final void showDropdown(SelectionEvent event) {
        Widget widget = event.widget;
        if (!(widget instanceof ToolItem)) {
            return;
        }
        
        ToolItem item = (ToolItem) widget;
        Rectangle rect = item.getBounds();
        Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
        Menu menu = getMenu();
        if (menu == null || menu.isDisposed()) {
            return;
        }
        menu.setLocation(pt.x, pt.y + rect.height);
        menu.setVisible(true);
    }

    protected abstract Menu getMenu();

    protected void centerWigetSelected(SelectionEvent event) {
        showDropdown(event);
    }
}

