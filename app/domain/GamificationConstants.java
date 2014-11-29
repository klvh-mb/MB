package domain;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/11/14
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GamificationConstants {

    /**
     * To be in sync with default-values.js
     */
    
    /**
     * Points
     * Conversion Rate = 0.007 (as of Nov 29th, 2014)
     * i.e. $50 gift = 50 / 0.007 = 7143 points (~274 days to get with full activity)
     */
    public static final int POINTS_SIGNUP = 50;
    public static final int POINTS_UPLOAD_PROFILE_PHOTO = 150;
    public static final int POINTS_SIGNIN = 6;
    public static final int POINTS_POST = 2;
    public static final int POINTS_COMMENT = 2;
    public static final int POINTS_LIKE = 0;             // not open yet

    public static final int POINTS_REFERRAL_SIGNUP = 280;

    /**
     * Limit on Points accounting
     */
    public static final long LIMIT_POST = 5;
    public static final long LIMIT_COMMENT = 5;
    public static final long LIMIT_LIKE = 5;
    public static final long LIMIT_REFERRAL_SIGNUP = 20;

}
