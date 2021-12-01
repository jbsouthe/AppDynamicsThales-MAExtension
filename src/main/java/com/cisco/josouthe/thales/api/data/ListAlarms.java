package com.cisco.josouthe.thales.api.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAlarms {
    public int skip, limit, total;
    public List<Alarm> resources;

    public boolean hasMore() {
        return this.total > this.limit;
    }

    public void add( ListAlarms other ) {
        this.resources.addAll( other.resources);
        this.limit += other.resources.size();
    }

    public Map<String,Integer> getActiveAlarmCountsBySeverity() {
        Map<String,Integer> map = new HashMap<>();
        for( Alarm alarm : resources ) {
            if( "on".equals(alarm.state) ) {
                int count = map.getOrDefault(alarm.severity, 0)+1;
                map.put(alarm.severity, count);
            }
        }
        return map;
    }
}
