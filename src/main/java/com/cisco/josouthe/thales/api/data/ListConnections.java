package com.cisco.josouthe.thales.api.data;

import java.util.List;

public class ListConnections {
    public int skip, limit, total;
    public List<Connection> resources;

    public boolean hasMore() {
        return this.total > this.limit;
    }

    public void add( ListConnections other ) {
        this.resources.addAll( other.resources);
        this.limit += other.resources.size();
    }
}
