package viewmodel;

import java.util.List;

import models.Comment;
import models.Resource;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostCommentVM {
	@JsonProperty("id") public Long commentId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public Long creationDate;
	@JsonProperty("hasImage") public boolean hasImage=false;
	@JsonProperty("imgs") public Long[] images;
	@JsonProperty("d") public String commentText;
	@JsonProperty("nol") public int noOfLikes;
	@JsonProperty("isLike") public boolean isLike=false;	
	
	public static CommunityPostCommentVM communityPostCommentVM(Comment comment) {
		CommunityPostCommentVM postCommentVM = new CommunityPostCommentVM();
		postCommentVM.commentId = comment.id;
		postCommentVM.ownerId = comment.owner.id;
		postCommentVM.name = comment.owner.name;
		postCommentVM.creationDate = comment.getCreatedDate().getTime();
		postCommentVM.commentText = comment.body;
		postCommentVM.noOfLikes =comment.noOfLikes;
		
		List<Resource> resources = null;
		if(comment.folder != null && comment.folder.resources != null && !comment.folder.resources.isEmpty()) {
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
