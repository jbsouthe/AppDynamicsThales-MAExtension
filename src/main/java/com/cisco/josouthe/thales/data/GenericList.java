package com.cisco.josouthe.thales.data;

import java.util.List;

public class GenericList<T> {
    public int skip, limit, total;
    public List<T> resources;

    public boolean hasMore() {
        return this.total > this.limit;
    }

    public void add( GenericList<T> other ) {
        this.resources.addAll( other.resources);
        this.limit += other.resources.size();
    }
}
