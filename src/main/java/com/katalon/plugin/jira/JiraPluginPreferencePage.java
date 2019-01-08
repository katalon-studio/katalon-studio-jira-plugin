package com.katalon.plugin.jira;

import com.katalon.platform.api.extension.PluginPreferencePage;
import com.katalon.plugin.jira.composer.preference.JiraSettingPage;

public class JiraPluginPreferencePage implements PluginPreferencePage {

    @Override
    public String getName() {
        return "JIRA";
    }

    @Override
    public String getPageId() {
        return "com.katalon.katalon-studio-jira-plugin";
    }

    @Override
    public Class<JiraSettingPage> getPreferencePageClass() {
        return JiraSettingPage.class;
    }

}
