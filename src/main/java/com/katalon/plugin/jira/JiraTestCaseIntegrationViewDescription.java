package com.katalon.plugin.jira;

import java.io.IOException;

import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.testcase.JiraTestCaseIntegrationView;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

public class JiraTestCaseIntegrationViewDescription implements TestCaseIntegrationViewDescription, JiraUIComponent {

    @Override
    public String getName() {
        return "JIRA Plugin";
    }

    @Override
    public Class<? extends TestCaseIntegrationView> getTestCaseIntegrationView() {
        return JiraTestCaseIntegrationView.class;
    }

    @Override
    public boolean isEnabled(ProjectEntity projectEntity) {
        JiraIntegrationSettingStore settingStore = getSettingStore();
        try {
            return settingStore != null ? settingStore.isIntegrationEnabled() : false;
        } catch (IOException e) {
            return false;
        }
    }
}
