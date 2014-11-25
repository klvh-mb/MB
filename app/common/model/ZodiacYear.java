package common.model;

import common.model.TargetYear.Zodiac;

/**
 * update community set targetinginfo='2008_RAT' where id=1;
 * update community set targetinginfo='2009_OX' where id=2;
 * update community set targetinginfo='2010_TIGER' where id=3;
 * update community set targetinginfo='2011_RABBIT' where id=4;
 * update community set targetinginfo='2012_DRAGON' where id=5;
 * update community set targetinginfo='2013_SNAKE' where id=6;
 * update community set targetinginfo='2014_HORSE' where id=7;
 * update community set targetinginfo='2015_GOAT' where id=8;
 * update community set targetinginfo='2016_MONKEY' where id=9;
 * update community set targetinginfo='2017_ROOSTER' where id=10;
 * update community set targetinginfo='2018_DOG' where id=11;
 * update community set targetinginfo='2019_PIG' where id=12;
 */
public class ZodiacYear {
    
    public int year;
    
    public Zodiac zodiac;
    
    public ZodiacYear(int year, Zodiac zodiac) {
        this.year = year;
        this.zodiac = zodiac;
    }
    
    public int getYear() {
        return this.year;
    }
    
    public Zodiac getZodiac() {
        return this.zodiac;
    }
    
    public static ZodiacYear parse(String value) {
        try {
            int year = Integer.valueOf(value.substring(0, 4));
            Zodiac zodiac = Zodiac.valueOf(value.substring(value.indexOf("_")+1));
            return new ZodiacYear(year, zodiac);
        } catch (NumberFormatException e) {
            ;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.year + "_" + this.zodiac.name();
    }
}