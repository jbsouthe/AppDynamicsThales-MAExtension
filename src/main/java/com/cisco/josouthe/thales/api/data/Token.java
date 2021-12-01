package com.cisco.josouthe.thales.api.data;

import java.util.List;

public class Token {
    public String id, account, client_id, userId, username;
    public long expiresIn, revokeNotRefreshedIn;
    public boolean expired, revoked;
    public String refreshedAt, createdAt, updatedAt;
    public List<String> labels;
}
