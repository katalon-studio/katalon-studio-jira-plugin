package com.katalon.plugin.jira.composer.preference;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.atlassian.jira.rest.client.api.domain.User;
import com.katalon.plugin.jira.composer.JiraProgressDialog;
import com.katalon.plugin.jira.composer.JiraProgressResult;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.entity.JiraField;
import com.katalon.plugin.jira.core.entity.JiraIssueType;
import com.katalon.plugin.jira.core.entity.JiraProject;
import com.katalon.plugin.jira.core.setting.StoredJiraObject;

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
                    monitor.beginTask(ComposerJiraIntegrationMessageConstant.JOB_TASK_JIRA_CONNECTION, 3);
                    try {
                        monitor.subTask(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_VALIDATING_ACCOUNT);
                        validateJiraAccount();
                        monitor.worked(1);
                        checkCanceled(monitor);

                        monitor.subTask(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_FETCHING_PROJECTS);
                        getJiraProjects();
                        monitor.worked(1);
                        checkCanceled(monitor);

                        monitor.subTask(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_FETCHING_ISSUE_TYPES);
                        getJiraIssueTypes();
                        monitor.worked(1);
                        
                        monitor.subTask(ComposerJiraIntegrationMessageConstant.JOB_SUB_TASK_FETCHING_FIELDS);
                        getJiraFields();
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
    
    private void getJiraFields() throws JiraIntegrationException {
        result.setJiraFields(handler.getBDDCustomFields(credential));
    }

    public class JiraConnectionResult extends JiraProgressResult {
        private User user;

        private DisplayedComboboxObject<JiraProject> jiraProjects;

        private DisplayedComboboxObject<JiraIssueType> jiraIssueTypes;
        
        private DisplayedComboboxObject<JiraField> jiraFields;

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

        public DisplayedComboboxObject<JiraField> getJiraFields() {
            return jiraFields;
        }

        public void setJiraFields(JiraField[] fields) {
            this.jiraFields = new DisplayedComboboxObject<>(new StoredJiraObject<JiraField>(null, fields));
        }
    }
}
