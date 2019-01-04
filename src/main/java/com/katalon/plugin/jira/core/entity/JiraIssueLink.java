package com.katalon.plugin.jira.core.entity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JiraIssueLink {    
    public static final String RELATES_TYPE = "Relates";

    private final String inwardIssue;

    private final String outwardIssue;
    
    private final String type;

    public JiraIssueLink(String type, String inwardIssue, String outwardIssue) {
        this.type = type;
        this.inwardIssue = inwardIssue;
        this.outwardIssue = outwardIssue;
    }

    public String toJson() {
        Gson gson = new Gson();
        JsonObject jiraIssueLink = new JsonObject();

        JsonObject typeJs = new JsonObject();
        typeJs.addProperty("name", type);
        jiraIssueLink.add("type", typeJs);

        JsonObject inwardIssueJs = new JsonObject();
        inwardIssueJs.addProperty("key", inwardIssue);
        jiraIssueLink.add("inwardIssue", inwardIssueJs);

        JsonObject outwardIssueJs = new JsonObject();
        outwardIssueJs.addProperty("key", outwardIssue);
        jiraIssueLink.add("outwardIssue", outwardIssueJs);

        return gson.toJson(jiraIssueLink);
    }
}
