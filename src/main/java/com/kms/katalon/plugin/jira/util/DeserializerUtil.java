package com.kms.katalon.plugin.jira.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DeserializerUtil {
    public static Object getJsonValue(JsonElement js) {
        if (js == null || js.isJsonNull()) {
            return null;
        }
        if (js.isJsonPrimitive()) {
            JsonPrimitive primitive = js.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return primitive.getAsNumber();
            }
            if (primitive.isString()) {
                return primitive.getAsString();
            }
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }
        if (js.isJsonNull()) {
            return null;
        }
        if (js.isJsonObject()) {
            return js.getAsJsonObject();
        }
        if (js.isJsonArray()) {
            return js.getAsJsonArray();
        }
        return js.toString();
    }
}
