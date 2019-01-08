package com.katalon.plugin.jira.composer.report.provider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.slf4j.LoggerFactory;

import com.katalon.platform.ui.viewer.HyperLinkColumnLabelProvider;
import com.katalon.plugin.jira.composer.JiraUIComponent;
import com.katalon.plugin.jira.core.entity.JiraIssue;

import ch.qos.logback.classic.Logger;

public class JiraIssueIDLabelProvider extends HyperLinkColumnLabelProvider<JiraIssue> implements JiraUIComponent {
    
    private Logger logger = (Logger) LoggerFactory.getLogger(JiraIssueIDLabelProvider.class);

    public JiraIssueIDLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @Override
    protected void handleMouseDown(MouseEvent e, ViewerCell cell) {
        JiraIssue jiraIssue = (JiraIssue) cell.getElement();
        try {
            Program.launch(getHTMLLink(jiraIssue).toURL().toString());
        } catch (IOException | URISyntaxException | GeneralSecurityException ex) {
            logger.error("", e);
        }
    }

    @Override
    protected Class<JiraIssue> getElementType() {
        return JiraIssue.class;
    }

    @Override
    protected Image getImage(JiraIssue element) {
        return null;
    }

    @Override
    protected String getText(JiraIssue element) {
        return element.getKey();
    }

    @Override
    protected String getElementToolTipText(JiraIssue element) {
        try {
            return getHTMLLink(element).toString();
        } catch (URISyntaxException | IOException | GeneralSecurityException e) {
            logger.error("", e);
            return "";
        }
    }
}
