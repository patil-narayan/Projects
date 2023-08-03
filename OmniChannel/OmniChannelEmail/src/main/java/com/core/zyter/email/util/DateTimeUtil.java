/*
 * @DateTimeUtil.java@
 * Created on 14Feb2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.email.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    private static final String SEARCH_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";


    public static Date calculateDate(long serverOffset, long clientOffset, Date dateValue) {
        long finalDate = dateValue.getTime() + clientOffset - serverOffset;
        Date serverDate = new Date();
        serverDate.setTime(finalDate);
        return serverDate;
    }

    public static Long getServerOffset(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SEARCH_DATE_FORMAT);
        String dateString = dateFormat.format(date);
        long serverOffset = TimeZone.getTimeZone(TimeZone.getDefault().getID()).getRawOffset();
        if (TimeZone.getDefault().inDaylightTime(dateFormat.parse(dateString))) {
            serverOffset = serverOffset + TimeZone.getDefault().getDSTSavings();
        }
        return serverOffset;

    }
}
