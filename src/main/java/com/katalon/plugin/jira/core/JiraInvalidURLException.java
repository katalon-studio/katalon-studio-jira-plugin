package com.katalon.plugin.jira.core;

public class JiraInvalidURLException extends JiraIntegrationException {
    private static final long serialVersionUID = 7942880791614112602L;

    public JiraInvalidURLException(String message) {
        super(message);
    }
    
    public JiraInvalidURLException(Exception ex) {
        super(ex);
    }
}
