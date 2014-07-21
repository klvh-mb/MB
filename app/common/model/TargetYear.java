package common.model;

import org.joda.time.DateTime;

/**
 * 
 */
public class TargetYear {
    
    public int birthYear;
    public Zodiac zodiac;
    
    public static final int MIN_YEAR = 1984;
    
    public static enum Zodiac {
        RAT,    // 0 ordinal
        OX,
        TIGER,
        RABBIT,
        DRAGON,
        SNAKE,
        HORSE,
        GOAT,
        MONKEY,
        ROOSTER,
        DOG,
        PIG
    }

    private TargetYear(DateTime birthday) {
        this.birthYear = birthday.getYear();
        this.zodiac = getZodiac(birthday);
    }

    public int getBirthYear() {
        return this.birthYear;
    }

    public Zodiac getZodiac() { 
        return this.zodiac;
    }
    
    /**
     * refer to here for exact zodiac 
     * http://en.wikipedia.org/wiki/Chinese_zodiac#Years
     */
    public static Zodiac getZodiac(DateTime birthday) {
        int minZodiacYear = MIN_YEAR;
        if (birthday.getYear() < minZodiacYear)
            throw new RuntimeException(birthday.getYear() + " is too old. Baby birthday should be after " + minZodiacYear);
        
        if (birthday.isAfter(new DateTime(1984,2,2,0,0)) && birthday.isBefore(new DateTime(1985,2,20,0,0))) {
            return Zodiac.RAT;
        } else if (birthday.isAfter(new DateTime(1985,2,20,0,0)) && birthday.isBefore(new DateTime(1986,2,9,0,0))) {
            return Zodiac.OX;
        } else if (birthday.isAfter(new DateTime(1986,2,9,0,0)) && birthday.isBefore(new DateTime(1987,1,29,0,0))) {
            return Zodiac.TIGER;
        } else if (birthday.isAfter(new DateTime(1987,1,29,0,0)) && birthday.isBefore(new DateTime(1988,2,17,0,0))) {
            return Zodiac.RABBIT;
        } else if (birthday.isAfter(new DateTime(1988,2,17,0,0)) && birthday.isBefore(new DateTime(1989,2,6,0,0))) {
            return Zodiac.DRAGON;
        } else if (birthday.isAfter(new DateTime(1989,2,6,0,0)) && birthday.isBefore(new DateTime(1990,1,27,0,0))) {
            return Zodiac.SNAKE;
        } else if (birthday.isAfter(new DateTime(1990,1,27,0,0)) && birthday.isBefore(new DateTime(1991,2,15,0,0))) {
            return Zodiac.HORSE;
        } else if (birthday.isAfter(new DateTime(1991,2,15,0,0)) && birthday.isBefore(new DateTime(1992,2,4,0,0))) {
            return Zodiac.GOAT;
        } else if (birthday.isAfter(new DateTime(1992,2,4,0,0)) && birthday.isBefore(new DateTime(1993,1,23,0,0))) {
            return Zodiac.MONKEY;
        } else if (birthday.isAfter(new DateTime(1993,1,23,0,0)) && birthday.isBefore(new DateTime(1994,2,10,0,0))) {
            return Zodiac.ROOSTER;
        } else if (birthday.isAfter(new DateTime(1994,2,10,0,0)) && birthday.isBefore(new DateTime(1995,1,31,0,0))) {
            return Zodiac.DOG;
        } else if (birthday.isAfter(new DateTime(1995,1,31,0,0)) && birthday.isBefore(new DateTime(1996,2,19,0,0))) {
            return Zodiac.PIG;
        } else if (birthday.isAfter(new DateTime(1996,2,19,0,0)) && birthday.isBefore(new DateTime(1997,2,7,0,0))) {
            return Zodiac.RAT;
        } else if (birthday.isAfter(new DateTime(1997,2,7,0,0)) && birthday.isBefore(new DateTime(1998,1,28,0,0))) {
            return Zodiac.OX;
        } else if (birthday.isAfter(new DateTime(1998,1,28,0,0)) && birthday.isBefore(new DateTime(1999,2,16,0,0))) {
            return Zodiac.TIGER;
        } else if (birthday.isAfter(new DateTime(1999,2,16,0,0)) && birthday.isBefore(new DateTime(2000,2,5,0,0))) {
            return Zodiac.RABBIT;
        } else if (birthday.isAfter(new DateTime(2000,2,5,0,0)) && birthday.isBefore(new DateTime(2001,1,24,0,0))) {
            return Zodiac.DRAGON;
        } else if (birthday.isAfter(new DateTime(2001,1,24,0,0)) && birthday.isBefore(new DateTime(2002,2,12,0,0))) {
            return Zodiac.SNAKE;
        } else if (birthday.isAfter(new DateTime(2002,2,12,0,0)) && birthday.isBefore(new DateTime(2003,2,1,0,0))) {
            return Zodiac.HORSE;
        } else if (birthday.isAfter(new DateTime(2003,2,1,0,0)) && birthday.isBefore(new DateTime(2004,1,22,0,0))) {
            return Zodiac.GOAT;
        } else if (birthday.isAfter(new DateTime(2004,1,22,0,0)) && birthday.isBefore(new DateTime(2005,2,9,0,0))) {
            return Zodiac.MONKEY;
        } else if (birthday.isAfter(new DateTime(2005,2,9,0,0)) && birthday.isBefore(new DateTime(2006,1,29,0,0))) {
            return Zodiac.ROOSTER;
        } else if (birthday.isAfter(new DateTime(2006,1,29,0,0)) && birthday.isBefore(new DateTime(2007,2,18,0,0))) {
            return Zodiac.DOG;
        } else if (birthday.isAfter(new DateTime(2007,2,18,0,0)) && birthday.isBefore(new DateTime(2008,2,7,0,0))) {
            return Zodiac.PIG;
        } else if (birthday.isAfter(new DateTime(2008,2,7,0,0)) && birthday.isBefore(new DateTime(2009,1,26,0,0))) {
            return Zodiac.RAT;
        } else if (birthday.isAfter(new DateTime(2009,1,26,0,0)) && birthday.isBefore(new DateTime(2010,2,14,0,0))) {
            return Zodiac.OX;
        } else if (birthday.isAfter(new DateTime(2010,2,14,0,0)) && birthday.isBefore(new DateTime(2011,2,3,0,0))) {
            return Zodiac.TIGER;
        } else if (birthday.isAfter(new DateTime(2011,2,3,0,0)) && birthday.isBefore(new DateTime(2012,1,23,0,0))) {
            return Zodiac.RABBIT;
        } else if (birthday.isAfter(new DateTime(2012,1,23,0,0)) && birthday.isBefore(new DateTime(2013,2,10,0,0))) {
            return Zodiac.DRAGON;
        } else if (birthday.isAfter(new DateTime(2013,2,10,0,0)) && birthday.isBefore(new DateTime(2014,1,31,0,0))) {
            return Zodiac.SNAKE;
        } else if (birthday.isAfter(new DateTime(2014,1,31,0,0)) && birthday.isBefore(new DateTime(2015,2,19,0,0))) {
            return Zodiac.HORSE;
        } else if (birthday.isAfter(new DateTime(2015,2,19,0,0)) && birthday.isBefore(new DateTime(2016,2,8,0,0))) {
            return Zodiac.GOAT;
        } else if (birthday.isAfter(new DateTime(2016,2,8,0,0)) && birthday.isBefore(new DateTime(2017,1,28,0,0))) {
            return Zodiac.MONKEY;
        } else if (birthday.isAfter(new DateTime(2017,1,28,0,0)) && birthday.isBefore(new DateTime(2018,2,16,0,0))) {
            return Zodiac.ROOSTER;
        } else if (birthday.isAfter(new DateTime(2018,2,16,0,0)) && birthday.isBefore(new DateTime(2019,2,5,0,0))) {
            return Zodiac.DOG;
        } else if (birthday.isAfter(new DateTime(2019,2,5,0,0)) && birthday.isBefore(new DateTime(2020,1,25,0,0))) {
            return Zodiac.PIG;
        } else if (birthday.isAfter(new DateTime(2020,1,25,0,0)) && birthday.isBefore(new DateTime(2021,2,12,0,0))) {
            return Zodiac.RAT;
        } else if (birthday.isAfter(new DateTime(2021,2,12,0,0)) && birthday.isBefore(new DateTime(2022,2,1,0,0))) {
            return Zodiac.OX;
        } else if (birthday.isAfter(new DateTime(2022,2,1,0,0)) && birthday.isBefore(new DateTime(2023,1,22,0,0))) {
            return Zodiac.TIGER;
        } else if (birthday.isAfter(new DateTime(2023,1,22,0,0)) && birthday.isBefore(new DateTime(2024,2,10,0,0))) {
            return Zodiac.RABBIT;
        } else if (birthday.isAfter(new DateTime(2024,2,10,0,0)) && birthday.isBefore(new DateTime(2025,1,29,0,0))) {
            return Zodiac.DRAGON;
        } else if (birthday.isAfter(new DateTime(2025,1,29,0,0)) && birthday.isBefore(new DateTime(2026,2,17,0,0))) {
            return Zodiac.SNAKE;
        } else if (birthday.isAfter(new DateTime(2026,2,17,0,0)) && birthday.isBefore(new DateTime(2027,2,6,0,0))) {
            return Zodiac.HORSE;
        } else if (birthday.isAfter(new DateTime(2027,2,6,0,0)) && birthday.isBefore(new DateTime(2028,1,26,0,0))) {
            return Zodiac.GOAT;
        } else if (birthday.isAfter(new DateTime(2028,1,26,0,0)) && birthday.isBefore(new DateTime(2029,2,13,0,0))) {
            return Zodiac.MONKEY;
        } else if (birthday.isAfter(new DateTime(2029,2,13,0,0)) && birthday.isBefore(new DateTime(2030,2,3,0,0))) {
            return Zodiac.ROOSTER;
        } 
        
        // else
        int index = (birthday.getYear() - minZodiacYear) % 12;
        return Zodiac.values()[index];
    }
    
    public static TargetYear valueOf(DateTime birthday) {
        return new TargetYear(birthday);
    }
    
    public String getZodiacInfo() {
        return this.zodiac.name();
    }
    
    @Override
    public String toString() {
        return "[birthYear=" + this.birthYear + "|zodiac=" + this.zodiac.name() + "]";
    }
}
