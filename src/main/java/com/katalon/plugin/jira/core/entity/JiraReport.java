package com.katalon.plugin.jira.core.entity;

import java.util.HashMap;
import java.util.Map;

public class JiraReport extends JiraIntegratedObject {
    private Map<Integer, JiraIssueCollection> issueCollectionMap;

    public Map<Integer, JiraIssueCollection> getIssueCollectionMap() {
        if (issueCollectionMap == null) {
            issueCollectionMap = new HashMap<>();
        }
        return issueCollectionMap;
    }

    public void setIssueCollectionMap(Map<Integer, JiraIssueCollection> issueCollectionMap) {
        this.issueCollectionMap = issueCollectionMap;
    }
}
