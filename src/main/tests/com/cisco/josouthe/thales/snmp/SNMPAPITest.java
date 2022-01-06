package com.cisco.josouthe.thales.snmp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SNMPAPITest extends TestCase {
    private static final Logger logger = LogManager.getFormatterLogger();

    @Before
    public void setUp() {
        Configurator.setAllLevels("", Level.ALL);
    }

    public void testSNMPv1Creation() {
        try {
            Map<String,String> configMap = new HashMap<>();
            configMap.put("snmp_targetAddress","udp:localhost/162");
            configMap.put("snmp_version", "1");
            configMap.put("snmp_communityName", "public");
            configMap.put("snmp_contextName", "");
            configMap.put("snmp_securityName","admin");
            configMap.put("snmp_authPassphrase","password");
            configMap.put("snmp_privPassphrase","password");
            configMap.put("snmp_authProtocol","hmac384sha512");
            configMap.put("snmp_privProtocol", "aes256");
            configMap.put("snmp_oidFile", "./snmp-oids.json");
            SNMPAPI snmpapi = new SNMPAPI(configMap, null);
            assert false;
        } catch (Exception e) {
            assert true;
        }

    }

    public void testSNMPv2Creation() {
        try {
            Map<String,String> configMap = new HashMap<>();
            configMap.put("snmp_targetAddress","udp:localhost/162");
            configMap.put("snmp_version", "2c");
            configMap.put("snmp_communityName", "public");
            configMap.put("snmp_oidFile", "./snmp-oids.json");
            SNMPAPI snmpapi = new SNMPAPI(configMap, null);
            snmpapi.getAllData();
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

    }

    public void testSNMPv3Creation() {
        try {
            Map<String,String> configMap = new HashMap<>();
            configMap.put("snmp_targetAddress","udp:localhost/162");
            configMap.put("snmp_version", "3");
            configMap.put("snmp_communityName", "");
            configMap.put("snmp_contextName", "");
            configMap.put("snmp_securityName","admin");
            configMap.put("snmp_authPassphrase","password");
            //configMap.put("snmp_privPassphrase","password");
            configMap.put("snmp_authProtocol","hmac384sha512");
            //configMap.put("snmp_privProtocol", "aes256");
            configMap.put("snmp_oidFile", "./snmp-oids.json");
            SNMPAPI snmpapi = new SNMPAPI(configMap, null);
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

    public void testMIBMap() throws IOException {
        /*
        Map<String,String> oidMap = new HashMap<>();
        oidMap.put("1.3.6.1.4.1.2021.10.1.3.1", "1 minute load average");
        oidMap.put("1.3.6.1.4.1.2021.10.1.3.2", "5 minute load average");
        oidMap.put("1.3.6.1.4.1.2021.10.1.3.3", "15 minute load average");
        oidMap.put("1.3.6.1.4.1.2021.11.11.0", "CPU Idle %");
        oidMap.put("1.3.6.1.4.1.2021.11.9.0", "CPU User %");
        oidMap.put("1.3.6.1.4.1.2021.4.4.0", "Swap Available");
        oidMap.put("1.3.6.1.4.1.2021.4.3.0", "Swap Total");
         */
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File snmpoidsFile = new File("./snmp-oids.json");
        BufferedReader reader = new BufferedReader( new FileReader(snmpoidsFile));
        StringBuilder jsonFileContent = new StringBuilder();
        while( reader.ready() ) {
            jsonFileContent.append(reader.readLine());
        }
        Map<String,String> map = gson.fromJson(jsonFileContent.toString(), new HashMap<String,String>().getClass());
        if( map != null ) {
            System.out.println("Map Loaded:");
            for (String key : map.keySet())
                System.out.println(String.format("%s: %s", key, map.get(key)));
        }
        String json = gson.toJson(map);
        System.out.println(String.format("back to json: '%s'",json));
    }
}