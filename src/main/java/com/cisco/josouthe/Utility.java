package com.cisco.josouthe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static long dayInMilliseconds = 1000*60*60*24;

    public static long getDateFromString( String date ) throws ParseException {
        return simpleDateFormat.parse(date).getTime();
    }

    public static long now() { return new Date().getTime(); }

    public static long gateDaysUntil(String valid_until) {
        try {
            long expirationTime = simpleDateFormat.parse(valid_until).getTime()-now();
            return expirationTime/dayInMilliseconds;
        } catch (ParseException e) {
            logger.warn("Parse Exception on date '%s' message: %s",valid_until, e.getMessage());
            return -1;
        }
    }
}
