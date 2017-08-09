package com.orangemuffin.tvnext.utils;

/* Created by OrangeMuffin on 8/1/2017 */
public class StringFormatUtil {

    public static String getDueDate(int duedate) {
        if (duedate == 0) {
            return "Today";
        } else if (duedate > 0) {
            if (duedate == 1) {
                return "Tomorrow";
            } else {
                return "In " + duedate + " days";
            }
        } else {
            if (duedate == -1) {
                return "Yesterday";
            } else {
                return Math.abs(duedate) + " days ago";
            }
        }
    }

    public static String prefixNumber(String number) {
        if (Integer.valueOf(number) < 10) {
            return "0" + number;
        } else {
            return number;
        }
    }

    public static String numDisplay(String seasonNum, String episodeNum) {
        return "S" + prefixNumber(seasonNum) + "E" + prefixNumber(episodeNum);
    }
}
