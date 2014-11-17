package domain;

/**
 * Created by IntelliJ IDEA.
 * Date: 9/11/14
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GamificationConstants {

    /**
     * Points
     */
    public static final int POINTS_SIGNUP = 20;
    public static final int POINTS_REFERRAL_SIGNUP = 20;
    public static final int POINTS_UPLOAD_PROFILE_PHOTO = 10;
    public static final int POINTS_POST = 5;
    public static final int POINTS_POST_DELETE = 1;
    public static final int POINTS_COMMENT = 5;
    public static final int POINTS_LIKE = 5;

    /**
     * Limit on Points accounting
     */
    public static final long LIMIT_POST = 5;
    public static final long LIMIT_COMMENT = 5;
    public static final long LIMIT_LIKE = 5;
    public static final long LIMIT_REFERRAL_SIGNUP = 20;

}
