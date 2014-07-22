package viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import common.collection.Pair;
import domain.SocialObjectType;
import models.Comment;
import models.Post;
import models.Resource;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import processor.LikeManager;

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
	@JsonProperty("cn") public String communityName;
	@JsonProperty("ci") public String communityIcon;
	@JsonProperty("cid") public Long communityId;
	@JsonProperty("nov") public int noOfViews;
	@JsonProperty("nol") public int noOfLikes;
	@JsonProperty("ep") public boolean expanded;
	
	@JsonProperty("isC") public boolean isCommentable = false;
	@JsonProperty("isLike") public boolean isLike = false;
	@JsonProperty("isBookmarked") public boolean isBookmarked = false;


    private static final int COMMENT_PREVIEW_COUNT = 3;

	public static CommunityPostVM communityPostVM(Post post, User user) {
        return communityPostVM(post, user, user.isMemberOf(post.community.id));
    }

    public static CommunityPostVM communityPostVM(Post post, User user, boolean isCommMember) {
		CommunityPostVM postVM = new CommunityPostVM();
		postVM.postId = post.id;
		postVM.ownerId = post.owner.id;
		postVM.postedBy = post.owner.name;
		postVM.postedOn = post.getCreatedDate().getTime();
		postVM.updatedOn = post.getSocialUpdatedDate().getTime();
		postVM.postedTitle = post.title;
		postVM.postedText = post.body;
		postVM.noOfComments = post.noOfComments;
		postVM.postType = post.postType.name();
		postVM.communityName = post.community.name;
		postVM.communityIcon = post.community.icon;
		postVM.communityId = post.community.id;
		//need to write logic for showing no of views
		postVM.noOfViews = 0;
		postVM.noOfLikes = post.noOfLikes;
		postVM.expanded = false;
		postVM.isBookmarked = post.isBookmarkedBy(user);
		postVM.isCommentable = isCommMember;

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
        Set<Pair<Long, SocialObjectType>> likesByUser = LikeManager.getLikedBy(user, likeCheckIds);

		for(int i = comments.size() - 1; i >= 0 ; i--) {
			Comment comment = comments.get(i);
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentVM.isLike = likesByUser.contains(new Pair<>(comment.id, SocialObjectType.COMMENT));
			commentsToShow.add(commentVM);
		}

        postVM.isLike = likesByUser.contains(new Pair<>(post.id, SocialObjectType.POST));
		postVM.comments = commentsToShow;
		
		return postVM;
	}
	
	public static CommunityPostVM communityPostVisitProfile(Post post, User user,User localUser) {
		CommunityPostVM postVM = new CommunityPostVM();
		postVM.postId = post.id;
		postVM.ownerId = post.owner.id;
		postVM.postedBy = post.owner.name;
		postVM.postedOn = post.getCreatedDate().getTime();
		postVM.updatedOn = post.getSocialUpdatedDate().getTime();
		postVM.postedTitle = post.title;
		postVM.postedText = post.body;
		postVM.noOfComments = post.noOfComments;
		postVM.postType = post.postType.name();
		postVM.communityName = post.community.name;
		postVM.communityIcon = post.community.icon;
		postVM.communityId = post.community.id;
		//need to write logic for showing no of views
		postVM.noOfViews = 0;
		postVM.noOfLikes = post.noOfLikes;
		postVM.expanded = false;
		postVM.isBookmarked = post.isBookmarkedBy(localUser);
		postVM.isCommentable = localUser.isMemberOf(post.community.id);

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
        Set<Pair<Long, SocialObjectType>> likesByUser = LikeManager.getLikedBy(user, likeCheckIds);

		for(int i = comments.size() - 1; i >= 0 ; i--) {
			Comment comment = comments.get(i);
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentVM.isLike = likesByUser.contains(new Pair<>(comment.id, SocialObjectType.COMMENT));
			commentsToShow.add(commentVM);
		}

        postVM.isLike = likesByUser.contains(new Pair<>(post.id, SocialObjectType.POST));
		postVM.comments = commentsToShow;
		
		return postVM;
	}
}
