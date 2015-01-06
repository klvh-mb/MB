package viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Comment;
import models.Post;
import models.PrimarySocialRelation;
import models.Resource;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;
import processor.PrimarySocialRelationManager;
import static processor.PrimarySocialRelationManager.PrimarySocialResult;

public class CommunityPostVM {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public long postedOn;
	@JsonProperty("ut") public long updatedOn;
	@JsonProperty("ptl") public String postedTitle;
	@JsonProperty("pt") public String postedText;
	@JsonProperty("hasImage") public boolean hasImage;
	@JsonProperty("n_c") public int noOfComments;
	@JsonProperty("cs") public List<CommunityPostCommentVM> comments;
	@JsonProperty("imgs") public Long[] images;
	@JsonProperty("type") public String postType;
    @JsonProperty("ctyp") public String communityType;
	@JsonProperty("cn") public String communityName;
	@JsonProperty("ci") public String communityIcon;
	@JsonProperty("cid") public Long communityId;
	@JsonProperty("nov") public int noOfViews;
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nowa") public int noOfWantAnswers;
	@JsonProperty("ep") public boolean expanded;
	
	@JsonProperty("isO") public boolean isOwner = false;
	@JsonProperty("showM") public boolean showMore = false;
	@JsonProperty("isC") public boolean isCommentable = false;
	@JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isWtAns") public boolean isWantAnswer = false;
	@JsonProperty("isBookmarked") public boolean isBookmarked = false;

	public CommunityPostVM(Post post, User user) {
        this(post, user, true);
    }

    public CommunityPostVM(Post post, User user, boolean isCommentable) {
        this.postId = post.id;
		this.ownerId = post.owner.id;
		this.postedBy = post.owner.displayName;
		this.postedOn = post.getCreatedDate().getTime();
		this.updatedOn = post.getSocialUpdatedDate().getTime();
		this.postedTitle = post.title;
		if(post.shortBodyCount > 0){
		    this.showMore= true; 
		    this.postedText = post.body.substring(0,post.shortBodyCount);
		} else {
		    this.postedText = post.body;
		}
		this.noOfComments = post.noOfComments;
		this.postType = post.postType.name();
		this.communityType = post.community.communityType.name();
		this.communityName = post.community.name;
		this.communityIcon = post.community.icon;
		this.communityId = post.community.id;
		//need to write logic for showing no of views
		this.noOfViews = post.noOfViews;
		this.noOfLikes = post.noOfLikes;
		this.noOfWantAnswers = post.noWantAns;
		this.expanded = false;
		this.isBookmarked = User.isLoggedIn(user) ? post.isBookmarkedBy(user):false;
		this.isCommentable = isCommentable;
		this.isOwner = post.owner.id == user.id;

		if(post.folder != null && !CollectionUtils.isEmpty(post.folder.resources)) {
		    this.hasImage = true;
		    this.images = new Long[post.folder.resources.size()];
			int i = 0;
			for (Resource rs : post.folder.resources) {
			    this.images[i++] = rs.id;
			}
		}

        // fetch preview comments
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		List<Comment> comments = post.getCommentsOfPost(DefaultValues.COMMENTS_PREVIEW_COUNT);

        List<Long> likeCheckIds = new ArrayList<>();
        likeCheckIds.add(post.id);
        for(int i = comments.size() - 1; i >= 0 ; i--) {
            likeCheckIds.add(comments.get(i).getId());
        }
        
        if (User.isLoggedIn(user)){
            Set<PrimarySocialResult> srByUser = PrimarySocialRelationManager.getSocialRelationBy(user, likeCheckIds);

    		for(int i = comments.size() - 1; i >= 0 ; i--) {
    			Comment comment = comments.get(i);
    			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment, user, post.noOfComments - i);
    			commentVM.isLike = srByUser.contains(new PrimarySocialResult(comment.id, comment.objectType, PrimarySocialRelation.Action.LIKED));
    			commentsToShow.add(commentVM);
    		}

    		this.isLike = srByUser.contains(new PrimarySocialResult(post.id, post.objectType, PrimarySocialRelation.Action.LIKED));
    		this.isWantAnswer = srByUser.contains(new PrimarySocialResult(post.id, post.objectType, PrimarySocialRelation.Action.WANT_ANS));
        } else {
            for(int i = comments.size() - 1; i >= 0 ; i--) {
                Comment comment = comments.get(i);
                CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment, user, post.noOfComments - i);
                commentVM.isLike = false;
                commentsToShow.add(commentVM);
            }

            this.isLike = false;
            this.isWantAnswer = false;
        }
        this.comments = commentsToShow;
	}
}
