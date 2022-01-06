package com.cisco.josouthe.thales.snmp;

import com.cisco.josouthe.thales.configuration.ThalesEndpoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.SnmpCompletableFuture;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SNMPAPI {
    private Logger logger = LogManager.getFormatterLogger();
    private TransportMapping transportMapping;
    private Snmp snmp;
    private TargetBuilder<?> targetBuilder;
    private Address address;
    private String contextName;
    private String communityName;
    private Map<String,String> oidMap;
    private int timeout=5000;
    private int retries=3;
    private int snmpVersion;

    public SNMPAPI(ThalesEndpoint thalesEndpoint, TaskExecutionContext taskExecutionContext ) throws TaskExecutionException, IOException {
        if( taskExecutionContext != null ) this.logger=taskExecutionContext.getLogger();
        ThalesEndpoint.SNMPEndpoint snmpEndpoint = thalesEndpoint.getSnmpEndpoint();
        this.address = GenericAddress.parse(snmpEndpoint.targetAddress);
        this.communityName = snmpEndpoint.communityName;
        SnmpBuilder snmpBuilder = new SnmpBuilder().udp().threads(2);
        switch(snmpEndpoint.version.charAt(0)) {
            case '1':{
                this.snmpVersion = SnmpConstants.version1;
                throw new TaskExecutionException("SNMP Version not yet implemented: "+ snmpEndpoint.version);
            }
            case '2':{
                this.snmpVersion = SnmpConstants.version2c;
                if( "".equals(communityName) ) throw new TaskExecutionException("SNMP v2 being used but Community Name Parameter is null?");
                snmp = snmpBuilder.v2c().build();
                targetBuilder = snmpBuilder.v2c().target(address)
                        .community(new OctetString(communityName))
                        .timeout(timeout).retries(retries);
                break;
            }
            case '3': {
                this.snmpVersion = SnmpConstants.version3;
                List<String> errorParameters = new ArrayList<>();
                if( "".equals(snmpEndpoint.securityName) ) errorParameters.add("Security Name");
                if( errorParameters.size() > 0 ) {
                    StringBuilder sb = new StringBuilder("SNMP v3 being used but missing needed parameter(s): ");
                    for( String param : errorParameters ) sb.append(param+",");
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    throw new TaskExecutionException(sb.toString());
                }
                snmp = snmpBuilder.v3().usm().build();
                byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(address, timeout);
                if( targetEngineID == null ) throw new TaskExecutionException("Could not discover the SNMP Authoritative Engine");
                targetBuilder = snmpBuilder.v3().target(address);
                TargetBuilder<?>.DirectUserBuilder directUserBuilder = targetBuilder.user(snmpEndpoint.securityName, targetEngineID);
                if( snmpEndpoint.authPassphrase != null )
                    directUserBuilder = directUserBuilder.auth(getAuthProtocol(snmpEndpoint.authProtocol)).authPassphrase(snmpEndpoint.authPassphrase);
                if( snmpEndpoint.privPassphrase != null )
                    directUserBuilder = directUserBuilder.priv(getPrivProtocol(snmpEndpoint.privProtocol)).privPassphrase(snmpEndpoint.privPassphrase);
                targetBuilder = directUserBuilder.done().timeout(timeout).retries(retries);
                break;
            }
            default: throw new TaskExecutionException("Unknown SNMP Version? "+ snmpEndpoint.version);
        }
        snmp.listen();
        this.oidMap = snmpEndpoint.oids;
        if(logger.isDebugEnabled()) {
            Target<?> target = targetBuilder.build();
            target.setVersion(this.snmpVersion);
            logger.debug(String.format("snmp target(%s): %s", getVersionString(target.getVersion()), target.toString()));
        }
        logger.info(String.format("Initialized SNMP API for version %s",snmpEndpoint.version));
    }

    public SNMPAPI(Map<String,String> configMap, TaskExecutionContext taskExecutionContext) throws TaskExecutionException, IOException {
        if( taskExecutionContext != null ) this.logger=taskExecutionContext.getLogger();
        this.communityName=configMap.getOrDefault("snmp_communityName", "public");
        this.contextName=configMap.getOrDefault("snmp_contextName", "");
        String securityName=configMap.get("snmp_securityName");
        String authPassphrase=configMap.getOrDefault("snmp_authPassphrase", "_unset");
        String privPassphrase=configMap.getOrDefault("snmp_privPassphrase", "_unset");
        String authProtocol=configMap.getOrDefault("snmp_authProtocol", "hmac384sha512");
        String privProtocol=configMap.getOrDefault("snmp_privProtocol", "aes256");
        String oidFile=configMap.getOrDefault("snmp_oidFile", "./snmp-oids.json");
        if( taskExecutionContext!=null && !oidFile.startsWith("/") ) oidFile = taskExecutionContext.getTaskDir() +"/"+ oidFile;
        address = GenericAddress.parse(configMap.get("snmp_targetAddress"));
        String version = configMap.getOrDefault("snmp_version", "2");
        SnmpBuilder snmpBuilder = new SnmpBuilder().udp().threads(2);
        switch(version.charAt(0)) {
            case '1':{
                this.snmpVersion = SnmpConstants.version1;
                throw new TaskExecutionException("SNMP Version not yet implemented: "+ version);
                }
            case '2':{
                this.snmpVersion = SnmpConstants.version2c;
                if( "".equals(communityName) ) throw new TaskExecutionException("SNMP v2 being used but Community Name Parameter is null?");
                snmp = snmpBuilder.v2c().build();
                targetBuilder = snmpBuilder.v2c().target(address)
                        .community(new OctetString(communityName))
                        .timeout(timeout).retries(retries);
                break;
            }
            case '3': {
                this.snmpVersion = SnmpConstants.version3;
                List<String> errorParameters = new ArrayList<>();
                if( "".equals(securityName) ) errorParameters.add("Security Name");
                if( errorParameters.size() > 0 ) {
                    StringBuilder sb = new StringBuilder("SNMP v3 being used but missing needed parameter(s): ");
                    for( String param : errorParameters ) sb.append(param+",");
                    sb.deleteCharAt(sb.lastIndexOf(","));
                    throw new TaskExecutionException(sb.toString());
                }
                snmp = snmpBuilder.v3().usm().build();
                byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(address, timeout);
                if( targetEngineID == null ) throw new TaskExecutionException("Could not discover the SNMP Authoritative Engine");
                targetBuilder = snmpBuilder.v3().target(address);
                TargetBuilder<?>.DirectUserBuilder directUserBuilder = targetBuilder.user(securityName, targetEngineID);
                if( !"_unset".equals(authPassphrase) )
                    directUserBuilder = directUserBuilder.auth(getAuthProtocol(authProtocol)).authPassphrase(authPassphrase);
                if( !"_unset".equals(privPassphrase) )
                    directUserBuilder = directUserBuilder.priv(getPrivProtocol(privProtocol)).privPassphrase(privPassphrase);
                targetBuilder = directUserBuilder.done().timeout(timeout).retries(retries);
                break;
                }
            default: throw new TaskExecutionException("Unknown SNMP Version? "+ version);
        }
        snmp.listen();
        this.oidMap = loadExternalSNMPOidList(oidFile);
        if(logger.isDebugEnabled()) {
            Target<?> target = targetBuilder.build();
            target.setVersion(this.snmpVersion);
            logger.debug(String.format("snmp target(%s): %s", getVersionString(target.getVersion()), target.toString()));
        }
        logger.info(String.format("Initialized SNMP API for version %s",version));
    }


    private String getVersionString( int v ) {
        switch (v) {
            case SnmpConstants.version1: return "v1";
            case SnmpConstants.version2c: return "v2c";
            case SnmpConstants.version3: return "v3";
            default: return "unknown-version";
        }
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
            logger.warn(String.format("Exception while reading the external file %s, message: %s", oidFile, exception));
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
        for( VariableBinding variableBinding : getOIDs(this.oidMap.keySet()))  {
            data.put( getOIDMetricName(variableBinding.getOid().toString()), variableBinding.toValueString());
        }
        return data;
    }

    public List<VariableBinding> getOIDs( Set<String> oids) throws TaskExecutionException {
        logger.debug(String.format("getOIDs beginning(%d): %s",oids.size(), oids));
        PDU pdu = null;
        Target<?> target = this.targetBuilder.build();
        target.setVersion(this.snmpVersion);
        logger.info(String.format("Target version %s for target: %s",target.getVersion(),target.toString()));
        switch (target.getVersion()) {
            case SnmpConstants.version1:
            case SnmpConstants.version2c: {
                pdu = targetBuilder
                        .pdu()
                        .type(PDU.GETNEXT)
                        .build();
                break;
            }
            case SnmpConstants.version3: {
                pdu = targetBuilder
                        .pdu()
                        .type(PDU.GETNEXT)
                        .contextName(contextName)
                        .build();
                break;
            }
            default: {
                throw new TaskExecutionException(String.format("SNMP version not yet implemented: %s",target.getVersion()));
            }
        }
        for( String oidName : oids ) {
            pdu.addOID( new VariableBinding( new OID(oidName)));
        }
        logger.debug(String.format("Request PDU: %s", pdu));
        SnmpCompletableFuture snmpRequestFuture = SnmpCompletableFuture.send(snmp, target, pdu);
        logger.debug(String.format("SnmpCompletableFuture created: %s",snmpRequestFuture.toString()));
        try {
            PDU responsePDU = snmpRequestFuture.get();
            logger.debug(String.format("ResponsePDU: %s SnmpCompletableFuture: %s",responsePDU, snmpRequestFuture));
            if( responsePDU.getErrorStatus() != PDU.noError ) {
                logger.warn(String.format("Response returned error: %s",responsePDU.getErrorStatusText()));
                throw new TaskExecutionException(responsePDU.getErrorStatusText());
            }
            List<VariableBinding> vbs = responsePDU.getAll();
            logger.debug(String.format("List<VariableBinding> returned with size: %d",(vbs==null?0:vbs.size())));
            return vbs;
        } catch (Exception ex) {
            if (ex.getCause() != null) {
                logger.warn(String.format("Error in processing: %s",ex.getCause().getMessage(),ex.getCause()));
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
