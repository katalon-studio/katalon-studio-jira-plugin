package com.katalon.plugin.jira.core.issue;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.report.LogRecord;
import com.katalon.platform.api.report.LogStatus;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.core.constant.JiraIntegrationMessageConstants;
import com.katalon.plugin.jira.core.entity.JiraEdittingIssue;
import com.katalon.plugin.jira.core.entity.JiraIssue;

public class IssueMetaDataProvider {

    protected TestCaseRecord testCaseRecord;

    private int endStepIndex;

    private TestSuiteRecord testSuiteRecord;

    public IssueMetaDataProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord testCaseRecord) {
        this(testSuiteRecord, testCaseRecord, testCaseRecord.getChildRecords().size());
    }

    public IssueMetaDataProvider(TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord, int endStepIndex) {
        this.testSuiteRecord = testSuiteRecord;
        this.testCaseRecord = logRecord;
        this.endStepIndex = endStepIndex;
    }

    public String getDescription() {
        return getStepDescription() + getErrorMessage();
    }

    /**
     * @return description of steps on JIRA looks like this:
     * 
     * <pre>
     * Test Steps:
     * 1. openBrowser
     * 2. navigateToURL
     * </pre>
     */
    private String getStepDescription() {
        StringBuilder builder = new StringBuilder();
        if (endStepIndex > 0) {
            builder.append(JiraIntegrationMessageConstants.MSG_TEST_STEPS + "\n");
            List<LogRecord> childRecords = testCaseRecord.getChildRecords();
            for (int i = 0; i < endStepIndex; i++) {
                LogRecord stepRecord = childRecords.get(i);
                builder.append(Integer.toString(i + 1))
                        .append(". ")
                        .append(StringUtils.defaultString(stepRecord.getName()))
                        .append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private String getErrorMessage() {
        if (testCaseRecord.getLogStatus() != LogStatus.ERROR && testCaseRecord.getLogStatus() != LogStatus.FAILED) {
            return StringUtils.EMPTY;
        }
        String message = testCaseRecord.getMessage();
        if (StringUtils.isEmpty(message)) {
            return StringUtils.EMPTY;
        }
        return MessageFormat.format(JiraIntegrationMessageConstants.MSG_ERROR_LOG, message);
    }

    public String getSummary() {
        String testCaseId = testCaseRecord.getTestCaseId();
        return testCaseId.substring(testCaseId.lastIndexOf("/") + 1, testCaseId.length());
    }

    public String getEnvironment() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> runDataEntry : testSuiteRecord.getRunData().entrySet()) {
            builder.append("- ")
                    .append(runDataEntry.getKey())
                    .append(": ")
                    .append(runDataEntry.getValue())
                    .append("\n");
        }
        return builder.toString();
    }

    public JiraEdittingIssue toEdittingIssue(JiraIssue jiraIssue) {
        return new JiraEdittingIssue(updateDescriptionForJiraIssue(jiraIssue));
    }

    private String updateDescriptionForJiraIssue(JiraIssue jiraIssue) {
        StringBuilder descriptionBuilder = new StringBuilder();

        String oldDescription = jiraIssue.getFields().getDescription();
        if (StringUtils.isNotEmpty(oldDescription)) {
            descriptionBuilder.append(oldDescription).append("\n");
        }
        descriptionBuilder.append(getDescription());
        return descriptionBuilder.toString();
    }
}
