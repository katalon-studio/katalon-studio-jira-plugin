package com.kms.katalon.platform.entity.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.TestCase;

public class TestCaseImpl implements TestCase {

    private String id;
    private String name;
    private String fileLocation;
    private String folderLocation;
    private List<Integration> integrations = new ArrayList<>();
    private String description;
    private String comment;
    private InputStream scriptContent;
    
    @Override
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getFileLocation() {
        return fileLocation;
    }
    
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
    
    @Override
    public String getFolderLocation() {
        return folderLocation;
    }
    public void setFolderLocation(String folderLocation) {
        this.folderLocation = folderLocation;
    }
    
    @Override
    public List<Integration> getIntegrations() {
        return integrations;
    }
    public void setIntegrations(List<Integration> integrations) {
        this.integrations = integrations;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public InputStream getScriptContent() {
        return scriptContent;
    }
    
    public void setScriptContent(InputStream scriptContent) {
        this.scriptContent = scriptContent;
    }
}
