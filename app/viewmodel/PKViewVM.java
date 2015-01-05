package viewmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.collection.Pair;
import models.Community;
import models.Comment;
import models.Post;
import models.PKViewMeta;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.DefaultValues;

/**
 * VM Class for PKView
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
	@JsonProperty("isRed") public boolean isRed = false;
	@JsonProperty("blue_ds") public String blueDescription;
	@JsonProperty("nobv") public long noOfBlueVotes;
	@JsonProperty("nobc") public long noOfBlueComments;
	@JsonProperty("blue_cs") public List<CommunityPostCommentVM> blueComments;
	@JsonProperty("isBlue") public boolean isBlue = false;
	
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isBookmarked") public boolean isBookmarked = false;
    
    @JsonProperty("ctyp") public String communityType;
    @JsonProperty("cn") public String communityName;
    @JsonProperty("ci") public String communityIcon;
    @JsonProperty("cid") public Long communityId;

    public PKViewVM(PKViewMeta pkViewMeta, Post post, User user) {
        this(pkViewMeta, post, user, false);
    }

	public PKViewVM(PKViewMeta pkViewMeta, Post post, User user, boolean preview) {
	    this.id = pkViewMeta.id;
	    this.createdDate = post.getCreatedDate();
        this.name = post.title;
	    if (preview && post.getBody().length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
	        this.description = post.getBody().substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
	    } else {
	        this.description = post.getBody();
	    }
		this.image = pkViewMeta.getImage();

        // fetch comments
        Pair<List<CommunityPostCommentVM>,List<CommunityPostCommentVM>>
                yesNoComments = extractYesNoCommentVMs(post, user);

		this.redDescription = pkViewMeta.getYesText();
		this.noOfRedVotes = pkViewMeta.getYesVoteCount();
		this.noOfRedComments = yesNoComments.first.size();
		this.redComments = yesNoComments.first;
		this.blueDescription = pkViewMeta.getNoText();
		this.noOfBlueVotes = pkViewMeta.getNoVoteCount();
		this.noOfBlueComments = yesNoComments.second.size();
		this.blueComments = yesNoComments.second;

        this.noOfLikes = post.noOfLikes;
		this.noOfViews = post.noOfViews;

        Community postComm = post.getCommunity();
		this.communityType = postComm.communityType.name();
		this.communityName = postComm.name;
		this.communityIcon = postComm.icon;
		this.communityId = postComm.id;

        try {
            this.isRed = true;
            this.isBlue = false;
            this.isLike = post.isLikedBy(user);
            this.isBookmarked = post.isBookmarkedBy(user);
        } catch (SocialObjectNotLikableException e) {
            this.isLike = false;
        }
	}

    private static Pair<List<CommunityPostCommentVM>,List<CommunityPostCommentVM>>
            extractYesNoCommentVMs(Post p, User user) {
        List<Comment> yesComments = p.getCommentsOfPostByAttribute(PKViewMeta.COMMENT_ATTR_YES);
        List<Comment> noComments = p.getCommentsOfPostByAttribute(PKViewMeta.COMMENT_ATTR_NO);

        int yesCommentCount = yesComments.size();
        int noCommentCount = noComments.size();
        List<CommunityPostCommentVM> yesCommentVMs = new ArrayList<>(yesCommentCount);
        List<CommunityPostCommentVM> noCommentVMs = new ArrayList<>(noCommentCount);

        for(int i = yesCommentCount- 1; i >= 0 ; i--) {
            Comment yesComment = yesComments.get(i);
            CommunityPostCommentVM vm = CommunityPostCommentVM.communityPostCommentVM(yesComment, user, yesCommentCount-i);
            yesCommentVMs.add(vm);
        }
        for(int i = noCommentCount- 1; i >= 0 ; i--) {
            Comment noComment = noComments.get(i);
            CommunityPostCommentVM vm = CommunityPostCommentVM.communityPostCommentVM(noComment, user, noCommentCount-i);
            noCommentVMs.add(vm);
        }
        return new Pair<>(yesCommentVMs, noCommentVMs);
    }
}
