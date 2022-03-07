package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;
import com.cisco.josouthe.thales.analytics.SchemaData;

import java.util.Map;

public class Connection implements SchemaData {
    @Override
    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        return null;
    }

    @Override
    public Map<String, String> getSchemaData() {
        return null;
    }
}
