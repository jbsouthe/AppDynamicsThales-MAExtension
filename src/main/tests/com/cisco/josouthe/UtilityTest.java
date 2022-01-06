package com.cisco.josouthe;

import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilityTest extends TestCase {

    public void testGetDateFromString() throws ParseException {
        long timestampOne = Utility.getDateFromString("2021-11-08T10:17:03.000000Z");
        long timestampTwo = Utility.getDateFromString("2021-11-08 10:17:03 +0000 GMT");
        System.out.println(timestampOne +" == "+ timestampTwo);
        assert timestampOne == timestampTwo;
    }

    public void testNow() {
        long timestamp = Utility.now();
        assert timestamp > 0;
    }

    public void testGetDaysUntil() {
        SimpleDateFormat simpleDateFormatWithoutT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z z");
        long timestamp = Utility.now() + (4*24*60*60*1000);
        String dateString = simpleDateFormatWithoutT.format( new Date(timestamp));
        long threeDays = Utility.getDaysUntil(dateString);
        System.out.println("3 days = "+ threeDays);
        assert threeDays == 3;
    }

    public void testNumberConversion() {
        assert Utility.isDecimalNumber("1.24") == true;
        assert Utility.isDecimalNumber("100") == false;
        assert Utility.isDecimalNumber("text") == false;
        assert Utility.isDecimalNumber("1.2.3.5") == false;
        assert Utility.decimalToLong("1.24") == 124;
    }
}