package com.cisco.josouthe.thales.api.data;

public class ClusterNodeHealth {
    public String nodeID, host, publicAddress, nodeName;
    public int port;
    public boolean isThisNode;
    public Status status;

    public class Status {
        public String code, description;
    }
}
