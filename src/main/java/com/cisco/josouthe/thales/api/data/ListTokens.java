package com.cisco.josouthe.thales.api.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListTokens {
    public int skip, limit, total;
    public List<Token> resources;

    public boolean hasMore() {
        return this.total > this.limit;
    }

    public void add( ListTokens other ) {
        this.resources.addAll( other.resources);
        this.limit += other.resources.size();
    }
    public Map<String, Integer> getTokensCountsByStatus() {
        Map<String, Integer> map = new HashMap<>();
        for( Token token : resources ) {
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
