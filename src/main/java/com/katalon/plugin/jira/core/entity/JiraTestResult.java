package com.katalon.plugin.jira.core.entity;

public class JiraTestResult {
    private String status;

    private long[] attachmentIds;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long[] getAttachmentIds() {
        return this.attachmentIds;
    }

    public void setAttachmentIds(long[] attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
    
    public static JiraTestResult from(String status, long[] attachmentIds) {
        JiraTestResult testResult = new JiraTestResult();
        testResult.setStatus(status);
        testResult.setAttachmentIds(attachmentIds);
        return testResult;
    }
}
