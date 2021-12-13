package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.Utility;
import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Alarm {
    public String id, uri, account, application, devAccount, createdAt, triggeredAt, name, state, description, severity,
            service, source, sourceID;
    boolean internal;

    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        Schema schema = new Schema("Thales_Alarms");
        schema.addField("id", "");
        schema.addField("name", "");
        schema.addField( "uri", "");
        schema.addField("state", "");
        schema.addField("account", "");
        schema.addField("application", "");
        schema.addField("devAccount", "");
        schema.addField("description", "");
        schema.addField("service", "");
        schema.addField("source", "");
        schema.addField("sourceId", "");
        schema.addField("severity", "");
        schema.addField("triggered_at", new Date());
        schema.addField("created_at", new Date());
        schema.addField("internal", true);
        return schema;
    }

    public Map<String,String> getSchemaData() {
        Map<String,String> data = new HashMap<>();
        data.put("id",id);
        data.put("name",name);
        data.put("uri",uri);
        data.put("state",state);
        data.put("account",account);
        data.put("application",application);
        data.put("devAccount",devAccount);
        data.put("description",description);
        data.put("service",service);
        data.put("source",source);
        data.put("sourceId",sourceID);
        data.put("severity",severity);
        try {
            data.put("triggered_at", String.valueOf(Utility.getDateFromString(triggeredAt)));
            data.put("created_at", String.valueOf(Utility.getDateFromString(createdAt)));
        } catch ( ParseException parseException ) {
            //ignored for now
        }
        data.put("internal", String.valueOf(internal));
        return data;
    }
}
