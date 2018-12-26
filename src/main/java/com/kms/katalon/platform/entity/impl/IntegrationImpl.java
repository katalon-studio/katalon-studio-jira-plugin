package com.kms.katalon.platform.entity.impl;

import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.IntegrationType;

public class IntegrationImpl implements Integration {
    
    private String productName;
    private Map<String, String> properties;
    private IntegrationType type;

    @Override
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public IntegrationType getType() {
        return type;
    }
    
    public void setType(IntegrationType type) {
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productName == null) ? 0 : productName.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Integration)) {
            return false;
        }
        Integration that = (Integration) obj;
        return new EqualsBuilder().append(this.getProductName(), that.getProductName())
                .append(this.getProperties(), that.getProperties()).append(this.getType(), that.getType()).isEquals();
    }

}
