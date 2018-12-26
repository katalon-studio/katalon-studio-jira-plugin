package com.kms.katalon.plugin.jira.setting;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * This class codes of {@link PreferencePage} with modifications to createControl
 * method because the original class contains private fields.
 * This class should not be re-factored.
 */
public abstract class PreferencePageWithHelp extends PreferencePage {

    /**
     * Preference store, or <code>null</code>.
     */
    private IPreferenceStore preferenceStore;

    /**
     * Valid state for this page; <code>true</code> by default.
     *
     * @see #isValid
     */
    private boolean isValid = true;

    /**
     * Body of page.
     */
    private Control body;

    /**
     * Whether this page has the standard Apply button; <code>true</code> by
     * default.
     *
     * @see #noDefaultAndApplyButton
     */
    private boolean createApplyButton = true;

    /**
     * Whether this page has the standard Default button; <code>true</code> by
     * default.
     *
     * @see #noDefaultButton
     */
    private boolean createDefaultButton = true;

    /**
     * Standard Defaults button, or <code>null</code> if none.
     * This button has id <code>DEFAULTS_ID</code>.
     */
    private Button defaultsButton = null;

    /**
     * The container this preference page belongs to; <code>null</code>
     * if none.
     */
    private IPreferencePageContainer container = null;

    /**
     * Standard Apply button, or <code>null</code> if none.
     * This button has id <code>APPLY_ID</code>.
     */
    private Button applyButton = null;

    /**
     * Description label.
     *
     * @see #createDescriptionLabel(Composite)
     */
    private Label descriptionLabel;

    /**
     * Caches size of page.
     */
    private Point size = null;

    /**
     * Creates a new preference page with an empty title and no image.
     */
    protected PreferencePageWithHelp() {
        this(""); //$NON-NLS-1$
    }

    /**
     * Creates a new preference page with the given title and no image.
     *
     * @param title the title of this preference page
     */
    protected PreferencePageWithHelp(String title) {
        super(title);
    }

    /**
     * Creates a new abstract preference page with the given title and image.
     *
     * @param title the title of this preference page
     * @param image the image for this preference page,
     * or <code>null</code> if none
     */
    protected PreferencePageWithHelp(String title, ImageDescriptor image) {
        super(title, image);
    }

    /**
     * Computes the size for this page's UI control.
     * <p>
     * The default implementation of this <code>IPreferencePage</code>
     * method returns the size set by <code>setSize</code>; if no size
     * has been set, but the page has a UI control, the framework
     * method <code>doComputeSize</code> is called to compute the size.
     * </p>
     *
     * @return the size of the preference page encoded as
     * <code>new Point(width,height)</code>, or
     * <code>(0,0)</code> if the page doesn't currently have any UI component
     */
    @Override
    public Point computeSize() {
        if (size != null) {
            return size;
        }
        Control control = getControl();
        if (control != null) {
            size = doComputeSize();
            return size;
        }
        return new Point(0, 0);
    }

    /**
     * The <code>PreferencePage</code> implementation of this
     * <code>IDialogPage</code> method creates a description label
     * and button bar for the page. It calls <code>createContents</code>
     * to create the custom contents of the page.
     * <p>
     * If a subclass that overrides this method creates a <code>Composite</code>
     * that has a layout with default margins (for example, a <code>GridLayout</code>)
     * it is expected to set the margins of this <code>Layout</code> to 0 pixels.
     * 
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        setControl(content);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        content.setLayout(layout);
        // Apply the font on creation for backward compatibility
        applyDialogFont(content);

        // initialize the dialog units
        initializeDialogUnits(content);

        descriptionLabel = createDescriptionLabel(content);
        if (descriptionLabel != null) {
            descriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        body = createContents(content);
        if (body != null) {
            // null is not a valid return value but support graceful failure
            body.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

//        Composite buttonBar = (hasDocumentation()) ? createButtonBarCompositeWithHelp(content)
//                : createDefaultButtonBarComposite(content);
        Composite buttonBar = createDefaultButtonBarComposite(content);

        contributeButtons(buttonBar);

        if (createApplyButton || createDefaultButton) {
            GridLayout buttonBarLayout = (GridLayout) buttonBar.getLayout();
            buttonBarLayout.numColumns += 1 + (createApplyButton && createDefaultButton ? 1 : 0);
            int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);

            if (createDefaultButton) {
                String label = getDefaultButtonLabel(); //$NON-NLS-1$
                defaultsButton = new Button(buttonBar, SWT.PUSH);
                defaultsButton.setText(label);
                Dialog.applyDialogFont(defaultsButton);
                Point minButtonSize = defaultsButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
                GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
                data.widthHint = Math.max(widthHint, minButtonSize.x);
                defaultsButton.setLayoutData(data);
                defaultsButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        performDefaults();
                    }
                });
            }
            if (createApplyButton) {
                String label = getApplyButtonLabel(); //$NON-NLS-1$

                applyButton = new Button(buttonBar, SWT.PUSH);
                applyButton.setText(label);
                Dialog.applyDialogFont(applyButton);
                Point minButtonSize = applyButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
                GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
                data.widthHint = Math.max(widthHint, minButtonSize.x);
                applyButton.setLayoutData(data);
                applyButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        performApply();
                    }
                });
                applyButton.setEnabled(isValid());
            }
            applyDialogFont(buttonBar.getParent());
        } else {
            /*
             * Check if there are any other buttons on the button bar.
             * If not, throw away the button bar composite. Otherwise
             * there is an unusually large button bar.
             */
            if (buttonBar.getChildren().length < 1) {
                buttonBar.dispose();
            }
        }
    }

    protected String getDefaultButtonLabel() {
        return JFaceResources.getString("defaults");
    }

    protected String getApplyButtonLabel() {
        return JFaceResources.getString("apply");
    }

    protected Composite createDefaultButtonBarComposite(Composite content) {
        Composite buttonBar = new Composite(content, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.makeColumnsEqualWidth = false;
        buttonBar.setLayout(gridLayout);

        buttonBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        return buttonBar;
    }

//    protected Composite createButtonBarCompositeWithHelp(Composite content) {
//        Composite buttonBar = new Composite(content, SWT.NONE);
//        GridLayout gridLayout = new GridLayout(1, false);
//        gridLayout.marginHeight = 0;
//        gridLayout.marginWidth = 0;
//        buttonBar.setLayout(gridLayout);
//        buttonBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
//        
//        new HelpCompositeForDialog(buttonBar, getDocumentationUrl());
//        
//        return createDefaultButtonBarComposite(buttonBar);
//    }

    /**
     * @return if this preference page have available documentation
     */
    public boolean hasDocumentation() {
        return false;
    }

    /**
     * Get documentation for this preference page
     * 
     * @return return empty for default
     */
    public String getDocumentationUrl() {
        return "";
    }

    /**
     * Computes the size needed by this page's UI control.
     * <p>
     * All pages should override this method and set the appropriate sizes
     * of their widgets, and then call <code>super.doComputeSize</code>.
     * </p>
     *
     * @return the size of the preference page encoded as
     * <code>new Point(width,height)</code>
     */
    protected Point doComputeSize() {
        if (descriptionLabel != null && body != null) {
            Point bodySize = body.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            GridData gd = (GridData) descriptionLabel.getLayoutData();
            gd.widthHint = bodySize.x;
        }
        return getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

    /**
     * Returns the container of this page.
     *
     * @return the preference page container, or <code>null</code> if this
     * page has yet to be added to a container
     */
    public IPreferencePageContainer getContainer() {
        return container;
    }

    /**
     * Returns the preference store of this preference page.
     *
     * @return the preference store , or <code>null</code> if none
     */
    public IPreferenceStore getPreferenceStore() {
        if (preferenceStore == null) {
            preferenceStore = doGetPreferenceStore();
        }
        if (preferenceStore != null) {
            return preferenceStore;
        } else if (container != null) {
            return container.getPreferenceStore();
        }
        return null;
    }

    /**
     * The preference page implementation of an <code>IPreferencePage</code>
     * method returns whether this preference page is valid. Preference
     * pages are considered valid by default; call <code>setValid(false)</code>
     * to make a page invalid.
     * 
     * @see IPreferencePage#isValid()
     */
    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Suppresses creation of the standard Default and Apply buttons
     * for this page.
     * <p>
     * Subclasses wishing a preference page without these buttons
     * should call this framework method before the page's control
     * has been created.
     * </p>
     */
    protected void noDefaultAndApplyButton() {
        createApplyButton = false;
        createDefaultButton = false;
    }

    /**
     * Suppress creation of the standard Default button for this page.
     * <p>
     * Subclasses wishing a preference page with this button should call this
     * framework method before the page's control has been created.
     * </p>
     *
     * @since 3.11
     */
    protected void noDefaultButton() {
        createDefaultButton = false;
    }

    @Override
    public void setContainer(IPreferencePageContainer container) {
        this.container = container;
    }

    /**
     * Sets the preference store for this preference page.
     * <p>
     * If preferenceStore is set to null, getPreferenceStore
     * will invoke doGetPreferenceStore the next time it is called.
     * </p>
     *
     * @param store the preference store, or <code>null</code>
     * @see #getPreferenceStore
     */
    public void setPreferenceStore(IPreferenceStore store) {
        preferenceStore = store;
    }

    @Override
    public void setSize(Point uiSize) {
        Control control = getControl();
        if (control != null) {
            control.setSize(uiSize);
            size = uiSize;
        }
    }

    /**
     * Sets whether this page is valid.
     * The enable state of the container buttons and the
     * apply button is updated when a page's valid state
     * changes.
     * <p>
     *
     * @param b the new valid state
     */
    public void setValid(boolean b) {
        boolean oldValue = isValid;
        isValid = b;
        if (oldValue != isValid) {
            // update container state
            if (getContainer() != null) {
                getContainer().updateButtons();
            }
            // update page state
            updateApplyButton();
        }
    }

    /**
     * Updates the enabled state of the Apply button to reflect whether
     * this page is valid.
     */
    protected void updateApplyButton() {
        if (applyButton != null) {
            applyButton.setEnabled(isValid());
        }
    }

    /**
     * Creates a composite with a highlighted Note entry and a message text.
     * This is designed to take up the full width of the page.
     *
     * @param font the font to use
     * @param composite the parent composite
     * @param title the title of the note
     * @param message the message for the note
     * @return the composite for the note
     */
    protected Composite createNoteComposite(Font font, Composite composite, String title, String message) {
        Composite messageComposite = new Composite(composite, SWT.NONE);
        GridLayout messageLayout = new GridLayout();
        messageLayout.numColumns = 2;
        messageLayout.marginWidth = 0;
        messageLayout.marginHeight = 0;
        messageComposite.setLayout(messageLayout);
        messageComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        messageComposite.setFont(font);

        final Label noteLabel = new Label(messageComposite, SWT.BOLD);
        noteLabel.setText(title);
        noteLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
        noteLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        Label messageLabel = new Label(messageComposite, SWT.WRAP);
        messageLabel.setText(message);
        messageLabel.setFont(font);
        return messageComposite;
    }

    /**
     * Returns the Apply button.
     *
     * @return the Apply button
     */
    protected Button getApplyButton() {
        return applyButton;
    }

    /**
     * Returns the Restore Defaults button.
     *
     * @return the Restore Defaults button
     */
    protected Button getDefaultsButton() {
        return defaultsButton;
    }
}
