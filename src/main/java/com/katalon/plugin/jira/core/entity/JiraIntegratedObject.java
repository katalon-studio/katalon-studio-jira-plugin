package com.katalon.plugin.jira.core.entity;

import java.util.HashMap;
import java.util.Map;

import com.katalon.plugin.jira.core.constant.StringConstants;
import com.katalon.plugin.jira.core.util.JsonUtil;


public abstract class JiraIntegratedObject {

    public Map<String, String> getIntegratedValue() {
        Map<String, String> properties = new HashMap<>();
        properties.put(StringConstants.INTEGRATED_VALUE_NAME, JsonUtil.toJson(this, false));
        return properties;
    }
}
