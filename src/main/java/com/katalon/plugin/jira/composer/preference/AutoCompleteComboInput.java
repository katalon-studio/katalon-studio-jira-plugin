package com.katalon.plugin.jira.composer.preference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;

public class AutoCompleteComboInput {
    private Combo combo;

    public AutoCompleteComboInput(Combo combo) {
        this.combo = combo;
    }

    public Combo build() {
        combo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                Combo cmb = ((Combo) keyEvent.getSource());
                setClosestMatch(cmb);
            }

            // Move the highlight back by one character for backspace
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.keyCode == SWT.BS) {
                    Combo cmb = ((Combo) keyEvent.getSource());
                    Point pt = cmb.getSelection();
                    cmb.setSelection(new Point(Math.max(0, pt.x - 1), pt.y));
                }
            }
        });
        return combo;
    }

    private void setClosestMatch(Combo combo) {
        String str = combo.getText();
        String[] cItems = combo.getItems();
        // Find Item in Combo Items. If full match returns index
        int index = -1;
        for (int i = 0; i < cItems.length; i++) {
            if (cItems[i].toLowerCase().startsWith(str.toLowerCase())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            Point pt = combo.getSelection();
            combo.select(index);
            combo.setText(cItems[index]);
            combo.setSelection(new Point(pt.x, cItems[index].length()));
        } else {
            combo.setText("");
        }
    }
}
