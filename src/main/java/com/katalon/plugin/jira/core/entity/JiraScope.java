package com.katalon.plugin.jira.core.entity;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.google.common.base.Objects;

public class JiraScope {

    private String type;

    private BasicProject project;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BasicProject getProject() {
        return project;
    }

    public void setProject(BasicProject project) {
        this.project = project;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JiraScope other = (JiraScope) obj;
        return Objects.equal(this.type, other.type) 
                && Objects.equal(this.project, other.project);
    }
}
