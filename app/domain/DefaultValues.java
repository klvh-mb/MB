package domain;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class DefaultValues {

    public static int DEFAULT_UTILITY_COUNT = 5;
    public static int DEFAULT_INFINITE_SCROLL_COUNT = 10;
    public static int FEATURED_ARTICLES_COUNT = 6;
    public static int ARTICLES_UTILITY_COUNT = 3;
    public static int ARTICLES_RELATED_COUNT = 10;
    public static int FRIENDS_UTILITY_COUNT = 8;
    
    public static Map<String, String> PARENT_BIRTH_YEARS = new LinkedHashMap<String, String>();
    public static Map<String, String> CHILD_BIRTH_YEARS = new LinkedHashMap<String, String>();
    
    public static DateTime NOW = new DateTime();
    
    public static int PARENT_YEAR_MIN_AGE = 16;
    public static int PARENT_YEAR_MAX_AGE = 50;
    public static int CHILD_YEAR_MIN_AGE = -1;
    public static int CHILD_YEAR_MAX_AGE = 14;
    
    static {
        init();
    }
    
    private static void init() {
        int year = NOW.getYear();
        
        // parent age range
        for (int i = PARENT_YEAR_MIN_AGE; i <= PARENT_YEAR_MAX_AGE; i++) {
            PARENT_BIRTH_YEARS.put(String.valueOf(year - i), String.valueOf(year - i));
        }
        PARENT_BIRTH_YEARS.put(String.valueOf(year - PARENT_YEAR_MAX_AGE) + "之前", "<" + String.valueOf(year - PARENT_YEAR_MAX_AGE));
        
        // child age range
        for (int i = CHILD_YEAR_MIN_AGE; i <= CHILD_YEAR_MAX_AGE; i++) {
            CHILD_BIRTH_YEARS.put(String.valueOf(year - i), String.valueOf(year - i));
        }
        CHILD_BIRTH_YEARS.put(String.valueOf(year - CHILD_YEAR_MAX_AGE) + "之前", "<" + String.valueOf(year - CHILD_YEAR_MAX_AGE));
    }
}
