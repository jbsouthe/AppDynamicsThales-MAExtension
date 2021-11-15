package com.cisco.josouthe.thales.data;

import com.cisco.josouthe.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthToken {
    private static final Logger logger = LogManager.getFormatterLogger();

    public String jwt, username, createdAt;
    public long expiresIn;

    public boolean isExpired() {
        if( expiresIn == 0 ) {
            return false;
        } else {
            //createdAt =~ "2021-11-12T17:42:46.33215Z"
            try {
                long now = new Date().getTime();
                long expiresAtTime = Utility.getDateFromString(createdAt)+expiresIn;
                if( now >= expiresAtTime ) return true;
            } catch (ParseException e) {
                logger.warn("Parse Exception in date string '%s'",createdAt);
                return true;
            }
        }
        return true;
    }
}
