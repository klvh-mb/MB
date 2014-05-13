package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Comment;
import models.Post;
import models.Resource;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostVM {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public long postedOn;
	@JsonProperty("pt") public String postedText;
	@JsonProperty("hasImage") public boolean hasImage;
	@JsonProperty("n_c") public int noOfComments;
	@JsonProperty("cs") public List<CommunityPostCommentVM> comments;
	@JsonProperty("imgs") public Long[] images;
	@JsonProperty("ts") public Long timestamp;
	@JsonProperty("type") public String postType;
	@JsonProperty("cn") public String communityName;
	@JsonProperty("cid") public Long communityId;
	@JsonProperty("nov") public int noOfViews;
	
	@JsonProperty("isLike") public boolean isLike=true;
	
	public static CommunityPostVM communityPostVM(Post post, User user) {
		CommunityPostVM postVM = new CommunityPostVM();
		postVM.postId = post.id;
		postVM.ownerId = post.owner.id;
		postVM.postedBy = post.owner.name;
		postVM.postedOn = post.getCreatedDate().getTime();
		postVM.postedText = post.body;
		postVM.noOfComments = post.comments.size();
		postVM.timestamp = post.getCreatedDate().getTime()/1000;
		postVM.postType = post.postType.name();
		postVM.communityName = post.community.name;
		postVM.communityId = post.community.id;
		//need to write logic for showing no of views
		postVM.noOfViews = 0;
		
		//need to write logic
		postVM.isLike = post.isLikedBy(user);
		
		if(post.folder != null && post.folder.resources != null && !post.folder.resources.isEmpty()) {
			postVM.hasImage = true;
			postVM.images = new Long[post.folder.resources.size()];
			int i = 0;
			for (Resource rs : post.folder.resources) {
				postVM.images[i++] = rs.id;
			}
		}
		
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		List<Comment> comments = post.getCommentsOfPost(3);
		for(int i = comments.size() - 1; i >= 0 ; i--) {
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comments.get(i));
			commentsToShow.add(commentVM);
		}
		
		postVM.comments = commentsToShow;
		
		return postVM;
	}
}
