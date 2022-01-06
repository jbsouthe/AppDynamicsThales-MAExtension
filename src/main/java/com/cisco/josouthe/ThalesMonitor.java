package com.cisco.josouthe;

import com.cisco.josouthe.thales.analytics.Analytics;
import com.cisco.josouthe.thales.analytics.AnalyticsSchemaException;
import com.cisco.josouthe.thales.analytics.Schema;
import com.cisco.josouthe.thales.api.APICalls;
import com.cisco.josouthe.thales.api.data.*;
import com.cisco.josouthe.thales.snmp.SNMPAPI;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

public class ThalesMonitor extends AManagedMonitor {
    private Logger logger = LogManager.getFormatterLogger();
    private String metricPrefix = "Custom Metrics|Thales Monitor|";
    private APICalls thalesAPIClient;
    private Analytics analyticsAPIClient;
    private SNMPAPI snmpApiClient;

    @Override
    public TaskOutput execute(Map<String, String> configMap, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        this.logger = taskExecutionContext.getLogger();
        this.thalesAPIClient = new APICalls( configMap.get("thalesURL"), configMap.get("apiUser"), configMap.get("apiPassword") );
        this.analyticsAPIClient = new Analytics( configMap.get("analytics_URL"), configMap.get("analytics_apiAccountName"), configMap.get("analytics_apiKey"));
        if( configMap.containsKey("metricPrefix") ) metricPrefix = "Custom Metrics|"+ configMap.get("metricPrefix");
        if( snmpApiClient == null && configMap.containsKey("snmp_targetAddress") && !"unconfigured".equals(configMap.getOrDefault("snmp_targetAddress", "unconfigured"))) {
            try {
                this.snmpApiClient = new SNMPAPI(configMap, taskExecutionContext.getTaskDir(), taskExecutionContext.getLogger());
            } catch (IOException ioException) {
                logger.warn(String.format("Could not configure SNMP settings, ignoring SNMP entirely :) "+ ioException.getMessage()));
            }
        }
        printMetric("up", 1,
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_SUM,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );

        try {
            ListClientCerts listClientCerts = thalesAPIClient.listClientsCerts();
            printMetricCurrent("Total Client Certificates", listClientCerts.total);
            ArrayList<Map<String,String>> data = new ArrayList<>();
            Schema schema = null;
            for( ClientCertificateInfo clientCertificateInfo : listClientCerts.resources ) {
                printMetricCurrent("Client Certificates|"+  clientCertificateInfo.name, clientCertificateInfo.daysUntilExpired() );
                if( schema == null ) {
                    schema = clientCertificateInfo.getSchemaDefinition();
                }
                try {
                    data.add(clientCertificateInfo.getSchemaData());
                } catch (ParseException e) {
                    logger.warn(String.format("Bad Date format in the client certificate data: %s", clientCertificateInfo.name));
                }
            }
            Schema checkSchema = analyticsAPIClient.getSchema(schema.name);
            if( checkSchema == null || !checkSchema.exists() ) analyticsAPIClient.createSchema(schema);
            analyticsAPIClient.insertSchema(schema, data);
        } catch (IOException e) {
            logger.warn(String.format("Error fetching Client Certificate Data, Exception: %s",e.getMessage()));
        } catch (AnalyticsSchemaException e) {
            logger.warn(String.format("Analytics Schema could not be determined for Client Certificate Info, Message: %s", e.getMessage()));
        }

        try {
            ListAlarms listAlarms = thalesAPIClient.listAlarms();
            Map<String,Integer> alarmsMap = listAlarms.getActiveAlarmCountsBySeverity();
            for( String severity : alarmsMap.keySet() )
                printMetricCurrent("Alarms Active Severity "+ severity, alarmsMap.get(severity) );
            ArrayList<Map<String,String>> data = new ArrayList<>();
            Schema schema = null;
            for(Alarm alarm : listAlarms.resources ) {
                if( schema == null ) schema = alarm.getSchemaDefinition();
                data.add(alarm.getSchemaData());
            }
            Schema checkSchema = analyticsAPIClient.getSchema(schema.name);
            if( checkSchema == null || !checkSchema.exists() ) analyticsAPIClient.createSchema(schema);
            analyticsAPIClient.insertSchema(schema, data);
        } catch (IOException ioException) {
            logger.warn(String.format("Error fetching Alarm Data, Exception: %s", ioException.getMessage()));
        } catch (AnalyticsSchemaException e) {
            logger.warn(String.format("Analytics Schema could not be determined for Alarms, Message: %s", e.getMessage()));
        }

        try {
            ListTokens listTokens = thalesAPIClient.listTokens();
            Map<String,Integer> tokensMap = listTokens.getTokensCountsByStatus();
            for( String state : tokensMap.keySet() )
                printMetricCurrent("Tokens "+ state, tokensMap.get(state) );
        } catch (IOException ioException) {
            logger.warn(String.format("Error fetching Token Data, Exception: %s", ioException.getMessage()));
        }

        if( snmpApiClient != null ) {
            Map<String,String> snmpData = snmpApiClient.getAllData();
            for( String key : snmpData.keySet()) {
                printMetricCurrent(key, snmpData.get(key));
            }
            //snmpApiClient.close();
        }
        return new TaskOutput("Thales Monitor Metric Upload Complete");
    }

    public void printMetricCurrent(String metricName, Object metricValue) {
        printMetric(metricName, metricValue,
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );
    }
    public void printMetricSum(String metricName, Object metricValue) {
        printMetric(metricName, metricValue,
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_SUM,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );
    }

    public void printMetric(String metricName, Object metricValue, String aggregation, String timeRollup, String cluster)
    {
        logger.info(String.format("Print Metric: '%s%s'=%d",this.metricPrefix, metricName, metricValue));
        MetricWriter metricWriter = getMetricWriter(this.metricPrefix + metricName,
                aggregation,
                timeRollup,
                cluster
        );

        metricWriter.printMetric(String.valueOf(metricValue));
    }
}
