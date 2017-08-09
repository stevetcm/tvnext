package com.orangemuffin.tvnext.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/* Created by OrangeMuffin on 8/1/2017 */
public class DateAndTimeUtil {

    public static String convertDate(String pattern, String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date temp = dateFormat.parse(date);
            dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(temp);
        } catch (Exception e) { }
        return null;
    }

    public static String setToLocalTimeZone(String pattern, String date) {
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat(pattern);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsed = sourceFormat.parse(date);

            TimeZone tz = TimeZone.getDefault();
            SimpleDateFormat destFormat = new SimpleDateFormat(pattern);
            destFormat.setTimeZone(tz);

            return destFormat.format(parsed);
        } catch (Exception e) { }
        return null;
    }

    public static String dateToDay(String date) {
        String day = "";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
            int result = calendar.get(Calendar.DAY_OF_WEEK);
            switch (result) {
                case Calendar.MONDAY:
                    day = "Monday";
                    break;
                case Calendar.TUESDAY:
                    day = "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    day = "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    day = "Thursday";
                    break;
                case Calendar.FRIDAY:
                    day = "Friday";
                    break;
                case Calendar.SATURDAY:
                    day = "Saturday";
                    break;
                case Calendar.SUNDAY:
                    day = "Sunday";
                    break;
            }
        } catch (Exception e) { }
        return day;
    }
}
