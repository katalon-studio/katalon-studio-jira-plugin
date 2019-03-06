package com.katalon.plugin.jira.composer.report.dialog;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.issue.DefaultIssueHTMLLinkProvider;

import ch.qos.logback.classic.Logger;

public class JiraEditIssueDialog extends JiraIssueBrowserDialog {

    private Logger logger = (Logger) LoggerFactory.getLogger(JiraIssueBrowserDialog.class);

    private final DefaultIssueHTMLLinkProvider htmlLinkProvider;

    public JiraEditIssueDialog(Shell parentShell, TestCaseRecord logRecord,
            DefaultIssueHTMLLinkProvider htmlLinkProvider) throws URISyntaxException, IOException {
        super(parentShell, logRecord, htmlLinkProvider);
        this.htmlLinkProvider = htmlLinkProvider;
    }

    @Override
    protected void trigger() {
        StringBuilder updateFieldsJS = new StringBuilder();
        try {
            if (getSettingStore().isUseTestCaseNameAsSummaryEnabled()) {
                updateFieldsJS
                        .append(updateField(JiraIssue.FIELD_SUMMARY, htmlLinkProvider.getIssueMetaData().getSummary()));
            }

            updateFieldsJS.append(
                    updateField(JiraIssue.FIELD_DESCRIPTION, htmlLinkProvider.getIssueMetaData().getDescription()));

            updateFieldsJS.append(
                    updateField(JiraIssue.FIELD_ENVIRONMENT, htmlLinkProvider.getIssueMetaData().getEnvironment()));
            browser.execute(waitAndExec(JiraIssue.FIELD_DESCRIPTION, updateFieldsJS.toString()));
        } catch (IOException e) {
            logger.error("", e);
        }
    }
}
