package com.katalon.plugin.jira.composer.report.dialog;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import com.katalon.plugin.jira.composer.util.CustomBrowser;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.issue.IssueHTMLLinkProvider;

import ch.qos.logback.classic.Logger;

public class JiraIssueBrowserDialog extends Dialog implements JiraUIComponent {
    private Logger logger = (Logger) LoggerFactory.getLogger(JiraIssueBrowserDialog.class);

    private boolean ready;

    private Text txtBrowserUrl;

    protected CustomBrowser browser;

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

        browser = new CustomBrowser(composite, SWT.NONE);
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
            browser.setUrl(htmlLinkProvider.getDashboardHTMLLink());
        } catch (IOException | URISyntaxException | GeneralSecurityException e) {
            logger.error("Unable to set URL to browser", e);
        }
    }

    private void registerControlModifyListeners() {
        browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(ProgressEvent event) {
                try {
                    String url = browser.getUrl();
                    if (!url.startsWith(getCredential().getServerUrl())) {
                        return;
                    }
                    if (isJiraCloud(url)) {
                        if (url.startsWith(htmlLinkProvider.getSecureDashboardHTMLLink())
                                && !url.equals(htmlLinkProvider.getDashboardHTMLLink())
                                && !url.startsWith(htmlLinkProvider.getIssueUrlPrefix())) {
                            browser.setUrl(htmlLinkProvider.getHTMLLink());
                        }
                    }

                    if (url.equals(htmlLinkProvider.getDashboardHTMLLink())) {
                        if (!isDashboardRequireLogin()) {
                            browser.setUrl(htmlLinkProvider.getHTMLLink());
                        }
                        return;
                    }

                    if (url.startsWith(htmlLinkProvider.getIssueUrlPrefix())) {
                        if (isNotAbleToCreateSubIssue()) {
                            return;
                        }
                        if (isIssueURLAuthorized()) {
                            ready = true;
                            trigger();
                        } else {
                            browser.setUrl(htmlLinkProvider.getLoginHTMLLink());
                        }
                        return;
                    }

                    String createdIssueURLPrefix = getHTMLIssueURLPrefix();
                    if (ready && url.startsWith(createdIssueURLPrefix)) {
                        browser.removeProgressListener(this);
                        issueKey = url.substring(createdIssueURLPrefix.length() + 1);
                        close();
                    }
                } catch (IOException | URISyntaxException | GeneralSecurityException | JiraIntegrationException e) {
                }
            }

            @Override
            public void changed(ProgressEvent event) {
            }
        });
        browser.addLocationListener(new LocationListener() {

            @Override
            public void changing(LocationEvent event) {
                txtBrowserUrl.setText(event.location);
            }

            @Override
            public void changed(LocationEvent event) {
                String location = browser.getUrl();
                txtBrowserUrl.setText(location);
            }
        });
    }
    
    private boolean isIssueURLAuthorized() {
        Object object = browser.evaluate("return document.getElementById('summary') !== null;");
        return object instanceof Boolean && ((Boolean) object).booleanValue();
    }

    private boolean isNotAbleToCreateSubIssue() {
        Object object = browser.evaluate(
                "return (document.getElementById('issuetype')!=null && document.getElementById('summary') == null);");
        return object instanceof Boolean && ((Boolean) object).booleanValue();
    }
    private boolean isDashboardRequireLogin() {
        Object object = browser.evaluate("return document.getElementById('username') !== null;");
        return object instanceof Boolean && ((Boolean) object).booleanValue();
    }

    private boolean isLoginDashboard() throws IOException, URISyntaxException, GeneralSecurityException {
        return htmlLinkProvider.getLoginHTMLLink().equals(browser.getUrl());
    }

    private boolean isLoginPage() throws IOException, URISyntaxException, GeneralSecurityException {
        String url = browser.getUrl();
        return url.startsWith(htmlLinkProvider.getLoginHTMLLink());
    }

    private boolean isSmartLoginPage(String url) {
        return url.contains("smartlock.google.com") || url.contains("https://id.atlassian.com/login");
    }
    
    private boolean isJiraCloud(String url) {
        return url.contains("atlassian.net");
    }

    protected void loginForCloud() {
//        try {
//            browser.execute("document.getElementById('username').innerText = '"
//                    + StringEscapeUtils.escapeEcmaScript(getCredential().getUsername()) + "';");
//
//            browser.execute("setTimeout(function waitLoginSubmitUsername(){ "
//                    + "document.getElementById('login-submit').click();" + "}, 500);");
//
//            browser.execute("setTimeout(function waitPassword(){ " + "document.getElementById('password').innerText = '"
//                    + StringEscapeUtils.escapeEcmaScript(getCredential().getPassword()) + "';"
//                    + "setTimeout(function waitLoginSubmitPassword(){ document.getElementById('login-submit').click();}, 500);"
//                    + "}, 2500);");
//
//        } catch (IOException | JiraIntegrationException e) {
//            logger.error("Unable to login to JIRA Cloud Server", e);
//        }
    }

    protected void loginForServer() {
//        try {
//            StringBuilder js = new StringBuilder();
//            js.append("document.getElementById(\"login-form-username\").value = \""
//                    + StringEscapeUtils.escapeEcmaScript(getCredential().getUsername()) + "\";\n")
//                    .append("document.getElementById(\"login-form-password\").value = \""
//                            + StringEscapeUtils.escapeEcmaScript(getCredential().getPassword()) + "\";\n")
//                    .append("document.getElementById(\"login-form-remember-me\").checked = true;\n")
//                    .append("document.getElementById(\"login-form-submit\").click();\n");
//            browser.execute(waitAndExec("login-form-submit", js.toString()));
//        } catch (IOException | JiraIntegrationException e) {
//            logger.error("Unable to login to JIRA Server", e);
//        }
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
        if (getShell() != null) {
            Rectangle rectangle = getShell().getDisplay().getPrimaryMonitor().getBounds();
            return new Point(rectangle.width - 100, rectangle.height - 100);
        } else {
            return new Point(1200, 900);
        }
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
