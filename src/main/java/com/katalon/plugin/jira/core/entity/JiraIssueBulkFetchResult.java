package com.katalon.plugin.jira.core.entity;

import java.util.List;

public class JiraIssueBulkFetchResult {
    private List<JiraIssue> issues;

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }
}
