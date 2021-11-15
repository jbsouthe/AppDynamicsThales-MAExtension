package com.cisco.josouthe.thales.data;

public class Token {
    public String id, account, client_id, labels, userId, username;
    public long expiresIn, revokeNotRefreshedIn;
    public boolean expired, revoked;
    public String refreshedAt, createdAt, updatedAt;
}
