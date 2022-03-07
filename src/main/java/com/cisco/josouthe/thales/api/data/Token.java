package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.Utility;
import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;
import com.cisco.josouthe.thales.analytics.SchemaData;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token implements SchemaData {
    public String id, account, client_id, userId, username;
    public long expiresIn, revokeNotRefreshedIn;
    public boolean expired, revoked;
    public String refreshedAt, createdAt, updatedAt;
    public List<String> labels;

    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        Schema schema = new Schema("Thales_ClientTokens");
        schema.addField("id", "");
        schema.addField("account", "");
        schema.addField("client_id", "");
        schema.addField("userId", "");
        schema.addField("username", "");
        schema.addField("expiresIn", 1l);
        schema.addField("revokeNotRefreshedIn", 1l);
        schema.addField("labels", "");
        schema.addField("refreshed_at", new Date());
        schema.addField("created_at", new Date());
        schema.addField("updated_at", new Date());
        return schema;
    }

    public Map<String,String> getSchemaData() {
        Map<String,String> data = new HashMap<>();
        data.put("id",id);
        data.put("account",account);
        data.put("client_id",client_id);
        data.put("userId",userId);
        data.put("username",username);
        data.put("expiresIn",String.valueOf(expiresIn));
        data.put("revokeNotRefreshedIn",String.valueOf(revokeNotRefreshedIn));
        data.put("labels",String.valueOf(labels));
        try {
            data.put("refreshed_at", String.valueOf(Utility.getDateFromString(refreshedAt)));
            data.put("created_at", String.valueOf(Utility.getDateFromString(createdAt)));
            data.put("updated_at", String.valueOf(Utility.getDateFromString(updatedAt)));
        } catch ( ParseException parseException ) {
            //ignored for now
        }
        return data;
    }
}
