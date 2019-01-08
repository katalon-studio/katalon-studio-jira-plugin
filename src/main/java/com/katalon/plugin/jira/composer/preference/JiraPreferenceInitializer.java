package com.katalon.plugin.jira.composer.preference;

import static com.katalon.plugin.jira.composer.constant.PreferenceConstants.PREF_LAST_EDITED_JQL;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.google.gson.reflect.TypeToken;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.util.JsonUtil;

public class JiraPreferenceInitializer {

    private static Type getTokenTypeForMap() {
        return new TypeToken<Map<String, String>>() {}.getType();
    }

    public static String getLastEditedJQL(ProjectEntity project) {
        if (project == null) {
            return StringUtils.EMPTY;
        }
        Map<String, String> jqlPerProjects = getJqlPerProjects();
        String key = project.getFolderLocation();
        return jqlPerProjects.containsKey(key) ? jqlPerProjects.get(key) : StringUtils.EMPTY;
    }

    private static Map<String, String> getJqlPerProjects() {
        Preferences preferences = getPreferences();
        String defaultValue = JsonUtil.toJson(Collections.emptyMap(), getTokenTypeForMap(), false);
        return JsonUtil.fromJson(preferences.get(PREF_LAST_EDITED_JQL, defaultValue), getTokenTypeForMap());
    }

    public static void saveLastEditedJQL(String jql, ProjectEntity project) throws JiraIntegrationException {
        if (jql == null || project == null) {
            return;
        }
        Map<String, String> jqlPerProjects = getJqlPerProjects();
        jqlPerProjects.put(project.getFolderLocation(), jql);
        
        Preferences preferences = getPreferences();
        preferences.put(PREF_LAST_EDITED_JQL, JsonUtil.toJson(jqlPerProjects, getTokenTypeForMap(), false));
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            throw new JiraIntegrationException(e);
        }
    }

    private static Preferences getPreferences() {
        Preferences preferences = InstanceScope.INSTANCE.getNode(StringConstants.JIRA_BUNDLE_ID);
        return preferences;
    }
}
