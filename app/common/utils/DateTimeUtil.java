package common.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;

public class DateTimeUtil {
    
    public static boolean isDateOfBirthValid(String year, String month, String day) {
        try {
            parseDate(year, month, day);
        } catch (IllegalFieldValueException ie) {
            return false;
        }
        return true;
    }
    
    public static boolean isDayOfMonthValid(String year, String month, String day) {
        try {
            parseDate(year, month, day);
        } catch (IllegalFieldValueException ie) {
            if ("dayOfMonth".equals(ie.getFieldName()))
                return false;
        }
        return true;
    }
    
    public static DateTime parseDate(String year, String month, String day) {
        if (year.startsWith("<")) {     // e.g. <1960
            year = year.replaceAll("<", "").trim();
        }
        
        if (StringUtils.isEmpty(day)) {
            day = "1";
        }
        
        int y = Integer.valueOf(year);
        int m = Integer.valueOf(month);
        int d = Integer.valueOf(day);
        
        if (d <= 0) {       // day is optional most of the times
            d = 1;
        }
        
        return new DateTime(y, m ,d, 12, 0);
    }
    
}
