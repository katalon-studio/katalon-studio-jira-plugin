package com.katalon.plugin.jira.core.entity;

import java.net.URI;
import java.util.Map;

import com.atlassian.jira.rest.client.api.domain.BasicProject;

public class JiraProject extends BasicProject {
    
    private Map<String, String> avatarUrls;

    public JiraProject(URI self, String key, Long id, String name) {
        super(self, key, id, name);
    }

    public Map<String, String> getAvatarUrls() {
        return avatarUrls;
    }

    public void setAvatarUrls(Map<String, String> avatarUrls) {
        this.avatarUrls = avatarUrls;
    }

}
