package com.cisco.josouthe.thales.snmp;

import com.cisco.josouthe.thales.configuration.ThalesEndpoint;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;

public class SNMPAPITest extends TestCase {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Before
    public void setUp() {
        Configurator.setAllLevels("", Level.ALL);
    }

    public void testSNMPv1Creation() {
        try {
            ThalesEndpoint.SNMPEndpoint snmpEndpoint = new ThalesEndpoint().snmpEndpoint;
            snmpEndpoint.version="1";
            snmpEndpoint.targetAddress="udp:localhost/162";
            snmpEndpoint.communityName="public";
            SNMPAPI snmpapi = new SNMPAPI(snmpEndpoint, null);
            assert false;
        } catch (Exception e) {
            assert true;
        }

    }

    public void testSNMPv2Creation() {
        try {
            ThalesEndpoint.SNMPEndpoint snmpEndpoint = new ThalesEndpoint().snmpEndpoint;
            snmpEndpoint.version="2";
            snmpEndpoint.targetAddress="udp:localhost/162";
            snmpEndpoint.communityName="public";
            SNMPAPI snmpapi = new SNMPAPI(snmpEndpoint, null);
            snmpapi.getAllData();
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

    }

    public void testSNMPv3Creation() {
        try {
            ThalesEndpoint.SNMPEndpoint snmpEndpoint = new ThalesEndpoint().snmpEndpoint;
            snmpEndpoint.version="3";
            snmpEndpoint.targetAddress="udp:localhost/162";
            snmpEndpoint.communityName="";
            snmpEndpoint.contextName="";
            snmpEndpoint.securityName="admin";
            snmpEndpoint.authPassphrase="password";
            snmpEndpoint.authProtocol="hmac384sha512";
            SNMPAPI snmpapi = new SNMPAPI(snmpEndpoint, null);
            snmpapi.getAllData();
            assert true;
        } catch (Exception e) {
            logger.info("Exception: "+e);
            if( e.getMessage().equals("Could not discover the SNMP Authoritative Engine") ) {
                assert true;
            } else {
                e.printStackTrace();
                assert false;
            }
        }
    }

}