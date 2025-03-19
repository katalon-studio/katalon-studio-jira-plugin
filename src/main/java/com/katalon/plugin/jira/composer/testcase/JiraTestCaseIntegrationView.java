package com.katalon.plugin.jira.composer.testcase;

import ch.qos.logback.classic.Logger;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.katalon.platform.api.controller.TestCaseController;
import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription.PartActionService;
import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription.TestCaseIntegrationView;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.composer.constant.ComposerJiraIntegrationMessageConstant;
import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.composer.util.ControlUtil;
import com.katalon.plugin.jira.composer.util.Theme;
import com.katalon.plugin.jira.core.JiraCredential;
import com.katalon.plugin.jira.core.JiraIntegrationAuthenticationHandler;
import com.katalon.plugin.jira.core.JiraIntegrationException;
import com.katalon.plugin.jira.core.JiraObjectToEntityConverter;
import com.katalon.plugin.jira.core.entity.JiraIssue;
import com.katalon.plugin.jira.core.issue.UpdateTestCaseIssueDescription;
import com.katalon.plugin.jira.core.setting.JiraIntegrationSettingStore;
import com.katalon.plugin.jira.core.util.PlatformUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;

public class JiraTestCaseIntegrationView implements JiraUIComponent, TestCaseIntegrationView {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JiraTestCaseIntegrationView.class);
    private final Logger logger = (Logger)LoggerFactory.getLogger(JiraTestCaseIntegrationView.class);

    private ScrolledComposite scrolledComposite;
    private  Composite container;

    private Link lblDisplayKey;
    private Label lblDisplaySummary;
    private Label lblDisplayStatus;
    private Label lblDisplayDescription;

    private Optional<JiraIssue> linkedJiraIssue;
    private JiraIntegrationAuthenticationHandler authenticationHandler;

    private TestCaseEntity testCaseModel;

    private Button btnLinkJiraIssue;

    private Button btnEditJiraIssueLink;

    private ToolBar btnRemoveJiraIssueLink;

    @Override
    public Control onCreateView(Composite parent, PartActionService partActionService, TestCaseEntity testCaseEntity) {
        testCaseModel = testCaseEntity;
        loadJiraIssueLink();
        authenticationHandler = new JiraIntegrationAuthenticationHandler();

        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        container = new Composite(scrolledComposite, SWT.NONE);
        container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        container.setBackgroundMode(SWT.INHERIT_FORCE);
        container.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).spacing(15, 10).margins(10, 0).create());

        scrolledComposite.setContent(container);

        createUIComponents();
        scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return scrolledComposite;
    }

    private void createUIComponents() {
        Label lblKey = new Label(container, SWT.NONE);
        lblKey.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).create());
        lblKey.setText(ComposerJiraIntegrationMessageConstant.VIEW_LBL_KEY);

        Composite displayKeyContainer = new Composite(container, SWT.NONE);
        displayKeyContainer.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).spacing(1, 1).create());
        displayKeyContainer.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).create());
        lblDisplayKey = new Link(displayKeyContainer, SWT.NONE);
        lblDisplayKey.setToolTipText(ComposerJiraIntegrationMessageConstant.VIEW_TOOLTIP_VIEW_ISSUE_ON_JIRA);
        lblDisplayKey.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());

        btnLinkJiraIssue = new Button(displayKeyContainer, SWT.PUSH);
        btnLinkJiraIssue.setData(ControlUtil.IGNORE_SET_ENABLED_DATA_KEY, Boolean.TRUE);
        btnLinkJiraIssue.setText(ComposerJiraIntegrationMessageConstant.BTN_LINK_JIRA_ISSUE_LABEL);
        btnLinkJiraIssue.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).create());

        btnEditJiraIssueLink = new Button(displayKeyContainer, SWT.PUSH);
        btnEditJiraIssueLink.setText(ComposerJiraIntegrationMessageConstant.BTN_EDIT_JIRA_ISSUE_LINK_LABEL);
        GridData editButtonLayoutData = GridDataFactory.fillDefaults()
                .align(SWT.CENTER, SWT.CENTER)
                .indent(10, 0)
                .create();
        btnEditJiraIssueLink.setLayoutData(editButtonLayoutData);

        // Button don't support to remove the border, we can use a ToolBar with 1 Item to simulate a Button
        btnRemoveJiraIssueLink = new ToolBar(displayKeyContainer, SWT.FLAT);
        ToolItem item = new ToolItem(btnRemoveJiraIssueLink, SWT.PUSH);
        item.setText(ComposerJiraIntegrationMessageConstant.BTN_REMOVE_JIRA_ISSUE_LINK_LABEL);
        item.setForeground(Theme.removeButtonColor());
        GridData removeButtonLayoutData = GridDataFactory.fillDefaults()
                .align(SWT.CENTER, SWT.CENTER)
                .indent(10, 0)
                .create();
        btnRemoveJiraIssueLink.setLayoutData(removeButtonLayoutData);

        Label lblSummary = new Label(container, SWT.NONE);
        lblSummary.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
        lblSummary.setText(StringConstants.SUMMARY);

        lblDisplaySummary = new Label(container, SWT.WRAP);
        lblDisplaySummary.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

        Label lblStatus = new Label(container, SWT.NONE);
        lblStatus.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
        lblStatus.setText(StringConstants.STATUS);

        lblDisplayStatus = new Label(container, SWT.NONE);
        lblDisplayStatus.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).create());
        lblDescription.setText(StringConstants.DESCRIPTION);

        lblDisplayDescription = new Label(container, SWT.WRAP);
        lblDisplayDescription.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

        registerControlEvents();
        renderTestCase();
    }

    private void changeUiControlVisibilityWithJiraIssueLinkStage() {
        boolean jiraIssueLinked = linkedJiraIssue.isPresent();
        setControlVisibility(lblDisplayKey, jiraIssueLinked);
        setControlVisibility(btnLinkJiraIssue, !jiraIssueLinked);
        setControlVisibility(btnEditJiraIssueLink, jiraIssueLinked);
        setControlVisibility(btnRemoveJiraIssueLink, jiraIssueLinked);
    }

    private void setControlVisibility(Control control, boolean visible) {
        control.setVisible(visible);
        Object layoutDataRaw = control.getLayoutData();
        if (Objects.nonNull(layoutDataRaw) && layoutDataRaw instanceof GridData) {
            ((GridData) layoutDataRaw).exclude = !visible;
        }
    }

    private void registerControlEvents() {
        lblDisplayKey.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                linkedJiraIssue.ifPresent(jiraIssue -> {
                    try {
                        Program.launch(getHTMLLink(jiraIssue).toURL().toString());
                    } catch (IOException | URISyntaxException | GeneralSecurityException ex) {
                        logger.error("Fail to launch the app to handle the URL of the JIRA Issue ", ex);
                    }
                });
            }
        });

        btnLinkJiraIssue.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JiraIssueLinkDialog dialog = new JiraIssueLinkDialog(btnLinkJiraIssue.getShell(), JiraTestCaseIntegrationView.this::onLinkingToJiraIssue, null);
                dialog.open();
            }
        });

            // Add event handler for btnRemoveJiraIssueLink
        ToolItem removeItem = btnRemoveJiraIssueLink.getItem(0);
        removeItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                onRemovingToJiraIssue();
            }
        });

        btnEditJiraIssueLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                JiraIssueLinkDialog dialog = new JiraIssueLinkDialog(
                        btnLinkJiraIssue.getShell(), JiraTestCaseIntegrationView.this::onLinkingToJiraIssue,
                        linkedJiraIssue.map(JiraIssue::getKey).orElse(StringUtils.EMPTY));
                dialog.open();
            }
        });
    }

    private void populateJiraFieldValues() {
        ControlUtil.recursiveSetEnabled(container, linkedJiraIssue.isPresent());

        if (!linkedJiraIssue.isPresent()) {
            lblDisplayKey.setText(StringUtils.EMPTY);
            lblDisplaySummary.setText(StringUtils.EMPTY);
            lblDisplayStatus.setText(StringUtils.defaultString(StringUtils.EMPTY));
            lblDisplayDescription.setText(StringUtils.defaultString(StringUtils.EMPTY));
        } else {
            JiraIssue issue = linkedJiraIssue.get();
            lblDisplayKey.setText("<a>" + issue.getKey() + "</a>");
            Issue fields = issue.getFields();
            lblDisplaySummary.setText(StringUtils.defaultString(fields.getSummary()));
            lblDisplayStatus.setText(StringUtils.defaultString(fields.getStatus().getName()));
            lblDisplayDescription.setText(StringUtils.defaultString(fields.getDescription()));
        }

        Composite descriptionParent = lblDisplayDescription.getParent();
        descriptionParent.layout();
    }

    public boolean hasDocumentation() {
        return true;
    }

    public String getDocumentationUrl() {
        return ComposerJiraIntegrationMessageConstant.URL_TEST_CASE_INTEGRATION_JIRA;
    }

    private void onLinkingToJiraIssue(JiraIssueLinkDialog.Result dialogResult) {
        try {
            JiraCredential credential = getCredential();
            JiraIntegrationSettingStore settingStore = getSettingStore();
            JiraIssue issue = dialogResult.getJiraIssue();
            testCaseModel = JiraObjectToEntityConverter.updateTestCaseJiraIssueLink(issue, testCaseModel);
            UpdateTestCaseIssueDescription updateDescription = UpdateTestCaseIssueDescription.Builder.create()
                    .setTestCase(testCaseModel)
                    .setIssue(issue)
                    .setJiraCredential(credential)
                    .setSettingStore(settingStore)
                    .setOverrideTestCaseDescriptionFromIssue(dialogResult.getTestCaseDescriptionOverride())
                    .setKatalonCommentField(authenticationHandler.getKatalonCommentField(credential, settingStore))
                    .build();
            testCaseModel = JiraObjectToEntityConverter.updateTestCase(testCaseModel, updateDescription);
        }
        catch (JiraIntegrationException | IOException e) {
            // Turn checked exception to unchecked one to comply
            // with the Consumer interface
            throw new RuntimeException(e);
        }

        // Reload the UI to reflect the test case has been linked to JIRA issue
        loadJiraIssueLink();
        renderTestCase();
    }

    private void onRemovingToJiraIssue() {
        try {
            boolean result = openConfirmDialog(btnRemoveJiraIssueLink.getShell(), ComposerJiraIntegrationMessageConstant.BTN_REMOVE_JIRA_ISSUE_LINK_LABEL,
                    String.format("The link between this test case and the Jira issue %s will be removed. Are you sure you want to proceed?",
                            linkedJiraIssue.get().getKey()));
            if (result)
                testCaseModel = JiraObjectToEntityConverter.removeTestCaseJiraIssueLink(testCaseModel);
        }
        catch (JiraIntegrationException e) {
            // Turn checked exception to unchecked one to comply
            // with the Consumer interface
            throw new RuntimeException(e);
        }

        // Reload the UI to reflect the test case has been linked to JIRA issue
        loadJiraIssueLink();
        renderTestCase();
    }

    private void loadJiraIssueLink() {
        linkedJiraIssue = Optional.ofNullable(JiraObjectToEntityConverter.getJiraIssue(testCaseModel));
    }

    private void redrawJiraIssueKeyColumn() {
        Composite parent = lblDisplayKey.getParent();
        if (!linkedJiraIssue.isPresent()) {
            parent = btnLinkJiraIssue.getParent();
        }

        parent.layout();
        parent.pack();

        //set new composite size to scrolledComposite
        scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    private void renderTestCase() {
        changeUiControlVisibilityWithJiraIssueLinkStage();
        populateJiraFieldValues();
        redrawJiraIssueKeyColumn();
    }

    private boolean isEnableRemoveIntegration() {
        try {
            PlatformUtil.getPlatformController(TestCaseController.class)
                    .getClass().getMethod("removeIntegration", ProjectEntity.class, TestCaseEntity.class, String.class);
            return true;
        } catch (NoSuchMethodException e) {
            logger.warn("This Katalon Studio version does not support removeIntegration method");
            return false;
        }
    }

    private boolean openConfirmDialog(Shell parent, String title, String message) {
        MessageDialog dialog = new MessageDialog(
                parent,
                title,
                null, // image
                message,
                MessageDialog.QUESTION,
                new String[] {"Remove", "Cancel"},
                0); // 0 is the index of Remove button

        return dialog.open() == 0; // return true if user clicks Remove button
    }
}
