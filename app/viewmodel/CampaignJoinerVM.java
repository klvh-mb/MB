package viewmodel;

import java.util.Date;

import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CampaignJoinerVM {
	@JsonProperty("userId") public long userId;
	@JsonProperty("campaignId") public long campaignId;
	@JsonProperty("name") public String name;
	@JsonProperty("joinedDate") public Date joinedDate;
	
	public CampaignJoinerVM(Long userId, Long campaignId, Date joinedDate) {
        this.userId = userId;
        this.campaignId = campaignId;
        this.joinedDate = joinedDate;
        this.name = User.findById(userId).name;
    }
}
