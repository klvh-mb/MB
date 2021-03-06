package viewmodel;

import java.util.Date;

import models.GameAccount;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CampaignJoinerVM {
	@JsonProperty("userId") public long userId;
	@JsonProperty("campaignId") public long campaignId;
	@JsonProperty("displayName") public String displayName;
	@JsonProperty("name") public String name;
	@JsonProperty("email") public String email;
	@JsonProperty("joinedDate") public Date joinedDate;
	@JsonProperty("fbLink") public String fbLink;
	
	public CampaignJoinerVM(Long userId, Long campaignId, Date joinedDate) {
        this.userId = userId;
        this.campaignId = campaignId;
        this.joinedDate = joinedDate;
        User user = User.findById(userId);
        this.displayName = user.displayName;
        this.name = user.firstName + " " + user.lastName;
        GameAccount gameAccount = GameAccount.findByUserId(userId);
        if (gameAccount != null) {
        	this.email = gameAccount.email;        	
        }
        if (user.fbUserInfo != null) {
            this.fbLink = user.fbUserInfo.link;
        }
    }
}
