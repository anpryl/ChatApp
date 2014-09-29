package com.aprylutskyi.chat.client.util;

import com.aprylutskyi.chat.client.constants.DataConstants;

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
