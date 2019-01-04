package com.katalon.plugin.jira.core.entity;

public class JiraEdittingIssue {

    private Metadata fields;

    public Metadata getFields() {
        return fields;
    }

    public void setFields(Metadata fields) {
        this.fields = fields;
    }

    public JiraEdittingIssue(String description) {
        this.fields = new Metadata(description);
    }

    public class Metadata {
        public Metadata(String description) {
            this.description = description;
        }

        private String description;

        public String getDescription() {
            return description;
        }
    }
}
