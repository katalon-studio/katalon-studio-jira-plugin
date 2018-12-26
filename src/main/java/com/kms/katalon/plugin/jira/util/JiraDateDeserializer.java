package com.kms.katalon.plugin.jira.util;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JiraDateDeserializer implements JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonElement jsonElement, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        return null;
    }
}