package com.cisco.josouthe;

import com.cisco.josouthe.thales.data.*;
import com.cisco.josouthe.thales.APICalls;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class ThalesMonitor extends AManagedMonitor {
    private static final Logger logger = LogManager.getFormatterLogger();
    private String metricPrefix = "Custom Metrics|Thales Monitor|";
    private APICalls thalesAPIClient;

    @Override
    public TaskOutput execute(Map<String, String> configMap, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        String thalesURLString = configMap.get("thalesURL");
        if( !thalesURLString.endsWith("/") ) thalesURLString+="/";
        this.thalesAPIClient = new APICalls( thalesURLString, configMap.get("apiUser"), configMap.get("apiPassword") );
        if( configMap.containsKey("metricPrefix") ) metricPrefix = "Custom Metrics|"+ configMap.get("metricPrefix");

        printMetric("up", 1,
                MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                MetricWriter.METRIC_TIME_ROLLUP_TYPE_SUM,
                MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_COLLECTIVE
        );

        try {
            ListClientCerts listClientCerts = thalesAPIClient.listClientsCerts();
            printMetricCurrent("Total Client Certificates", listClientCerts.total);
            for( ClientCertificateInfo clientCertificateInfo : listClientCerts.resources ) {
                printMetricCurrent("Client Certificates|"+  clientCertificateInfo.name, clientCertificateInfo.daysUntilExpired() );
            }
        } catch (IOException e) {
            logger.warn("Error fetching Client Certificate Data, Exception: %s",e.getMessage());
        }

        try {
            ListAlarms listAlarms = thalesAPIClient.listAlarms();
            Map<String,Integer> alarmsMap = listAlarms.getActiveAlarmCountsBySeverity();
            for( String severity : alarmsMap.keySet() )
                printMetricCurrent("Alarms Active Severity "+ severity, alarmsMap.get(severity) );
        } catch (IOException ioException) {
            logger.warn("Error fetching Alarm Data, Exception: %s", ioException.getMessage());
        }

        try {
            ListTokens listTokens = thalesAPIClient.listTokens();
            Map<String,Integer> tokensMap = listTokens.getTokensCountsByStatus();
            for( String state : tokensMap.keySet() )
                printMetricCurrent("Tokens "+ state, tokensMap.get(state) );
        } catch (IOException ioException) {
            logger.warn("Error fetching Token Data, Exception: %s", ioException.getMessage());
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
        MetricWriter metricWriter = getMetricWriter(this.metricPrefix + metricName,
                aggregation,
                timeRollup,
                cluster
        );

        metricWriter.printMetric(String.valueOf(metricValue));
    }
}
