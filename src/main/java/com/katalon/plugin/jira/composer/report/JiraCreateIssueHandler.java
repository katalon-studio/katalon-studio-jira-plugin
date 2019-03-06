package com.katalon.plugin.jira.composer.report;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.report.dialog.CreateAsSubTaskBrowserDialog;
import com.katalon.plugin.jira.composer.report.dialog.JiraEditIssueDialog;
import com.katalon.plugin.jira.composer.report.dialog.JiraIssueBrowserDialog;
import com.katalon.plugin.jira.composer.report.dialog.JiraNewIssueDialog;
import com.katalon.plugin.jira.composer.report.dialog.LinkJiraIssueDialog;
import com.katalon.plugin.jira.composer.report.dialog.progress.JiraIssueProgressResult;
import com.katalon.plugin.jira.composer.report.dialog.progress.LinkJiraIssueProgressDialog;
import com.katalon.plugin.jira.composer.report.dialog.progress.NewIssueProgressDialog;
import com.katalon.plugin.jira.composer.report.dialog.progress.UpdateJiraIssueProgressDialog;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.issue.EditIssueHTMLLinkProvider;
import com.katalon.plugin.jira.core.issue.NewIssueHTMLLinkProvider;
import com.katalon.plugin.jira.core.issue.NewSubtaskHTMLLinkProvider;

import ch.qos.logback.classic.Logger;

public class JiraCreateIssueHandler implements JiraUIComponent {
    private Logger logger = (Logger) LoggerFactory.getLogger(JiraCreateIssueHandler.class);

    private Shell shell;

    private TestCaseRecord logRecord;

    private TestSuiteRecord testSuiteRecord;

    public JiraCreateIssueHandler(Shell shell, TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord) {
        this.shell = shell;
        this.logRecord = logRecord;
        this.testSuiteRecord = testSuiteRecord;
    }

    public JiraIssueProgressResult openLinkIssueDialog() {
        LinkJiraIssueDialog linkDialog = new LinkJiraIssueDialog(shell,
                ComposerJiraIntegrationMessageConstant.DIA_TITLE_LINK_TO_EXISTING_ISSUE,
                ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_LINK_TO_EXISTING_ISSUE,
                ComposerJiraIntegrationMessageConstant.DIA_LBL_LINK_TO_EXISTING_ISSUE);
        if (linkDialog.open() != Dialog.OK) {
            return null;
        }
        return new LinkJiraIssueProgressDialog(shell, linkDialog.getIssueKey(), testSuiteRecord, logRecord).run();
    }

    public JiraIssueProgressResult openNewIssueDialog(int numSteps) {
        try {
            NewIssueHTMLLinkProvider htmlLinkProvider = new NewIssueHTMLLinkProvider(testSuiteRecord, logRecord,
                    numSteps, getSettingStore());
            return openNewIssueBrowserDialog(new JiraNewIssueDialog(shell, logRecord, htmlLinkProvider));
        } catch (URISyntaxException | IOException ex) {
            logger.error("", ex);
            return null;
        }
    }

    private JiraIssueProgressResult openNewIssueBrowserDialog(JiraIssueBrowserDialog browserDialog) {
        if (browserDialog.open() != Dialog.OK) {
            return null;
        }
        String issueKey = browserDialog.getIssueKey();
        return new NewIssueProgressDialog(shell, issueKey, testSuiteRecord, logRecord).run();
    }

    public JiraIssueProgressResult openCreateAsSubTaskDialog(int numSteps) {
        LinkJiraIssueDialog linkDialog = new LinkJiraIssueDialog(shell,
                ComposerJiraIntegrationMessageConstant.DIA_TITLE_CREATE_NEW_AS_SUB_TASK,
                ComposerJiraIntegrationMessageConstant.DIA_MESSAGE_CREATE_NEW_AS_SUB_TASK,
                ComposerJiraIntegrationMessageConstant.DIA_LBL_CREATE_NEW_AS_SUB_TASK);
        if (linkDialog.open() != Dialog.OK) {
            return null;
        }
        JiraIssueProgressResult result = new UpdateJiraIssueProgressDialog(shell, linkDialog.getIssueKey(),
                testSuiteRecord, logRecord).run();
        if (!checkResult(result)) {
            return null;
        }

        try {
            JiraIssue parentIssue = result.getJiraIssue();
            CreateAsSubTaskBrowserDialog browserDialog = new CreateAsSubTaskBrowserDialog(shell, logRecord,
                    new NewSubtaskHTMLLinkProvider(testSuiteRecord, logRecord, getSettingStore(), parentIssue));
            if (browserDialog.open() != Dialog.OK) {
                return null;
            }
            String issueKey = browserDialog.getIssueKey();
            if (parentIssue.getKey().equals(issueKey)) {
                return null;
            }
            return new NewIssueProgressDialog(shell, issueKey, testSuiteRecord, logRecord).run();

        } catch (URISyntaxException | IOException e) {
            logger.error("", e);
            return null;
        }
    }

    public boolean checkResult(JiraIssueProgressResult result) {
        if (result == null) {
            return false;
        }
        if (result.hasError()) {
            JiraIntegrationException error = result.getError();
            MessageDialog.openError(shell, StringConstants.ERROR, error.getMessage());
            logger.error("", error);
            return false;
        }
        return result.isComplete();
    }

    public JiraIssueProgressResult openEditIssueDialog(JiraIssue jiraIssue) {
        try {
            EditIssueHTMLLinkProvider htmlLinkProvider = new EditIssueHTMLLinkProvider(testSuiteRecord, logRecord,
                    getSettingStore(), jiraIssue);
            JiraEditIssueDialog browserDialog = new JiraEditIssueDialog(shell, logRecord, htmlLinkProvider);

            if (browserDialog.open() != Dialog.OK) {
                return null;
            }
            return new UpdateJiraIssueProgressDialog(shell, browserDialog.getIssueKey(), testSuiteRecord, logRecord)
                    .run();
        } catch (ClassCastException | URISyntaxException | IOException e) {
            logger.error("", e);
            return null;
        }
    }

}
