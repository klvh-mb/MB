package viewmodel;

import models.Comment;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostCommentVM {
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public Long creationDate;
	@JsonProperty("d") public String commentText;
	
	public static CommunityPostCommentVM communityPostCommentVM(Comment comment) {
		CommunityPostCommentVM postCommentVM = new CommunityPostCommentVM();
		postCommentVM.ownerId = comment.owner.id;
		postCommentVM.name = comment.owner.name;
		postCommentVM.creationDate = comment.getCreatedDate().getTime();
		postCommentVM.commentText = comment.body;
		return postCommentVM;
	}
}
