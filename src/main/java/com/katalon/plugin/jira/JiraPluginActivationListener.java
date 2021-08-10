package com.katalon.plugin.jira;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.extension.PluginActivationListener;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.plugin.jira.core.JiraComponent;
import com.katalon.plugin.jira.core.report.JiraReportIntegration;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;

import ch.qos.logback.classic.Logger;

public class JiraPluginActivationListener implements PluginActivationListener, JiraComponent {

    private Logger logger = (Logger) LoggerFactory.getLogger(JiraReportIntegration.class);

    @Override
    public void afterActivation(Plugin plugin) {
        ProjectEntity project = getCurrentProject();

        if (project != null) {
            JiraIntegrationSettingStore store = getSettingStore();
            try {
                if (!store.isEncryptionMigrated() && store.isEncryptionEnabled()) {
                    String password = store.getPassword(store.isEncryptionEnabled());
                    if (StringUtils.isNotEmpty(password)) {
                        store.saveEncryptionMigrated(true);
                        store.savePassword(password, true);
                        store.saveStore();
                    }
                }
            } catch (PlatformException | IOException | GeneralSecurityException e) {
                logger.debug("Failed to migrate JIRA credentials", e);
            }
        }
    }
}
