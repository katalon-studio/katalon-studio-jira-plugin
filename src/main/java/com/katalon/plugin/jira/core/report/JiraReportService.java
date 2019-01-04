package com.katalon.plugin.jira.core.report;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.katalon.platform.api.controller.ReportController;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ReportEntity;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.katalon.plugin.jira.core.JiraComponent;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.entity.JiraAttachment;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.entity.JiraTestResult;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class JiraReportService implements JiraComponent {
    public void uploadTestCaseLog(TestSuiteRecord testSuiteRecord, TestCaseRecord logRecord, JiraIssue issue)
            throws IOException, JiraIntegrationException {
        ReportEntity report = null;
        try {
            report = JiraComponent.getPlatformController(ReportController.class)
                    .getReport(JiraComponent.getCurrentProject(), testSuiteRecord.getReportId());
        } catch (ResourceException e) {
            throw new JiraIntegrationException(e);
        }

        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();

        List<JiraAttachment> jiraAttachments = new ArrayList<>();
        if (getSettingStore().isAttachScreenshotEnabled()) {
            for (String screenshot : logRecord.getAttachments()) {
                jiraAttachments.addAll(handler.uploadAttachment(getCredential(), issue,
                        new File(report.getFolderLocation(), screenshot).getAbsolutePath()));
            }
        }

        if (getSettingStore().isAttachLogEnabled()) {
            for (String logFile : testSuiteRecord.getLogFiles()) {
                jiraAttachments.addAll(handler.uploadAttachment(getCredential(), issue,
                        new File(report.getFolderLocation(), logFile).getAbsolutePath()));
            }
        }
    }

    public void uploadTestCaseReport(TestCaseRecord logRecord, JiraIssue issue, File zipFile)
            throws IOException, JiraIntegrationException {
        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();

        List<JiraAttachment> jiraAttachments = new ArrayList<>();
        jiraAttachments.addAll(handler.uploadAttachment(getCredential(), issue, zipFile.getAbsolutePath()));

        List<Long> jiraAttachmentIds = jiraAttachments.stream()
                .map(attachment -> attachment.getId())
                .collect(Collectors.toList());
        String testStatus = logRecord.getLogStatus().name();
        JiraTestResult testResult = JiraTestResult.from(testStatus,
                ArrayUtils.toPrimitive(jiraAttachmentIds.toArray(new Long[jiraAttachmentIds.size()])));

        handler.sendKatalonIntegrationProperty(getCredential(), issue, testResult);
    }

    public File zipReportFolder(File folderToZip) throws ZipException, IOException {
        File filteredTempFolder = new File(getJiraZipTempFolder(), folderToZip.getName());
        FileUtils.copyDirectory(folderToZip, filteredTempFolder, new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.getAbsolutePath().startsWith(new File(folderToZip, "videos").getAbsolutePath());
            }
        });
        File zipTempFile = new File(getJiraZipTempFolder(), folderToZip.getName() + ".zip");
        if (zipTempFile.exists()) {
            FileUtils.deleteQuietly(zipTempFile);
        }
        ZipFile returnedZipFile = new ZipFile(zipTempFile);

        ZipParameters parameters = new ZipParameters();

        // set compression method to store compression
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        // Set the compression level
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

        // Add folder to the zip file
        returnedZipFile.addFolder(filteredTempFolder.getAbsolutePath(), parameters);

        FileUtils.deleteDirectory(filteredTempFolder);

        return zipTempFile;
    }

    private File getJiraZipTempFolder() {
        String tempDir = System.getProperty("java.io.tmpdir") + File.separator + "Katalon";
        File zipTempFolder = new File(tempDir, "zip/" + StringConstants.JIRA_BUNDLE_ID);
        if (!zipTempFolder.exists()) {
            zipTempFolder.mkdirs();
        }
        return zipTempFolder;
    }

    public void linkIssues(TestCaseRecord logRecord, JiraIssue inwardIssue)
            throws JiraIntegrationException, IOException {
        JiraIssue outwardIssue = null;
        try {
            outwardIssue = JiraObjectToEntityConverter.getJiraIssue(
                    getTestCase(logRecord.getTestCaseId()));
        } catch (ResourceException e) {
            throw new JiraIntegrationException(e);
        }

        if (outwardIssue == null) {
            return;
        }
        JiraIntegrationAuthenticationHandler handler = new JiraIntegrationAuthenticationHandler();
        handler.linkJiraIssues(getCredential(), inwardIssue, outwardIssue);
    }
}
