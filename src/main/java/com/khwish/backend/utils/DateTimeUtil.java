package com.khwish.backend.utils;

import java.util.Date;

public class DateTimeUtil {

    public static Long getCurrentEpoch() {
        return System.currentTimeMillis() / 1000;
    }

    public static Long getEpochFromDate(Date date) {
        return date.getTime() / 1000;
    }
}