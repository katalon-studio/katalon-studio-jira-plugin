package com.katalon.plugin.jira;

import java.io.IOException;

import com.katalon.platform.api.extension.ReportIntegrationViewDescription;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.report.JiraReportTestCaseIntegrationView;
import com.katalon.plugin.jira.composer.report.JiraTestCaseColumnDescription;
import com.katalon.plugin.jira.composer.report.JiraTestStepColumnDescription;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

public class JiraReportIntegrationViewDescription implements ReportIntegrationViewDescription, JiraUIComponent {

    @Override
    public Class<? extends TestCaseColumnDescription> getTestCaseColumnClass() {
        return JiraTestCaseColumnDescription.class;
    }

    @Override
    public Class<? extends TestStepColumnDescription> getTestStepColumnClass() {
        return JiraTestStepColumnDescription.class;
    }

    @Override
    public Class<? extends TestCaseRecordIntegrationView> getTestCaseRecordViewClass() {
        return JiraReportTestCaseIntegrationView.class;
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
