package com.cisco.josouthe.thales.snmp;

import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.fluent.SnmpBuilder;
import org.snmp4j.fluent.SnmpCompletableFuture;
import org.snmp4j.fluent.TargetBuilder;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SNMPAPI {
    private TransportMapping transportMapping;
    private Snmp snmp;
    private TargetBuilder<?> targetBuilder;
    private Target<?> target;
    private Address address;
    private String contextName;
    private Map<String,String> oidMap;

    public SNMPAPI( String hostAddressString,  String version, String contextName, String securityName,
                    String authPassphrase, String privPassphrase) throws TaskExecutionException, IOException {
        this.contextName=contextName;
        address = GenericAddress.parse(hostAddressString);
        SnmpBuilder snmpBuilder = new SnmpBuilder();
        switch(version) {
            case "1":
            case "2": {
                throw new TaskExecutionException("SNMP Version not yet implemented: "+ version);
                }
            case "3": {
                snmp = snmpBuilder.udp().v3().usm().threads(2).build();
                byte[] targetEngineID = snmp.discoverAuthoritativeEngineID(address, 1000);
                if( targetEngineID == null ) throw new TaskExecutionException("Could not discover the SNMP Authoritative Engine");
                targetBuilder = snmpBuilder.target(address);
                target = targetBuilder
                        .user(securityName, targetEngineID)
                        .auth(TargetBuilder.AuthProtocol.hmac192sha256).authPassphrase(authPassphrase)
                        .priv(TargetBuilder.PrivProtocol.aes128).privPassphrase(privPassphrase)
                        .done()
                        .timeout(500).retries(1)
                        .build();
                break;
                }
            default: throw new TaskExecutionException("Unknown SNMP Version? "+ version);
        }
        snmp.listen();
        this.oidMap = new HashMap<>();
        this.oidMap.put(".1.3.6.1.4.1.2021.10.1.3.1", "1 minute load average");
        this.oidMap.put(".1.3.6.1.4.1.2021.10.1.3.2", "5 minute load average");
        this.oidMap.put(".1.3.6.1.4.1.2021.10.1.3.3", "15 minute load average");
        this.oidMap.put(".1.3.6.1.4.1.2021.11.11.0", "CPU Idle %");
        this.oidMap.put(".1.3.6.1.4.1.2021.11.9.0", "CPU User %");
        this.oidMap.put(".1.3.6.1.4.1.2021.4.4.0", "Swap Available");
        this.oidMap.put(".1.3.6.1.4.1.2021.4.3.0", "Swap Total");
    }

    public Map<String,String> getAllData() throws TaskExecutionException {
        Map<String,String> data = new HashMap<>();
        for( VariableBinding variableBinding : getOIDs(this.oidMap.keySet().toArray( new String[0])))  {
            data.put( getOIDMetricName(variableBinding.getOid().toString()), variableBinding.toValueString());
        }
        return data;
    }

    public List<VariableBinding> getOIDs( String ... oids) throws TaskExecutionException {
        PDU pdu = targetBuilder.pdu().type(PDU.GETNEXT).oids(oids).contextName(contextName).build();
        SnmpCompletableFuture snmpRequestFuture = SnmpCompletableFuture.send(snmp, target, pdu);
        try {
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
