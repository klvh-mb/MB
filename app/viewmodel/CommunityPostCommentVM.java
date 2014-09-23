package viewmodel;

import java.util.List;

import models.Comment;
import models.Resource;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostCommentVM {
	@JsonProperty("id") public Long commentId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String postedBy;
	@JsonProperty("cd") public Long postedOn;
	@JsonProperty("hasImage") public boolean hasImage = false;
	@JsonProperty("imgs") public Long[] images;
	@JsonProperty("d") public String commentText;
	@JsonProperty("nol") public int noOfLikes;
	
	@JsonProperty("isO") public boolean isOwner = false;
	@JsonProperty("isLike") public boolean isLike = false;     // filled outside
	
	public static CommunityPostCommentVM communityPostCommentVM(Comment comment, User user) {
		CommunityPostCommentVM postCommentVM = new CommunityPostCommentVM();
		postCommentVM.commentId = comment.id;
		postCommentVM.ownerId = comment.owner.id;
		postCommentVM.postedBy = comment.owner.displayName;
		postCommentVM.postedOn = comment.getCreatedDate().getTime();
		postCommentVM.commentText = comment.body;
		postCommentVM.noOfLikes = comment.noOfLikes;
		postCommentVM.isOwner = comment.owner.id == user.id;
		
		List<Resource> resources = null;
		if(comment.folder != null && !CollectionUtils.isEmpty(comment.folder.resources)) {
			postCommentVM.hasImage = true;
			resources = Resource.findAllResourceOfFolder(comment.folder.id);
			postCommentVM.images = new Long[resources.size()];
			int i = 0;
			for (Resource rs : resources) {
				postCommentVM.images[i++] = rs.id;
			}
		}
		
		return postCommentVM;
	}
}
