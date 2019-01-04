package com.katalon.plugin.jira.core.entity;

import java.net.URI;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicUser;

public class JiraAttachment extends Attachment {
    private final Long id;

    public JiraAttachment(Long id, URI self, String filename, BasicUser author, DateTime creationDate, int size,
            String mimeType, URI contentUri, URI thumbnailUri) {

        super(self, filename, author, creationDate, size, mimeType, contentUri, thumbnailUri);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
