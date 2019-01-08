package com.katalon.plugin.jira.composer.report.dialog;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.issue.IssueMetaDataProvider;
import com.katalon.plugin.jira.core.issue.NewSubtaskHTMLLinkProvider;

import ch.qos.logback.classic.Logger;

public class CreateAsSubTaskBrowserDialog extends JiraIssueBrowserDialog {
    
    private Logger logger = (Logger) LoggerFactory.getLogger(CreateAsSubTaskBrowserDialog.class);

    private IssueMetaDataProvider issueMetaData;
    public CreateAsSubTaskBrowserDialog(Shell parentShell, TestCaseRecord logRecord,
            NewSubtaskHTMLLinkProvider htmlLinkProvider) throws URISyntaxException, IOException {
        super(parentShell, logRecord, htmlLinkProvider);
        this.issueMetaData = htmlLinkProvider.getIssueMetaData();
    }

    @Override
    protected void trigger() {
        try {
            StringBuilder updateFieldsJS = new StringBuilder();
            if (getSettingStore().isUseTestCaseNameAsSummaryEnabled()) {
                updateFieldsJS.append(updateField(JiraIssue.FIELD_SUMMARY, issueMetaData.getSummary()));
            }
            updateFieldsJS.append(updateField(JiraIssue.FIELD_DESCRIPTION, issueMetaData.getDescription()));
            browser.execute(waitAndExec(JiraIssue.FIELD_DESCRIPTION, updateFieldsJS.toString()));
        } catch (IOException e) {
           logger.error("", e);
        }
    }

    private String updateField(String id, String value) {
        return "document.getElementById(\"" + id + "\").value = \"" + StringEscapeUtils.escapeEcmaScript(value) + "\";\n";
    }
}
