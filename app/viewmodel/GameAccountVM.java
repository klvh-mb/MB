package viewmodel;

import models.GameAccount;
import models.GameAccountStatistics;
import models.GameLevel;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 20/11/14
 * Time: 12:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameAccountVM {
    @JsonProperty("uid")    public Long userId;
    @JsonProperty("pmcde")  public String promoCode;
    @JsonProperty("gmpt")   public Long gamePoints;
    @JsonProperty("acpt")   public Long activityPoints;
    @JsonProperty("rdpt")   public Long redeemedPoints;
    @JsonProperty("refs")   public Long noReferralSignups;
    @JsonProperty("signedIn") public Boolean isSignedInToday;
    @JsonProperty("profilePic") public Boolean hasUploadProfilePic;
    @JsonProperty("appLogin") public Boolean appLogin;
    @JsonProperty("gl") public Long gameLevel;
    @JsonProperty("gln") public String gameLevelName;
    @JsonProperty("gli") public String gameLevelIcon;
    @JsonProperty("gnlpt") public Long gameNextLevelPoints;
    
    public GameAccountVM(GameAccount account, GameAccountStatistics stat) {
        this.userId = account.user_id;
        this.promoCode = account.promoCode;
        this.gamePoints = account.getGamePoints();
        this.activityPoints = account.getActivityPoints();
        this.redeemedPoints = account.redeemed_points;
        this.noReferralSignups = account.number_of_referral_signups;
        this.isSignedInToday = stat.num_sign_in > 0;
        this.hasUploadProfilePic = account.has_upload_profile_pic;
        this.appLogin = account.app_login;
        GameLevel gameLevel = GameLevel.getGameLevel(account.getActivityPoints());
        this.gameLevel = gameLevel.level;
        this.gameLevelName = gameLevel.name;
        this.gameLevelIcon = gameLevel.icon.url;
        this.gameNextLevelPoints = gameLevel.toPoints;
    }
}
