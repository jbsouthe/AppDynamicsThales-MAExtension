package com.cisco.josouthe.thales.snmp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.SnmpCompletableFuture;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SNMPAPI {
    private static final Logger logger = LogManager.getFormatterLogger();
    private TransportMapping transportMapping;
    private Snmp snmp;
    private TargetBuilder<?> targetBuilder;
    private Target<?> target;
    private Address address;
    private String contextName;
    private String communityName;
    private Map<String,String> oidMap;
    private int timeout=5000;
    private int retries=3;

    public SNMPAPI( String hostAddressString, String version, String communityName, String contextName, String securityName,
                    String authPassphrase, String privPassphrase, String authProtocol, String privProtocol, String oidFile
    ) throws TaskExecutionException, IOException {
        this.contextName=contextName;
        this.communityName=communityName;
        address = GenericAddress.parse(hostAddressString);
        SnmpBuilder snmpBuilder = new SnmpBuilder().udp().threads(2);
        switch(version.charAt(0)) {
            case '1':{
                throw new TaskExecutionException("SNMP Version not yet implemented: "+ version);
                }
            case '2':{
                if( "".equals(communityName) ) throw new TaskExecutionException("SNMP v2 being used but Community Name Parameter is null?");
                snmp = snmpBuilder.v2c().build();
                targetBuilder = snmpBuilder.target(address);
                target = targetBuilder
                        .v2c()
                        .community(new OctetString(communityName))
                        .timeout(timeout).retries(retries)
                        .build();
                break;
            }
            case '3': {
                List<String> errorParameters = new ArrayList<>();
                if( "".equals(securityName) ) errorParameters.add("Security Name");
                if( "".equals(authPassphrase)) errorParameters.add("Auth Passphrase");
                if( "".equals(authProtocol)) errorParameters.add("Auth Protocol");
                if( "".equals(privPassphrase)) errorParameters.add("Private Passphrase");
                if( "".equals(privProtocol)) errorParameters.add("Private Protocol");
                if( errorParameters.size() > 0 ) {
                    StringBuilder sb = new StringBuilder("SNMP v3 being used but missing needed parameter(s): ");
                    for( String param : errorParameters ) sb.append(param+",");
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    throw new TaskExecutionException(sb.toString());
                }
                snmp = snmpBuilder.v3().usm().build();
                byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(address, timeout);
                if( targetEngineID == null ) throw new TaskExecutionException("Could not discover the SNMP Authoritative Engine");
                targetBuilder = snmpBuilder.target(address);
                target = targetBuilder
                        .v3()
                        .user(securityName, targetEngineID)
                        .auth(getAuthProtocol(authProtocol)).authPassphrase(authPassphrase)
                        .priv(getPrivProtocol(privProtocol)).privPassphrase(privPassphrase)
                        .done()
                        .timeout(timeout).retries(retries)
                        .build();
                break;
                }
            default: throw new TaskExecutionException("Unknown SNMP Version? "+ version);
        }
        snmp.listen();
        this.oidMap = loadExternalSNMPOidList(oidFile);
    }

    private Map<String, String> loadExternalSNMPOidList(String oidFile) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File snmpoidsFile = new File(oidFile);
            BufferedReader reader = new BufferedReader(new FileReader(snmpoidsFile));
            StringBuilder jsonFileContent = new StringBuilder();
            while (reader.ready()) {
                jsonFileContent.append(reader.readLine());
            }
            Map<String,String> map = gson.fromJson(jsonFileContent.toString(), new HashMap<String, String>().getClass());
            if( map != null ) return map;
        } catch (IOException exception) {
            logger.warn("Exception while reading the external file %s, message: %s", oidFile, exception);
        }
        Map<String,String> map = new HashMap<>();
        map.put("1.3.6.1.4.1.2021.10.1.3.1", "1 minute load average");
        map.put("1.3.6.1.4.1.2021.10.1.3.2", "5 minute load average");
        map.put("1.3.6.1.4.1.2021.10.1.3.3", "15 minute load average");
        map.put("1.3.6.1.4.1.2021.11.11.0", "CPU Idle %");
        map.put("1.3.6.1.4.1.2021.11.9.0", "CPU User %");
        map.put("1.3.6.1.4.1.2021.4.4.0", "Swap Available");
        map.put("1.3.6.1.4.1.2021.4.3.0", "Swap Total");
        return map;
    }

    private TargetBuilder.AuthProtocol getAuthProtocol( String name ) throws TaskExecutionException {
        try {
            return TargetBuilder.AuthProtocol.valueOf(name);
        } catch (IllegalArgumentException exception ) {
            throw new TaskExecutionException("No AuthProtocol found for input name: "+ name);
        }
    }

    private TargetBuilder.PrivProtocol getPrivProtocol( String name ) throws TaskExecutionException {
        try {
            return TargetBuilder.PrivProtocol.valueOf(name);
        } catch (IllegalArgumentException exception ) {
            throw new TaskExecutionException("No PrivProtocol found for input name: "+ name);
        }
    }

    public Map<String,String> getAllData() throws TaskExecutionException {
        Map<String,String> data = new HashMap<>();
        for( VariableBinding variableBinding : getOIDs(this.oidMap.keySet().toArray( new String[0])))  {
            data.put( getOIDMetricName(variableBinding.getOid().toString()), variableBinding.toValueString());
        }
        return data;
    }

    public List<VariableBinding> getOIDs( String ... oids) throws TaskExecutionException {
        PDU pdu = null;
        switch (target.getVersion()) {
            case SnmpConstants.version2c: {
                pdu = targetBuilder.pdu().type(PDU.GETNEXT).oids(oids).build();
                break;
            }
            case SnmpConstants.version3: {
                pdu = targetBuilder.pdu().type(PDU.GETNEXT).oids(oids).contextName(contextName).build();
                break;
            }
        }
        SnmpCompletableFuture snmpRequestFuture = SnmpCompletableFuture.send(snmp, target, pdu);
        try {
            ResponseEvent responseEvent = snmpRequestFuture.getResponseEvent();
            if( responseEvent != null && responseEvent.getError() != null ) {
                logger.warn("Response returned error: %s",responseEvent.getError().toString());
                throw new TaskExecutionException(responseEvent.getError());
            }
            List<VariableBinding> vbs = snmpRequestFuture.get().getAll();
            return vbs;
        } catch (ExecutionException | InterruptedException ex) {
            if (ex.getCause() != null) {
                throw new TaskExecutionException(ex.getCause().getMessage());
            } else {
                throw new TaskExecutionException(ex.getMessage());
            }
        }
    }

    public String getOIDMetricName( String oid ) {
        return this.oidMap.get(oid);
    }

    public void close() {
        try {
            snmp.close();
        } catch (IOException ignore) {
            //ignored
        }
    }
}
