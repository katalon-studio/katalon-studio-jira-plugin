package com.kms.katalon.plugin.jira.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.kms.katalon.plugin.jira.entity.ImprovedIssue;

public class ImprovedIssueDeserializer implements JsonDeserializer<ImprovedIssue> {

    @Override
    public ImprovedIssue deserialize(JsonElement jsonElement, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        ImprovedIssue issue = new GsonBuilder().registerTypeAdapter(DateTime.class, new JiraDateDeserializer())
                .create()
                .fromJson(jsonElement, ImprovedIssue.class);

        Map<String, Object> customFields = new HashMap<>(issue.getCustomFields());

        jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
            String fieldName = entry.getKey();
            if (fieldName.startsWith("custom")) {
                customFields.put(fieldName, DeserializerUtil.getJsonValue(entry.getValue()));
            }
        });
        issue.setCustomFields(customFields);
        return issue;
    }
}
