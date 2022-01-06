package com.cisco.josouthe;

import com.cisco.josouthe.thales.configuration.ThalesEndpoint;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;

public class ThalesMonitorTest extends TestCase {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Before
    public void setUp() {
        Configurator.setAllLevels("", Level.ALL);
    }

    public void testConfigReading() throws TaskExecutionException {
        ThalesMonitor thalesMonitor = new ThalesMonitor();
        ThalesEndpoint[] thalesEndpoints = thalesMonitor.readThalesConfiguration("./Thales-Config-EXAMPLE.json");
        for( ThalesEndpoint thalesEndpoint : thalesEndpoints) {
            logger.info("Endpoint name: '%s' SNMP Version: %s", thalesEndpoint.name, thalesEndpoint.snmpEndpoint.version);
        }
    }

}