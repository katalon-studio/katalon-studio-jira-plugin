package com.katalon.plugin.jira.composer;

import com.katalon.plugin.jira.core.JiraIntegrationException;

public class JiraProgressResult {
    private JiraIntegrationException error;

    private boolean complete;

    public JiraIntegrationException getError() {
        return error;
    }

    public void setError(JiraIntegrationException error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
