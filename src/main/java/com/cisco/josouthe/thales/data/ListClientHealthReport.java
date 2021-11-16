package com.cisco.josouthe.thales.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListClientHealthReport {
    public int skip, limit, total;
    public List<ClientHealthReport> resources;
    public Map<String,Integer> getCountsByStatus() {
        Map<String,Integer> map = new HashMap<>();
        for( ClientHealthReport clientHealthReport : resources ) {
            int count = map.getOrDefault(clientHealthReport.status, 0)+1;
            map.put(clientHealthReport.status, count);
        }
        return map;
    }
}
