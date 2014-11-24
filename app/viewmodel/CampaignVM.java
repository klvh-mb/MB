package viewmodel;

import java.util.Date;

import models.Campaign;
import models.CampaignActionsUser;
import models.User;
import models.Campaign.CampaignState;
import models.Campaign.CampaignType;

import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

import common.utils.DateTimeUtil;
import controllers.CampaignController;
import domain.DefaultValues;

public class CampaignVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("ds") public String description;
	@JsonProperty("ct") public String campaignType;
	@JsonProperty("cs") public String campaignState;
	@JsonProperty("sd") public String startDate;
	@JsonProperty("ed") public String endDate;
	@JsonProperty("at") public String announcementType;
	@JsonProperty("an") public String announcement;
	
	@JsonProperty("ac") public boolean isActive = false;
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("isJoined") public boolean isJoined = false;
    @JsonProperty("isLike") public boolean isLike = false;

    @JsonProperty("uc") public Long joinedUsersCount = -1L;
    
	public CampaignVM(Campaign campaign) {
	    this(campaign, false);
	}
	
	public CampaignVM(Campaign campaign, boolean preview) {
	    this.id = campaign.id;
	    this.name = campaign.name;
	    this.image = campaign.image;
	    if (preview && campaign.description.length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
	        this.description = campaign.description.substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
	    } else {
	        this.description = campaign.description;
	    }
		this.campaignType = campaign.campaignType.name();
		this.campaignState = campaign.campaignState.name();
		if ((campaign.campaignState == CampaignState.NEW || 
		        campaign.campaignState == CampaignState.PUBLISHED || 
		        campaign.campaignState == CampaignState.STARTED) && 
		        campaign.endDate.after(new Date())) {
		    this.isActive = true;
		}
		this.startDate = DateTimeUtil.toString(campaign.startDate);
		this.endDate = DateTimeUtil.toString(campaign.endDate);
		this.announcementType = campaign.announcementType.name();
		this.announcement = campaign.announcement;
		
		this.noOfLikes = campaign.noOfLikes;
		this.noOfViews = campaign.noOfViews;
	}

    public CampaignVM(Campaign campaign, User user) {
        this(campaign);
        
        if (CampaignType.ACTIONS == campaign.campaignType) {
            this.isJoined = CampaignActionsUser.isJoinedCampaign(user.id, campaign.id);
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        } else if (CampaignType.PHOTO_CONTEST == campaign.campaignType) {
            // TODO
        }
        
        try {
            this.isLike = campaign.isLikedBy(user);
        } catch (SocialObjectNotLikableException e) {
            ;
        }
        
        if (user.isLoggedIn() && user.isEditor()) {
            this.joinedUsersCount = CampaignController.getJoinedUsersCount(campaign.id);
        }
    }
}
