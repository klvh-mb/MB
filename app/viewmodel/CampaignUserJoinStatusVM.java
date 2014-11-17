package viewmodel;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class CampaignUserJoinStatusVM {
	@JsonProperty("campaignId") public long campaignId;
	@JsonProperty("userId") public long userId;
	@JsonProperty("success") public boolean success;
	@JsonProperty("messages") public List<String> messages;

	public CampaignUserJoinStatusVM(long campaignId, long userId, boolean success, List<String> messages) {
	    this.campaignId = campaignId;
	    this.userId = userId;
	    this.success = success;
	    this.messages = messages;
	}
}
