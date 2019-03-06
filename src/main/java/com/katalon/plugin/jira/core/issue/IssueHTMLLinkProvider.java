package com.katalon.plugin.jira.core.issue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public interface IssueHTMLLinkProvider {

    String getSecureDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException;

    String getLoginHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException;

    String getHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException;

    String getDashboardHTMLLink() throws IOException, URISyntaxException, GeneralSecurityException;

    String getIssueUrl() throws IOException, URISyntaxException, GeneralSecurityException;

    String getIssueUrlPrefix() throws IOException, URISyntaxException, GeneralSecurityException;
}
