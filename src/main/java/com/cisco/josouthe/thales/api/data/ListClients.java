package com.cisco.josouthe.thales.api.data;

import java.util.List;

public class ListClients{
    public int skip, limit, total;
    public List<Token> resources;

    public boolean hasMore() {
        return this.total > this.limit;
    }

    public void add( ListClients other ) {
        this.resources.addAll( other.resources);
        this.limit += other.resources.size();
    }
}
