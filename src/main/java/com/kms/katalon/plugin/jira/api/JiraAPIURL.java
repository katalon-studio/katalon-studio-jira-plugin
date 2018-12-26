package com.kms.katalon.plugin.jira.api;

import org.apache.commons.lang3.StringUtils;

public class JiraAPIURL {
    public static final String REST_API_V2 = "/rest/api/2/";

    public static final String REST_API_URL_USER = "myself";

    public static final String REST_API_URL_PROJECT = "project";

    public static final String REST_API_URL_ISSUE = "issue";

    public static final String REST_API_URL_ISSUE_TYPE = "issuetype";

    public static final String REST_API_URL_ISSUE_LINK = "issueLink";

    public static final String REST_API_URL_ATTACHMENTS = "attachments";

    public static final String REST_API_URL_SEARCH = "search";

    public static final String REST_API_URL_SEARCH_BY_JQL = "search?maxResults=1000&jql=";

    public static final String REST_API_URL_SET_ISSUE_PROPERTY = "properties/katalonTestResult";

    public static final String REST_API_URL_FIELD = "field";

    public static String removeLastSplash(String s) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        String coppied = String.copyValueOf(s.toCharArray());
        while (coppied.endsWith("/")) {
            coppied = coppied.substring(0, coppied.length() - 1);
        }
        return coppied;
    }

    public static String getJiraAPIPrexfix(JiraCredential credential) {
        return removeLastSplash(credential.getServerUrl()) + REST_API_V2;
    }

    public static String getUserAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_USER;
    }

    public static String getProjectAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_PROJECT;
    }

    public static String getIssueAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_ISSUE;
    }

    public static String getIssueTypeAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_ISSUE_TYPE;
    }

    public static String getIssueLinkUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_ISSUE_LINK;
    }

    public static String getIssueAttachmentsAPIUrl(JiraCredential credential, String issueKey) {
        return getIssueAPIUrl(credential) + "/" + issueKey + "/" + REST_API_URL_ATTACHMENTS;
    }

    public static String getFilterByJqlUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_SEARCH_BY_JQL;
    }

    public static String getKatalonIssuePropertyUrl(JiraCredential credential, String issueId) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_ISSUE + "/" + issueId + "/"
                + REST_API_URL_SET_ISSUE_PROPERTY;
    }

    public static String getFieldAPIUrl(JiraCredential credential) {
        return getJiraAPIPrexfix(credential) + REST_API_URL_FIELD;
    }
}
