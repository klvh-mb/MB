package viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.*;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
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
	@JsonProperty("isC") public boolean isCommentable = false;
	@JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isWtAns") public boolean isWantAnswer = false;
	@JsonProperty("isBookmarked") public boolean isBookmarked = false;


    private static final int COMMENT_PREVIEW_COUNT = 3;

	public static CommunityPostVM communityPostVM(Post post, User user) {
        final boolean isCommentable = true;     // open for comment
        return communityPostVM(post, user, isCommentable);
    }

    public static CommunityPostVM communityPostVM(Post post, User user, boolean isCommentable) {
		CommunityPostVM postVM = new CommunityPostVM();
		postVM.postId = post.id;
		postVM.ownerId = post.owner.id;
		postVM.postedBy = post.owner.displayName;
		postVM.postedOn = post.getCreatedDate().getTime();
		postVM.updatedOn = post.getSocialUpdatedDate().getTime();
		postVM.postedTitle = post.title;
		postVM.postedText = post.body;
		postVM.noOfComments = post.noOfComments;
		postVM.postType = post.postType.name();
        postVM.communityType = post.community.communityType.name();
		postVM.communityName = post.community.name;
		postVM.communityIcon = post.community.icon;
		postVM.communityId = post.community.id;
		//need to write logic for showing no of views
		postVM.noOfViews = post.noOfViews;
		postVM.noOfLikes = post.noOfLikes;
        postVM.noOfWantAnswers = post.noWantAns;
		postVM.expanded = false;
		postVM.isBookmarked = User.isLoggedIn(user) ? post.isBookmarkedBy(user):false;
		postVM.isCommentable = isCommentable;
		postVM.isOwner = post.owner.id == user.id;

		if(post.folder != null && !CollectionUtils.isEmpty(post.folder.resources)) {
			postVM.hasImage = true;
			postVM.images = new Long[post.folder.resources.size()];
			int i = 0;
			for (Resource rs : post.folder.resources) {
				postVM.images[i++] = rs.id;
			}
		}

        // fetch preview comments
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		List<Comment> comments = post.getCommentsOfPost(COMMENT_PREVIEW_COUNT);

        List<Long> likeCheckIds = new ArrayList<>();
        likeCheckIds.add(post.id);
        for(int i = comments.size() - 1; i >= 0 ; i--) {
            likeCheckIds.add(comments.get(i).getId());
        }
        
        if (User.isLoggedIn(user)){
            Set<PrimarySocialResult> srByUser = PrimarySocialRelationManager.getSocialRelationBy(user, likeCheckIds);
    
    		for(int i = comments.size() - 1; i >= 0 ; i--) {
    			Comment comment = comments.get(i);
    			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment, user);
    			commentVM.isLike = srByUser.contains(new PrimarySocialResult(comment.id, comment.objectType, PrimarySocialRelation.Action.LIKED));
    			commentsToShow.add(commentVM);
    		}

    		postVM.isLike = srByUser.contains(new PrimarySocialResult(post.id, post.objectType, PrimarySocialRelation.Action.LIKED));
            postVM.isWantAnswer = srByUser.contains(new PrimarySocialResult(post.id, post.objectType, PrimarySocialRelation.Action.WANT_ANS));
        } else {
            for(int i = comments.size() - 1; i >= 0 ; i--) {
                Comment comment = comments.get(i);
                CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment, user);
                commentVM.isLike = false;
                commentsToShow.add(commentVM);
            }

            postVM.isLike = false;
            postVM.isWantAnswer = false;
        }
        postVM.comments = commentsToShow;
		
		return postVM;
	}
}
