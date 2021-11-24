package com.cisco.josouthe.thales.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListTokens<T> extends GenericList<T> {

    public Map<String, Integer> getTokensCountsByStatus() {
        Map<String, Integer> map = new HashMap<>();
        for( Token token : (List<Token>) resources ) {
            if( !token.expired && !token.revoked ) {
                int count = map.getOrDefault("active", 0) +1;
                map.put("active", count);
            } else if(token.expired) {
                int count = map.getOrDefault("expired", 0) +1;
                map.put("expired", count);
            } else if(token.revoked) {
                int count = map.getOrDefault("revoked", 0) +1;
                map.put("revoked", count);
            }
        }
        return map;
    }
}
