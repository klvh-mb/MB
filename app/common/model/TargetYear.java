package common.model;

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

    private TargetYear(int birthYear) {
        this.birthYear = birthYear;
        this.zodiac = getZodiac(birthYear);
    }

    public int getBirthYear() {
        return this.birthYear;
    }

    public Zodiac getZodiac() { 
        return this.zodiac;
    }
    
    public static Zodiac getZodiac(int year) {
        int minZodiacYear = MIN_YEAR;
        if (year < minZodiacYear)
            throw new RuntimeException(year + " is too small. Please input year after " + minZodiacYear);
        
        int index = (year - minZodiacYear) % 12;
        return Zodiac.values()[index];
    }
    
    public static TargetYear valueOf(int year) {
        return new TargetYear(year);
    }
}
