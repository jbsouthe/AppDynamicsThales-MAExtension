package com.cisco.josouthe.thales.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class ThalesEndpointTest extends TestCase {
    private static final Logger logger = LogManager.getFormatterLogger();
    @Before
    public void setUp() {
        Configurator.setAllLevels("", Level.ALL);
    }

    public void testThalesConfigurationJSONCreation() {
        List<ThalesEndpoint> thalesEndpoints = new ArrayList<>();

        ThalesEndpoint thalesEndpoint = new ThalesEndpoint();
        thalesEndpoint.url = "https://thalesmachine/";
        thalesEndpoint.user = "user";
        thalesEndpoint.password = "pass";
        thalesEndpoint.snmpEndpoint.targetAddress = "udp:hostname/162";
        thalesEndpoint.snmpEndpoint.version = "2";
        thalesEndpoint.snmpEndpoint.communityName = "public";
        thalesEndpoints.add(thalesEndpoint);

        ThalesEndpoint te = new ThalesEndpoint();
        te.url = "https://secondmachine/";
        te.user = "user";
        te.password = "pass";
        te.snmpEndpoint.version="3";
        te.snmpEndpoint.targetAddress="udp:secondmachine/162";
        te.snmpEndpoint.authPassphrase="authpass";
        thalesEndpoints.add(te);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(thalesEndpoints.toArray(new ThalesEndpoint[0]));
        logger.info("ThalesEndpoint JSON: %s",json);
    }
}