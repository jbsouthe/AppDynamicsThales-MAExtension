package com.cisco.josouthe.thales.analytics;

import junit.framework.TestCase;

public class SchemaTest extends TestCase {

    public void gsonParserCreation() throws Exception {

    }

    public void testAddField() {
        Schema schema = new Schema("testSchema" );
        try {
            schema.addField("9invalid", new String());
        } catch (AnalyticsSchemaException e) {
            assert e != null;
        }

        try {
            schema.addField("valid_field", new String());
            assert true;
        } catch (AnalyticsSchemaException e) {
            e.printStackTrace();
        }
    }
}