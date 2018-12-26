package com.kms.katalon.plugin.jira.preference;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.atlassian.jira.rest.client.api.domain.User;
import com.kms.katalon.plugin.jira.api.JiraCredential;
import com.kms.katalon.plugin.jira.api.JiraIntegrationAuthenticationHandler;
import com.kms.katalon.plugin.jira.common.JiraIntegrationException;
import com.kms.katalon.plugin.jira.common.JiraProgressResult;
import com.kms.katalon.plugin.jira.constants.JiraIntegrationMessageConstants;
import com.kms.katalon.plugin.jira.entity.JiraIssueType;
import com.kms.katalon.plugin.jira.entity.JiraProject;
import com.kms.katalon.plugin.jira.setting.StoredJiraObject;
import com.kms.katalon.plugin.jira.ui.DisplayedComboboxObject;
import com.kms.katalon.plugin.jira.ui.DisplayedIssueTypeComboboxObject;
import com.kms.katalon.plugin.jira.ui.JiraProgressDialog;

public class JiraConnectionJob extends JiraProgressDialog {

    private JiraCredential credential;

    private JiraConnectionResult result;
    
    private JiraIntegrationAuthenticationHandler handler;

    public JiraConnectionJob(Shell parent, JiraCredential credential) {
        super(parent);
        this.credential = credential;
        handler = new JiraIntegrationAuthenticationHandler();
    }

    @Override
    public JiraConnectionResult run() {
        result = new JiraConnectionResult();
        try {
            run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(JiraIntegrationMessageConstants.JOB_TASK_JIRA_CONNECTION, 3);
                    try {
                        monitor.subTask(JiraIntegrationMessageConstants.JOB_SUB_TASK_VALIDATING_ACCOUNT);
                        validateJiraAccount();
                        monitor.worked(1);
                        checkCanceled(monitor);

                        monitor.subTask(JiraIntegrationMessageConstants.JOB_SUB_TASK_FETCHING_PROJECTS);
                        getJiraProjects();
                        monitor.worked(1);
                        checkCanceled(monitor);

                        monitor.subTask(JiraIntegrationMessageConstants.JOB_SUB_TASK_FETCHING_ISSUE_TYPES);
                        getJiraIssueTypes();
                        monitor.worked(1);
                        result.setComplete(true);
                    } catch (JiraIntegrationException e) {
                        result.setError(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException | InterruptedException ignored) {}
        return result;
    }

    private void validateJiraAccount() throws JiraIntegrationException {
        result.setUser(handler.authenticate(credential));
    }

    private void getJiraProjects() throws JiraIntegrationException {
        result.setJiraProjects(handler.getJiraProjects(credential));
    }

    private void getJiraIssueTypes() throws JiraIntegrationException {
        result.setJiraIssueTypes(handler.getJiraIssuesTypes(credential));
    }

    public class JiraConnectionResult extends JiraProgressResult {
        private User user;

        private DisplayedComboboxObject<JiraProject> jiraProjects;

        private DisplayedComboboxObject<JiraIssueType> jiraIssueTypes;

        public JiraConnectionResult() {
            setComplete(false);
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public void setJiraProjects(JiraProject[] jiraProjects) {
            this.jiraProjects = new DisplayedComboboxObject<>(new StoredJiraObject<JiraProject>(null, jiraProjects));
        }

        public DisplayedComboboxObject<JiraIssueType> getJiraIssueTypes() {
            return jiraIssueTypes;
        }

        public void setJiraIssueTypes(JiraIssueType[] jiraIssueTypes) {
            this.jiraIssueTypes = new DisplayedIssueTypeComboboxObject(
                    new StoredJiraObject<JiraIssueType>(null, jiraIssueTypes));
        }

        public DisplayedComboboxObject<JiraProject> getJiraProjects() {
            return jiraProjects;
        }
    }
}
