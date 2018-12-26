package com.kms.katalon.plugin.jira.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kms.katalon.plugin.jira.common.JiraIntegrationException;
import com.kms.katalon.plugin.jira.constants.StringConstants;
import com.kms.katalon.plugin.jira.entity.ImprovedIssue;
import com.kms.katalon.plugin.jira.entity.JiraAttachment;
import com.kms.katalon.plugin.jira.entity.JiraFilter;
import com.kms.katalon.plugin.jira.entity.JiraIssue;
import com.kms.katalon.plugin.jira.entity.JiraIssueType;
import com.kms.katalon.plugin.jira.entity.JiraProject;
import com.kms.katalon.plugin.jira.util.ImprovedIssueDeserializer;
import com.kms.katalon.plugin.jira.util.JiraDateDeserializer;
import com.kms.katalon.plugin.jira.util.JsonUtil;

public class JiraIntegrationAuthenticationHandler extends JiraIntegrationRequest {

    private static final String UTF_8 = "utf-8";

    public User authenticate(JiraCredential credential) throws JiraIntegrationException {
        return getJiraObject(credential, JiraAPIURL.getUserAPIUrl(credential), User.class);
    }

    public JiraIssue getJiraIssue(JiraCredential credential, String issueKey) throws JiraIntegrationException {
        return getJiraObject(credential, JiraAPIURL.getIssueAPIUrl(credential) + "/" + issueKey, JiraIssue.class);
    }

    public JiraIssueType[] getJiraIssuesTypes(JiraCredential credential) throws JiraIntegrationException {
        return getJiraArrayObjects(credential, JiraAPIURL.getIssueTypeAPIUrl(credential), JiraIssueType[].class);
    }

    public JiraProject[] getJiraProjects(JiraCredential credential) throws JiraIntegrationException {
        return getJiraArrayObjects(credential, JiraAPIURL.getProjectAPIUrl(credential), JiraProject[].class);
    }

    public List<JiraAttachment> uploadAttachment(JiraCredential credential, JiraIssue issue, String logFilePath)
            throws JiraIntegrationException {
        String result = sendUploadRequest(credential, JiraAPIURL.getIssueAttachmentsAPIUrl(credential, issue.getKey()),
                logFilePath);
        if (StringUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        return Arrays.asList(JsonUtil.fromJson(result, JiraAttachment[].class));
    }

//    public void updateIssue(JiraCredential credential, JiraIssue jiraIssue, TestCaseLogRecord logRecord)
//            throws JiraIntegrationException {
//        IssueMetaDataProvider metaDataProvider = new IssueMetaDataProvider(logRecord);
//        sendPutRequest(credential, JiraAPIURL.getIssueAPIUrl(credential) + "/" + jiraIssue.getKey(),
//                JsonUtil.toJson(metaDataProvider.toEdittingIssue(jiraIssue), false));
//    }

    public JiraFilter getJiraFilterByJql(JiraCredential credential, String jql)
            throws JiraIntegrationException, UnsupportedEncodingException {
        String result = getJiraResponse(credential,
                JiraAPIURL.getFilterByJqlUrl(credential) + URLEncoder.encode(jql, UTF_8));

        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new JiraDateDeserializer())
                .registerTypeAdapter(ImprovedIssue.class, new ImprovedIssueDeserializer()).create();
        return gson.fromJson(result, JiraFilter.class);
    }

//    public void sendKatalonIntegrationProperty(JiraCredential credential, JiraIssue issue, JiraTestResult testResult)
//            throws JiraIntegrationException {
//        sendPutRequest(credential, JiraAPIURL.getKatalonIssuePropertyUrl(credential, Long.toString(issue.getId())),
//                JsonUtil.toJson(testResult));
//    }

//    public void linkJiraIssues(JiraCredential credential, JiraIssue inwardIssue, JiraIssue outwardIssue)
//            throws JiraIntegrationException {
//        JiraIssueLink issueLink = new JiraIssueLink(JiraIssueLink.RELATES_TYPE, inwardIssue.getKey(),
//                outwardIssue.getKey());
//        sendPostRequest(credential, JiraAPIURL.getIssueLinkUrl(credential), issueLink.toJson());
//    }

    public Optional<Field> getKatalonCustomField(JiraCredential credential) throws JiraIntegrationException {
        Field[] fields = getJiraArrayObjects(credential, JiraAPIURL.getFieldAPIUrl(credential), Field[].class);
        if (fields == null) {
            return Optional.empty();
        }
        return Arrays.asList(fields)
                .stream()
                .filter(field -> field.getSchema() != null
                        && StringConstants.KATALON_CUSTOM_FIELD_ID.equals(field.getSchema().getCustom()))
                .findFirst();
    }
}
