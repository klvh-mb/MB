package viewmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Community;
import models.PKView;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.DefaultValues;

public class PKViewVM {
	@JsonProperty("id") public long id;
	@JsonProperty("cd") public Date createdDate;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("im") public String image;
	@JsonProperty("red_ds") public String redDescription;
	@JsonProperty("norv") public long noOfRedVotes;
	@JsonProperty("norc") public long noOfRedComments;
	@JsonProperty("red_cs") public List<CommunityPostCommentVM> redComments;
	@JsonProperty("blue_ds") public String blueDescription;
	@JsonProperty("nobv") public long noOfBlueVotes;
	@JsonProperty("nobc") public long noOfBlueComments;
	@JsonProperty("blue_cs") public List<CommunityPostCommentVM> blueComments;
	
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isBookmarked") public boolean isBookmarked = false;
    
    @JsonProperty("ctyp") public String communityType;
    @JsonProperty("cn") public String communityName;
    @JsonProperty("ci") public String communityIcon;
    @JsonProperty("cid") public Long communityId;
    
	public PKViewVM(PKView pkView) {
	    this(pkView, false);
	}
	
	public PKViewVM(PKView pkView, boolean preview) {
	    // TODO
	    if (pkView == null) {
	        this.id = 1;
	        this.name = "PK View Topic";
	        this.description = "PK View Description";
	        this.createdDate = new Date();
	        this.noOfLikes = 1;
	        this.noOfViews = 1;
	        
	        this.redDescription = "Red Description";
	        this.noOfRedVotes = 30;
	        this.noOfRedComments = 20;
	        this.redComments = new ArrayList<>();
	        this.blueDescription = "Blue Description";
	        this.noOfBlueVotes = 100;
	        this.noOfBlueComments = 35;
	        this.blueComments = new ArrayList<>();
	        
	        Community community = Community.findById(37L);
	        this.communityType = community.communityType.name();
	        this.communityName = community.name;
	        this.communityIcon = community.icon;
	        this.communityId = community.id;
	        return;
	    }
	    
	    this.id = pkView.id;
	    this.name = pkView.name;
	    this.image = pkView.image;
	    if (preview && pkView.description.length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
	        this.description = pkView.description.substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
	    } else {
	        this.description = pkView.description;
	    }
		this.createdDate = pkView.getCreatedDate();
		
		this.noOfLikes = pkView.noOfLikes;
		this.noOfViews = pkView.noOfViews;
		
		// TODO
		this.redDescription = "Red Description";
		this.noOfRedVotes = 30;
		this.noOfRedComments = 20;
		this.redComments = new ArrayList<>();
		this.blueDescription = "Blue Description";
		this.noOfBlueVotes = 100;
		this.noOfBlueComments = 35;
		this.blueComments = new ArrayList<>();
		
		this.communityType = pkView.community.communityType.name();
		this.communityName = pkView.community.name;
		this.communityIcon = pkView.community.icon;
		this.communityId = pkView.community.id;
	}

    public PKViewVM(PKView pkView, User user) {
        this(pkView);
        
        try {
            this.isLike = pkView.isLikedBy(user);
            this.isBookmarked = pkView.isBookmarkedBy(user);
        } catch (SocialObjectNotLikableException e) {
            ;
        }
        
        if (user.isLoggedIn() && user.isEditor()) {
            
        }
    }
}
