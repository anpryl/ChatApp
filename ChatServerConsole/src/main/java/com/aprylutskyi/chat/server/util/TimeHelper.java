package com.aprylutskyi.chat.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aprylutskyi.chat.server.constants.DataConstants;

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
