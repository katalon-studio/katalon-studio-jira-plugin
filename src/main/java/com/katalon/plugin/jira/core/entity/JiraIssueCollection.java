package com.katalon.plugin.jira.core.entity;

import java.util.ArrayList;
import java.util.List;

public class JiraIssueCollection {
    private String testCaseId;

    private List<JiraIssue> issues;
    
    public JiraIssueCollection(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public List<JiraIssue> getIssues() {
        if (issues == null) {
            issues = new ArrayList<>();
        }
        return issues;
    }

    public void setIssues(List<JiraIssue> issues) {
        this.issues = issues;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }
}
