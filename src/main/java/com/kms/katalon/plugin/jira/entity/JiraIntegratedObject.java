package com.kms.katalon.plugin.jira.entity;

import java.util.HashMap;
import java.util.Map;

import com.kms.katalon.plugin.jira.constants.StringConstants;
import com.kms.katalon.plugin.jira.util.JsonUtil;

public abstract class JiraIntegratedObject {

    public Map<String, String> getIntegratedValue() {
        Map<String, String> properties = new HashMap<>();
        properties.put(StringConstants.INTEGRATED_VALUE_NAME, JsonUtil.toJson(this, false));
        return properties;
    }
}
