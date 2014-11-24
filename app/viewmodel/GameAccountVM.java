package viewmodel;

import models.GameAccount;
import models.GameAccountStatistics;
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
    @JsonProperty("signTd") public Boolean hasSignedInToday;


    public GameAccountVM(GameAccount account, GameAccountStatistics stat) {
        this.userId = account.user_id;
        this.promoCode = account.promoCode;
        this.gamePoints = account.getGamePoints();
        this.activityPoints = account.getActivityPoints();
        this.redeemedPoints = account.redeemed_points;
        this.noReferralSignups = account.number_of_referral_signups;
        this.hasSignedInToday = stat.num_sign_in > 0;
    }
}