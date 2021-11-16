package com.cisco.josouthe.thales.data;

import com.cisco.josouthe.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthToken {
    private static final Logger logger = LogManager.getFormatterLogger();

    public String jwt;
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
