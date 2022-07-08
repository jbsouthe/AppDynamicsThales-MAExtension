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
/*
{
			"name": "External Backup Linux Server UAT",
			"id": "d318bebd-bdae-4f87-9453-0dcf80495273",
			"uri": "kylo:kylo:connectionmgmt:connections:external-backup-linux-server-uat-d318bebd-bdae-4f87-9453-0dcf80495273",
			"account": "kylo:kylo:admin:accounts:kylo",
			"createdAt": "2021-12-30T07:44:30.797233Z",
			"updatedAt": "2021-12-30T07:44:36.467634Z",
			"service": "scp",
			"category": "external-server",
			"products": [
				"backup/restore"
			],
			"last_connection_ok": true,
			"last_connection_at": "2021-12-30T07:44:36.467403Z"
		}
 */
public class Connection implements SchemaData {
    public String name, id, uri, account, service, category;
    public Boolean last_connection_ok;
    public List<String> products;
    public String createdAt, updatedAt, last_connection_at; //dates

    @Override
    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        Schema schema = new Schema("Thales_Connection");
        schema.addField("id", "");
        schema.addField("name", "");
        schema.addField("uri", "");
        schema.addField("account", "");
        schema.addField("service", "");
        schema.addField("category", "");
        schema.addField("products", "");
        schema.addField("created_at", new Date());
        schema.addField("updated_at", new Date());
        schema.addField("last_connection_at", new Date());
        schema.addField("last_connection_ok", true);
        return schema;
    }

    @Override
    public Map<String, String> getSchemaData() {
        Map<String,String> data = new HashMap<>();
        data.put("id",id);
        data.put("name",name);
        data.put("uri",uri);
        data.put("account",account);
        data.put("service",service);
        data.put("category",category);
        data.put("products",String.valueOf(products));
        data.put("service",service);
        try {
            data.put("updated_at", String.valueOf(Utility.getDateFromString(updatedAt)));
            data.put("last_connection_at", String.valueOf(Utility.getDateFromString(last_connection_at)));
            data.put("created_at", String.valueOf(Utility.getDateFromString(createdAt)));
        } catch ( ParseException parseException ) {
            //ignored for now
        }
        data.put("last_connection_ok", String.valueOf(last_connection_ok));
        return data;
    }
}
