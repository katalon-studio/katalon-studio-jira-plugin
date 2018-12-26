package com.kms.katalon.plugin.jira.ui;

import com.kms.katalon.plugin.jira.constants.StringConstants;
import com.kms.katalon.plugin.jira.entity.JiraIssueType;
import com.kms.katalon.plugin.jira.setting.StoredJiraObject;

public class DisplayedIssueTypeComboboxObject extends DisplayedComboboxObject<JiraIssueType> {

    public DisplayedIssueTypeComboboxObject(StoredJiraObject<JiraIssueType> storedObject) {
        super(storedObject);
    }

    @Override
    public int getPreferredIndex() {
        JiraIssueType[] jiraIssueTypes = getStoredObject().getJiraObjects();
        for (int index = 0; index < jiraIssueTypes.length; index++) {
            if (StringConstants.DF_ISSUE_TYPE_NAME.equalsIgnoreCase(jiraIssueTypes[index].getName())) {
                return index;
            }
        }
        return super.getPreferredIndex();
    }
}
