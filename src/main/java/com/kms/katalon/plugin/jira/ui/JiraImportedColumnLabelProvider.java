package com.kms.katalon.plugin.jira.ui;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.plugin.jira.constants.JiraIntegrationMessageConstants;
import com.kms.katalon.plugin.jira.entity.JiraIssue;

public class JiraImportedColumnLabelProvider extends TypeCheckedStyleCellLabelProvider<JiraIssue> {

    private Map<Long, JiraIssue> existedIssues;

    public JiraImportedColumnLabelProvider(int columnIndex) {
        super(columnIndex);
        existedIssues = Collections.emptyMap();
    }

    @Override
    protected Class<JiraIssue> getElementType() {
        return JiraIssue.class;
    }

    @Override
    protected Image getImage(JiraIssue issue) {
//        return isIssueImported(issue) ? ImageConstants.IMG_16_WARNING : null;
        //TODO image here
        return null;
    }

    @Override
    protected String getText(JiraIssue issue) {
        return StringUtils.EMPTY;
    }

    @Override
    protected String getElementToolTipText(JiraIssue issue) {
        return isIssueImported(issue) ? JiraIntegrationMessageConstants.CLMN_STATUS_IMPORTED : null;
    }

    private boolean isIssueImported(JiraIssue issue) {
        return existedIssues.containsKey(issue.getId());
    }

    public void setExistedIssues(Map<Long, JiraIssue> existedIssues) {
        this.existedIssues = existedIssues;
    }
}
