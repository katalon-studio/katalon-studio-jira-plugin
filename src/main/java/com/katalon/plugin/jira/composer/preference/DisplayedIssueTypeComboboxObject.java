package com.katalon.plugin.jira.composer.preference;

import com.katalon.plugin.jira.composer.constant.StringConstants;
import com.katalon.plugin.jira.core.entity.JiraIssueType;
import com.katalon.plugin.jira.core.setting.StoredJiraObject;

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
