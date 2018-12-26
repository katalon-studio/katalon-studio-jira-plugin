package com.kms.katalon.plugin.jira.entity;

public class JiraIntegratedIssue extends JiraIntegratedObject {

    private JiraIssue jiraIssue;

    public JiraIntegratedIssue(JiraIssue jiraIssue) {
        this.jiraIssue = jiraIssue;
    }

    public JiraIssue getJiraIssue() {
        return jiraIssue;
    }

    public void setJiraIssue(JiraIssue jiraIssue) {
        this.jiraIssue = jiraIssue;
    }
}
