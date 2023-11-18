package com.bank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BankUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parseDate(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }
}