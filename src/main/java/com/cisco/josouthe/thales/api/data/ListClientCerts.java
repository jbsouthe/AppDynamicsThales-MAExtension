package com.cisco.josouthe.thales.api.data;

import java.util.List;

public class ListClientCerts {
    public int skip, limit, total;
    public List<ClientCertificateInfo> resources;

    public boolean hasMore() {
        return this.total > this.limit;
    }

    public void add( ListClientCerts other ) {
        this.resources.addAll( other.resources);
        this.limit += other.resources.size();
    }
}
