package com.katalon.plugin.jira.composer.preference;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.dialog.PreferencePageWithHelp;


public class JiraSettingPage extends PreferencePageWithHelp {

    private JiraSettingsComposite settingsComposite;

    private Control container;

    public JiraSettingPage() {
        settingsComposite = new JiraSettingsComposite();
    }

    @Override
    protected Control createContents(Composite parent) {
        createControls(parent);

        initialize();

        addControlModifyListeners();

        return container;
    }

    private void createControls(Composite parent) {
        container = settingsComposite.createContainer(parent);
    }

    private void initialize() {
        settingsComposite.initializeData();
    }

    private void addControlModifyListeners() {
        settingsComposite.registerControlModifyListeners();
    }

    @Override
    public boolean performOk() {
        if (container == null) {
            return true;
        }
        return settingsComposite.okPressed();
    }

    @Override
    protected void performDefaults() {
        initialize();
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return ComposerJiraIntegrationMessageConstant.URL_SETTINGS_JIRA;
    }
}
