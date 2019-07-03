package com.katalon.plugin.jira.core.entity;

import java.net.URI;

import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.google.common.base.Objects;

public class JiraIssueType extends IssueType {

    private String iconUrl;

    private JiraScope scope;

    public JiraIssueType(URI self, Long id, String name, boolean isSubtask, String description, URI iconUri) {
        super(self, id, name, isSubtask, description, iconUri);
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public JiraScope getScope() {
        return scope;
    }

    public void setScope(JiraScope scope) {
        this.scope = scope;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((iconUrl == null) ? 0 : iconUrl.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JiraIssueType other = (JiraIssueType) obj;
        return Objects.equal(this.iconUrl, other.iconUrl) 
                && Objects.equal(this.scope, other.scope);
    }

}
