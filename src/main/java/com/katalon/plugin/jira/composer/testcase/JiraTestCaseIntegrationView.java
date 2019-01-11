package com.katalon.plugin.jira.composer.testcase;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription.PartActionService;
import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription.TestCaseIntegrationView;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.util.ControlUtil;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.entity.JiraIssue;

import ch.qos.logback.classic.Logger;

public class JiraTestCaseIntegrationView implements JiraUIComponent, TestCaseIntegrationView {
    
    private Logger logger = (Logger) LoggerFactory.getLogger(JiraTestCaseIntegrationView.class);

    private Composite container;

    private Link lblDisplayKey;

    private Label lblDisplaySummary, lblDisplayStatus, lblDisplayDiscription;

    private JiraIssue jiraIssue;

    @Override
    public Control onCreateView(Composite parent, PartActionService partActionService, TestCaseEntity testCaseEntity) {
        jiraIssue = JiraObjectToEntityConverter.getJiraIssue(testCaseEntity);

        container = new Composite(parent, SWT.BORDER);
        container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        container.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 10;
        gridLayout.horizontalSpacing = 15;
        container.setLayout(gridLayout);

        Label lblKey = new Label(container, SWT.NONE);
        lblKey.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblKey.setText(ComposerJiraIntegrationMessageConstant.VIEW_LBL_KEY);

        lblDisplayKey = new Link(container, SWT.NONE);
        lblDisplayKey.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDisplayKey.setToolTipText(ComposerJiraIntegrationMessageConstant.VIEW_TOOLTIP_VIEW_ISSUE_ON_JIRA);

        Label lblSummary = new Label(container, SWT.NONE);
        lblSummary.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblSummary.setText(StringConstants.SUMMARY);

        lblDisplaySummary = new Label(container, SWT.WRAP);
        lblDisplaySummary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStatus = new Label(container, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblStatus.setText(StringConstants.STATUS);

        lblDisplayStatus = new Label(container, SWT.NONE);
        lblDisplayStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(StringConstants.DESCRIPTION);

        lblDisplayDiscription = new Label(container, SWT.WRAP);
        lblDisplayDiscription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        setInput();

        registerControlListeners();

        return container;
    }

    private void registerControlListeners() {
        lblDisplayKey.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                try {
                    Program.launch(getHTMLLink(jiraIssue).toURL().toString());
                } catch (IOException | URISyntaxException | GeneralSecurityException ex) {
                    logger.error("", e);
                }
            }
        });
    }

    private void setInput() {
        if (jiraIssue == null) {
            ControlUtil.recursiveSetEnabled(container, false);
            return;
        }
        lblDisplayKey.setText("<a>" + jiraIssue.getKey() + "</a>");

        Issue fields = jiraIssue.getFields();
        lblDisplaySummary.setText(StringUtils.defaultString(fields.getSummary()));
        lblDisplayStatus.setText(StringUtils.defaultString(fields.getStatus().getName()));
        lblDisplayDiscription.setText(StringUtils.defaultString(fields.getDescription()));
    }

    public boolean hasDocumentation() {
        return true;
    }

    public String getDocumentationUrl() {
        return ComposerJiraIntegrationMessageConstant.URL_TEST_CASE_INTEGRATION_JIRA;
    }
}
