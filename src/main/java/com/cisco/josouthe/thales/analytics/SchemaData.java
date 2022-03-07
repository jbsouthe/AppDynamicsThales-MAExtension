package com.cisco.josouthe.thales.analytics;

import java.util.Map;

public interface SchemaData {

    public Schema getSchemaDefinition() throws AnalyticsSchemaException;
    public Map<String,String> getSchemaData();
}
