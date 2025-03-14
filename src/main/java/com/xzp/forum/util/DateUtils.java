package com.xzp.forum.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static String getParseDate(Date date) {
        if (date == null) {
            return "";
        }
        return formatter.format(date);
    }
}
