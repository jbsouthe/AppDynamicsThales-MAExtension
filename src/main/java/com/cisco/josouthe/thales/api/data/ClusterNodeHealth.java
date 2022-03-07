package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;
import com.cisco.josouthe.thales.analytics.SchemaData;

import java.util.HashMap;
import java.util.Map;

public class ClusterNodeHealth implements SchemaData {
    public String nodeID, host, publicAddress, nodeName;
    public int port;
    public boolean isThisNode;
    public Status status;

    public class Status {
        public String code, description;
    }

    public Schema getSchemaDefinition() throws AnalyticsSchemaException {
        Schema schema = new Schema("Thales_ClusterNode");
        schema.addField("nodeID", "");
        schema.addField("host", "");
        schema.addField("publicAddress", "");
        schema.addField("nodeName", "");
        schema.addField("port", 1l);
        schema.addField("isThisNode", true);
        schema.addField("status_code", "");
        schema.addField("status_description", "");
        return schema;
    }

    public Map<String,String> getSchemaData() {
        Map<String,String> data = new HashMap<>();
        data.put("nodeID",nodeID);
        data.put("host",host);
        data.put("publicAddress",publicAddress);
        data.put("nodeName",nodeName);
        data.put("port",String.valueOf(port));
        data.put("isThisNode",String.valueOf(isThisNode));
        data.put("status_code",String.valueOf(status.code));
        data.put("status_description",String.valueOf(status.description));
        return data;
    }
}
