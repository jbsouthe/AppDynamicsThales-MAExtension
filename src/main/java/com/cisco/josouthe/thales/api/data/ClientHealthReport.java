package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;
import com.cisco.josouthe.thales.analytics.SchemaData;

import java.util.HashMap;
import java.util.Map;

public class ClientHealthReport implements SchemaData {
    public String client_name, account, os_type, os_sub_type, os_kernel, client_version, status;
    public long total_gp, enabled_gp;

    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        Schema schema = new Schema("Thales_ClientHealth");
        schema.addField("client_name", "");
        schema.addField("account", "");
        schema.addField("os_type", "");
        schema.addField("os_sub_type", "");
        schema.addField("os_kernel", "");
        schema.addField("client_version", "");
        schema.addField("status", "");
        schema.addField("total_gp", 1l);
        schema.addField("enabled_gp", 1l);
        return schema;
    }

    public Map<String,String> getSchemaData() {
        Map<String,String> data = new HashMap<>();
        data.put("client_name",client_name);
        data.put("account", account);
        data.put("os_type",os_type);
        data.put("os_sub_type",os_sub_type);
        data.put("os_kernel",os_kernel);
        data.put("client_version",client_version);
        data.put("os_kernel",os_kernel);
        data.put("status",status);
        data.put("total_gp",String.valueOf(total_gp));
        data.put("enabled_gp",String.valueOf(enabled_gp));
        return data;
    }
}
