package com.cisco.josouthe.thales.api.data;

import java.util.List;

public class ListClusterNodeHealth {
    public int skip, limit, total;
    public List<ClusterNodeHealth> resources;
}
