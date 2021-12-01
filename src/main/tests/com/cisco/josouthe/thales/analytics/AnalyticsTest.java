package com.cisco.josouthe.thales.analytics;

import com.cisco.josouthe.Utility;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsTest extends TestCase {

    public void testCreateAndInsertSchema() throws IOException, AnalyticsSchemaException {
        Analytics analytics = new Analytics(
                "https://analytics.api.appdynamics.com/",
                "southerland-test_65322e21-efed-4126-8827-920141a9ac21",
                "bd568573-9175-44a2-b258-6ca582696acf"
        );
        Schema schema = new Schema("Test");
        schema.addField("name", "");
        schema.addField( "number", 1f);
        schema.addField( "today", new Date() );

        Schema checkSchemaExists = analytics.getSchema("Test");
        if( checkSchemaExists.isErrorResponse() ) {
            System.out.println(String.format("Check Error: %d message: %s", checkSchemaExists.statusCode, checkSchemaExists.message));

            System.out.println("Schema does not exist");
            System.out.println("Schema: " + schema.getDefinitionJSON());
            String reply = analytics.createSchema(schema);
            System.out.println("Create Schema Reply: "+ reply);
        } else {
            System.out.println(String.format("Schema Exists: %s", checkSchemaExists.getDefinitionJSON()));
        }

        Map<String,String> data = new HashMap<>();
        data.put("name","Test Name");
        data.put("number","1.0");
        data.put("today", String.valueOf( Utility.now() ));

        String reply = analytics.insertSchema(schema, data);
        System.out.println("Insert Reply: "+ reply);

    }


}