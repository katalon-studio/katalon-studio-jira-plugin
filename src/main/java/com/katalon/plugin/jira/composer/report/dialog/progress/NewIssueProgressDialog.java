package com.katalon.plugin.jira.composer.report.dialog.progress;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;

public class NewIssueProgressDialog extends JiraIssueProgressDialog {
    public NewIssueProgressDialog(Shell parent, String issueKey, TestSuiteRecord testSuiteRecord,
            TestCaseRecord logRecord) {
        super(parent, issueKey, testSuiteRecord, logRecord);
    }

    public JiraIssueProgressResult run() {
        final JiraIssueProgressResult result = new JiraIssueProgressResult();
        try {
            run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(MessageFormat.format(
                                ComposerJiraIntegrationMessageConstant.JOB_TASK_UPDATE_JIRA_ISSUE, issueKey), 2);
                        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();
                        retrieveJiraIssue(handler, result);
                        checkCanceled(monitor);
                        monitor.worked(1);

                        uploadTestCaseLog(logRecord, result.getJiraIssue());
                        checkCanceled(monitor);
                        monitor.worked(1);

                        linkWithTestCaseJiraIssue(logRecord, result.getJiraIssue());
                        checkCanceled(monitor);
                        monitor.worked(1);
                        result.setComplete(true);
                    } catch (JiraIntegrationException e) {
                        result.setError(e);
                    } catch (IOException e) {
                        result.setError(new JiraIntegrationException(e));
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException ignored) {}
        return result;
    }
}
