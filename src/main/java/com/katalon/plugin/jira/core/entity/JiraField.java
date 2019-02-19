package com.katalon.plugin.jira.core.entity;

import java.net.URI;

import com.atlassian.jira.rest.client.api.AddressableEntity;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.FieldType;

public class JiraField extends Field implements AddressableEntity {
    
    private boolean custom;

    public JiraField(String id, String name, FieldType fieldType, boolean orderable, boolean navigable,
            boolean searchable, FieldSchema schema) {
        super(id, name, fieldType, orderable, navigable, searchable, schema);
    }

    @Override
    public URI getSelf() {
        return URI.create("katalon://" + getId());
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
