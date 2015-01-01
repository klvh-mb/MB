package viewmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Community;
import models.Post;
import models.PKViewMeta;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.DefaultValues;

/**
 *
 */
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
    
	public PKViewVM(PKViewMeta pkViewMeta, Post post) {
	    this(pkViewMeta, post, false);
	}
	
	public PKViewVM(PKViewMeta pkViewMeta, Post post, boolean preview) {
	    this.id = pkViewMeta.id;
	    this.createdDate = post.getCreatedDate();
        this.name = post.title;
	    if (preview && post.getBody().length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
	        this.description = post.getBody().substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
	    } else {
	        this.description = post.getBody();
	    }
		this.image = pkViewMeta.getImage();

		this.redDescription = pkViewMeta.getYesText();
		this.noOfRedVotes = pkViewMeta.getYesVoteCount();
		this.noOfRedComments = pkViewMeta.getYesCommentCount();
		this.redComments = new ArrayList<>();       // TODO
		this.blueDescription = pkViewMeta.getNoText();
		this.noOfBlueVotes = pkViewMeta.getNoVoteCount();
		this.noOfBlueComments = pkViewMeta.getNoCommentCount();
		this.blueComments = new ArrayList<>();      // TODO

        this.noOfLikes = post.noOfLikes;
		this.noOfViews = post.noOfViews;

        Community postComm = post.getCommunity();
		this.communityType = postComm.communityType.name();
		this.communityName = postComm.name;
		this.communityIcon = postComm.icon;
		this.communityId = postComm.id;
	}

    public PKViewVM(PKViewMeta pkViewMeta, Post post, User user) {
        this(pkViewMeta, post);

        try {
            this.isLike = post.isLikedBy(user);
            this.isBookmarked = post.isBookmarkedBy(user);
        } catch (SocialObjectNotLikableException e) {
            this.isLike = false;
        }
    }
}
