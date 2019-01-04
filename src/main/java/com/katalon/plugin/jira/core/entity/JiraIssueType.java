package com.katalon.plugin.jira.core.entity;

import java.net.URI;

import com.atlassian.jira.rest.client.api.domain.IssueType;

public class JiraIssueType extends IssueType {
    
    private String iconUrl;

    public JiraIssueType(URI self, Long id, String name, boolean isSubtask, String description, URI iconUri) {
        super(self, id, name, isSubtask, description, iconUri);
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

}
