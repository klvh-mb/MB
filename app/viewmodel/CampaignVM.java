package viewmodel;

import java.util.Date;

import models.Campaign;
import models.User;
import models.Campaign.CampaignState;

import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

import common.utils.DateTimeUtil;
import domain.DefaultValues;

public class CampaignVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("ds") public String description;
	@JsonProperty("ct") public String campaignType;
	@JsonProperty("cs") public String campaignState;
	@JsonProperty("ac") public boolean isActive = false;
	@JsonProperty("sd") public String startDate;
	@JsonProperty("ed") public String endDate;
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("isLike") public boolean isLike = false;  

	public CampaignVM(Campaign campaign) {
	    this(campaign, false);
	}
	
	public CampaignVM(Campaign campaign, boolean preview) {
	    this.id = campaign.id;
	    this.name = campaign.name;
	    this.image = campaign.image;
	    if (preview && campaign.description.length() > DefaultValues.MAX_PREVIEW_CHARS) {
	        this.description = campaign.description.substring(0, DefaultValues.MAX_PREVIEW_CHARS);
	    } else {
	        this.description = campaign.description;
	    }
		this.campaignType = campaign.campaignType.name();
		this.campaignState = campaign.campaignState.name();
		if ((campaign.campaignState == CampaignState.PUBLISHED || campaign.campaignState == CampaignState.STARTED) && 
		        campaign.endDate.before(new Date())) {
		    this.isActive = true;
		}
		this.startDate = DateTimeUtil.toString(campaign.startDate);
		this.endDate = DateTimeUtil.toString(campaign.endDate);
		
		this.noOfLikes = campaign.noOfLikes;
		this.noOfViews = campaign.noOfViews;
	}

    public CampaignVM(Campaign campaign, User user) {
        this(campaign);
        try {
            this.isLike = campaign.isLikedBy(user);
        } catch (SocialObjectNotLikableException e) {
            ;
        }
    }
}
