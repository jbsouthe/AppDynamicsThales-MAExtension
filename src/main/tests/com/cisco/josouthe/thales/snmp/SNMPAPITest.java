package com.cisco.josouthe.thales.snmp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SNMPAPITest extends TestCase {

    public void testSNMPv1Creation() {
        try {
            SNMPAPI snmpapi = new SNMPAPI("udp:localhost/162", "1", null,
                    null, null, null, null, null, null, null);
            assert false;
        } catch (Exception e) {
            assert true;
        }

    }

    public void testSNMPv2Creation() {
        try {
            SNMPAPI snmpapi = new SNMPAPI("udp:localhost/162", "2", "public",
                    null, null, null, null, null, null, null);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }

    }

    public void testSNMPv3Creation() {
        try {
            SNMPAPI snmpapi = new SNMPAPI("udp:localhost/162", "3", null,
                    null, "user_name", "authpass", "privpass",
                    "hmac384sha512", "aes256", null);
            assert true;
        } catch (Exception e) {
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