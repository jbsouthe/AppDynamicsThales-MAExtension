package com.cisco.josouthe.thales.data;

import com.cisco.josouthe.Utility;

public class ClientCertificateInfo {
    public String id, name, cert, cert_id, sha256_fingerprint, ca_id, state, issuer;
    public String updated_at, created_at, valid_until;

    public long daysUntilExpired() {
        return Utility.getDaysUntil( valid_until );
    }
}
