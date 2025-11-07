package com.katalon.plugin.jira.core.entity;

public class JiraIssueBulkFetch {
    private String[] issueIdsOrKeys;

    private String[] fields = new String[] { "priority", "status", "summary", "description" };


    public JiraIssueBulkFetch(String[] issueIdsOrKeys) {
        this.issueIdsOrKeys = issueIdsOrKeys;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String[] getIssueIdsOrKeys() {
        return issueIdsOrKeys;
    }

    public void setIssueIdsOrKeys(String[] issueIdsOrKeys) {
        this.issueIdsOrKeys = issueIdsOrKeys;
    }
}
