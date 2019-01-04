package com.katalon.plugin.jira.core.entity;

import java.util.List;

public class JiraFilter {
    private int startAt;

    private int maxResults;

    private int total;

    private List<JiraIssue> issues;

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<JiraIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public boolean isLastPage() {
        return startAt + 1 + maxResults >= total;
    }
}
