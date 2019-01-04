package com.katalon.plugin.jira.core.setting;

import java.net.URI;
import java.util.Arrays;

import com.atlassian.jira.rest.client.api.AddressableEntity;

public class StoredJiraObject<T extends AddressableEntity> {
    private URI defaultURI;

    private T[] jiraObjects;

    public StoredJiraObject(URI defaultURI, T[] jiraObjects) {
        this.defaultURI = defaultURI;
        this.jiraObjects = jiraObjects;
    }

    public T[] getJiraObjects() {
        return jiraObjects;
    }

    public URI getDefaultProjectURI() {
        return defaultURI;
    }

    public void setDefaultURI(URI defaultProjectURI) {
        this.defaultURI = defaultProjectURI;
    }

    public void setJiraObjects(T[] jiraObjects) {
        this.jiraObjects = jiraObjects;
    }

    public T getDefaultJiraObject() {
        if (jiraObjects == null || defaultURI == null) {
            return null;
        }
        return Arrays.stream(jiraObjects).filter(p -> p.getSelf().equals(defaultURI)).findFirst().orElse(null);
    }
}
