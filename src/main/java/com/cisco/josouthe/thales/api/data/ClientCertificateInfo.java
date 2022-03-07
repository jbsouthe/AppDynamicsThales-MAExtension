package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.Utility;
import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;
import com.cisco.josouthe.thales.analytics.SchemaData;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ClientCertificateInfo implements SchemaData {
    public String id, name, cert, cert_id, sha256_fingerprint, ca_id, state, issuer;
    public String updated_at, created_at, valid_until;

    public long daysUntilExpired() {
        return com.cisco.josouthe.Utility.getDaysUntil( valid_until );
    }

    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        Schema schema = new Schema("Thales_ClientCertificateInfo");
        schema.addField("id", "");
        schema.addField("name", "");
        schema.addField( "ca_id", "");
        schema.addField("state", "");
        schema.addField("issuer", "");
        schema.addField( "expiresInDays", 1);
        schema.addField("updated_at", new Date());
        schema.addField("created_at", new Date());
        schema.addField("valid_until", new Date());
        return schema;
    }

    public Map<String,String> getSchemaData() {
        Map<String,String> data = new HashMap<>();
        data.put("id",id);
        data.put("name",name);
        data.put("ca_id",ca_id);
        data.put("state",state);
        data.put("issuer",issuer);
        data.put("expiresInDays", String.valueOf(daysUntilExpired()) );
        try {
            data.put("updated_at", String.valueOf(Utility.getDateFromString(updated_at)));
            data.put("created_at", String.valueOf(Utility.getDateFromString(created_at)));
            data.put("valid_until", String.valueOf(Utility.getDateFromString(valid_until)));
        } catch (ParseException e) {
            //nothing for now
        }
        return data;
    }
}
