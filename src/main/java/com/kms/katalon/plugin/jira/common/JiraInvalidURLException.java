package com.kms.katalon.plugin.jira.common;

public class JiraInvalidURLException extends JiraIntegrationException {
    private static final long serialVersionUID = 7942880791614112602L;

    public JiraInvalidURLException(String message) {
        super(message);
    }
    
    public JiraInvalidURLException(Exception ex) {
        super(ex);
    }
}
