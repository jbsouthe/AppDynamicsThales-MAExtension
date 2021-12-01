package com.cisco.josouthe.thales.api.data;

import com.cisco.josouthe.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class AuthToken {
    private static final Logger logger = LogManager.getFormatterLogger();

    public String jwt, token_type, client_id, refresh_token_id, refresh_token;
    public long duration, createdTime;

    public AuthToken() {
        this.createdTime = Utility.now();
    }

    public boolean isExpired() {
        if( duration == 0 ) {
            return false;
        } else {
            long now = new Date().getTime();
            long expiresAtTime = createdTime+(duration*1000);
            if( now >= expiresAtTime ) return true;
            return false;
        }
    }
}
