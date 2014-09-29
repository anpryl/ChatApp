package com.aprylutskyi.chat.server.util;

import com.aprylutskyi.chat.server.constants.DataConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {

    private TimeHelper() {
    }

    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DataConstants.DATE_FORMAT);
        return formatter.format(date);
    }

    public static String getNow() {
        return dateToString(new Date());
    }
}
