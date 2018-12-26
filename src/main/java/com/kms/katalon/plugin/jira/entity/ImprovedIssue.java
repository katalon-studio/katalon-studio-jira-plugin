package com.kms.katalon.plugin.jira.entity;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.BasicWatchers;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Operations;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.Status;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.Worklog;

public class ImprovedIssue extends Issue {

    private Map<String, Object> customFields;

    public ImprovedIssue(String summary, URI self, String key, Long id, BasicProject project, IssueType issueType,
            Status status, String description, BasicPriority priority, Resolution resolution,
            Collection<Attachment> attachments, User reporter, User assignee, DateTime creationDate,
            DateTime updateDate, DateTime dueDate, Collection<Version> affectedVersions,
            Collection<Version> fixVersions, Collection<BasicComponent> components, TimeTracking timeTracking,
            Collection<IssueField> issueFields, Collection<Comment> comments, URI transitionsUri,
            Collection<IssueLink> issueLinks, BasicVotes votes, Collection<Worklog> worklogs, BasicWatchers watchers,
            Iterable<String> expandos, Collection<Subtask> subtasks, Collection<ChangelogGroup> changelog,
            Operations operations, Set<String> labels) {
        super(summary, self, key, id, project, issueType, status, description, priority, resolution, attachments,
                reporter, assignee, creationDate, updateDate, dueDate, affectedVersions, fixVersions, components,
                timeTracking, issueFields, comments, transitionsUri, issueLinks, votes, worklogs, watchers, expandos,
                subtasks, changelog, operations, labels);
    }

    public Map<String, Object> getCustomFields() {
        if (customFields == null) {
            customFields = new HashMap<>();
        }
        return customFields;
    }

    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }

}
