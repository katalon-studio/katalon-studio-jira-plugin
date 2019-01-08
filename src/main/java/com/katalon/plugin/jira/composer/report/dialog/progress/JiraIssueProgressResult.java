package com.katalon.plugin.jira.composer.report.dialog.progress;

import com.katalon.plugin.jira.composer.JiraProgressResult;
import com.katalon.plugin.jira.core.entity.JiraIssue;

public class JiraIssueProgressResult extends JiraProgressResult {
    private JiraIssue jiraIssue;

    public JiraIssue getJiraIssue() {
        return jiraIssue;
    }

    public void setJiraIssue(JiraIssue jiraIssue) {
        this.jiraIssue = jiraIssue;
    }
}
