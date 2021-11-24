package com.cisco.josouthe.thales.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAlarms<T> extends GenericList<T> {

    public Map<String,Integer> getActiveAlarmCountsBySeverity() {
        Map<String,Integer> map = new HashMap<>();
        for( Alarm alarm : (List<Alarm>) resources ) {
            if( "on".equals(alarm.state) ) {
                int count = map.getOrDefault(alarm.severity, 0)+1;
                map.put(alarm.severity, count);
            }
        }
        return map;
    }
}
