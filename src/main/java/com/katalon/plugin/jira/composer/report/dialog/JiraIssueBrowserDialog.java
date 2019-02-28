package com.katalon.plugin.jira.composer.report.dialog;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.issue.IssueHTMLLinkProvider;

import ch.qos.logback.classic.Logger;

public class JiraIssueBrowserDialog extends Dialog implements JiraUIComponent {
    private Logger logger = (Logger) LoggerFactory.getLogger(JiraIssueBrowserDialog.class);

    private Text txtBrowserUrl;

    protected Browser browser;

    private String issueKey;

    private IssueHTMLLinkProvider htmlLinkProvider;

    public JiraIssueBrowserDialog(Shell parentShell, TestCaseRecord logRecord, IssueHTMLLinkProvider htmlLinkProvider)
            throws URISyntaxException, IOException {
        super(parentShell);
        this.htmlLinkProvider = htmlLinkProvider;
    }

    @Override
    protected int getShellStyle() {
        return (SWT.CLOSE | SWT.ON_TOP | SWT.RESIZE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) composite.getLayout();
        gridLayout.numColumns = 2;
        composite.setBackgroundMode(SWT.INHERIT_FORCE);
        txtBrowserUrl = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
        txtBrowserUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(composite, SWT.NONE);

        browser = new Browser(composite, SWT.NONE);
        browser.setJavascriptEnabled(true);
        browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        new Label(composite, SWT.NONE);

        Label lblNotification = new Label(composite, SWT.WRAP);
        lblNotification.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNotification.setText(ComposerJiraIntegrationMessageConstant.DIA_ISSUE_BROWSE_NOTIFICATION);

        registerControlModifyListeners();
        setInput();
        return composite;
    }

    private void setInput() {
        try {
            browser.setUrl(htmlLinkProvider.getLoginHTMLLink());
        } catch (IOException | URISyntaxException | GeneralSecurityException e) {
            logger.error("Unable to set URL to browser", e);
        }
    }

    private void registerControlModifyListeners() {
        browser.addLocationListener(new LocationListener() {
            private boolean ready;

            private boolean dashBoardSet;

            private boolean loggedIn;

            @Override
            public void changing(LocationEvent event) {
                txtBrowserUrl.setText(event.location);
                try {
                    if (!loggedIn && isSmartLoginPage(event.location)) {
                        loggedIn = true;
                        login();
                        return;
                    }
                } catch (IOException | URISyntaxException | GeneralSecurityException ignore) {}
            }

            @Override
            public void changed(LocationEvent event) {
                try {
                    String location = browser.getUrl();
                    if (!ready) {
                        if (!loggedIn && isLoginPage()) {
                            loggedIn = true;
                            login();
                            return;
                        }

                        if (location.startsWith(htmlLinkProvider.getIssueUrlPrefix())) {
                            ready = true;
                            trigger();
                            return;
                        }

                        if (!dashBoardSet && !event.location.equals(htmlLinkProvider.getDashboardHTMLLink())) {
                            browser.setUrl(htmlLinkProvider.getDashboardHTMLLink());
                            browser.setUrl(htmlLinkProvider.getHTMLLink());
                            dashBoardSet = true;
                            return;
                        }

                        return;
                    }
                    getNewIssueKey(location);
                } catch (IOException | URISyntaxException | GeneralSecurityException e) {
                    logger.error("Unable to navigate to dashboard", e);
                }
            }

            private void login() throws IOException, URISyntaxException, GeneralSecurityException {
                if (isLoginDashboard()) {
                    loginForServer();
                } else {
                    loginForCloud();
                }
            }

            private void getNewIssueKey(String location) throws IOException, GeneralSecurityException {
                String createdIssueURLPrefix = getHTMLIssueURLPrefix();
                if (location.startsWith(createdIssueURLPrefix)) {
                    browser.removeLocationListener(this);
                    issueKey = location.substring(createdIssueURLPrefix.length() + 1);
                    close();
                }
            }
        });
    }

    private boolean isLoginDashboard() throws IOException, URISyntaxException, GeneralSecurityException {
        return htmlLinkProvider.getLoginHTMLLink().equals(browser.getUrl());
    }

    private boolean isLoginPage() throws IOException, URISyntaxException, GeneralSecurityException {
        String url = browser.getUrl();
        return htmlLinkProvider.getLoginHTMLLink().startsWith(url);
    }

    private boolean isSmartLoginPage(String url) {
        return url.contains("smartlock.google.com");
    }

    protected void loginForCloud() {
        try {
            browser.execute("document.getElementById('username').value = '"
                    + StringEscapeUtils.escapeEcmaScript(getCredential().getUsername()) + "';");

            browser.execute("document.getElementById('login-submit').click();");

            browser.execute("document.getElementById('password').value = '"
                    + StringEscapeUtils.escapeEcmaScript(getCredential().getPassword()) + "';");

            browser.execute(
                    "setTimeout(function waitLoginSubmit(){ document.getElementById('login-submit').click();}, 3000);");
        } catch (IOException | JiraIntegrationException e) {
            logger.error("Unable to login to JIRA cloud", e);
        }
    }

    protected void loginForServer() {
        try {
            StringBuilder js = new StringBuilder();
            js.append("document.getElementById(\"login-form-username\").value = \""
                    + StringEscapeUtils.escapeEcmaScript(getCredential().getUsername()) + "\";\n")
                    .append("document.getElementById(\"login-form-password\").value = \""
                            + StringEscapeUtils.escapeEcmaScript(getCredential().getPassword()) + "\";\n")
                    .append("document.getElementById(\"login-form-remember-me\").checked = true;\n")
                    .append("document.getElementById(\"login-form-submit\").click();\n");
            browser.execute(waitAndExec("login-form-submit", js.toString()));
        } catch (IOException | JiraIntegrationException e) {
            logger.error("Unable to login to JIRA server", e);
        }
    }

    protected String waitAndExec(String element, String js) {
        return "function waitUntilExist() {" + "if (document.getElementById('" + element + "') === null) {"
                + "setTimeout(waitUntilExist, 1000);" + "} else {" + js + "}" + "};" + "waitUntilExist();";
    }

    protected String updateField(String id, String value) {
        return "document.getElementById(\"" + id + "\").value = \"" + StringEscapeUtils.escapeEcmaScript(value)
                + "\";\n";
    }

    protected void trigger() {
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return null;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(1200, 800);
    }

    @Override
    public boolean close() {
        browser.close();
        return super.close();
    }

    public String getIssueKey() {
        return issueKey;
    }
}
