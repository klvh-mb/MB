package viewmodel;

import models.Comment;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostCommentVM {
	@JsonProperty("id") public Long commentId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public Long creationDate;
	@JsonProperty("d") public String commentText;
	@JsonProperty("nol") public int noOfLikes;
	@JsonProperty("isLike") public boolean isLike=true;	
	
	public static CommunityPostCommentVM communityPostCommentVM(Comment comment) {
		CommunityPostCommentVM postCommentVM = new CommunityPostCommentVM();
		postCommentVM.commentId = comment.id;
		postCommentVM.ownerId = comment.owner.id;
		postCommentVM.name = comment.owner.name;
		postCommentVM.creationDate = comment.getCreatedDate().getTime();
		postCommentVM.commentText = comment.body;
		postCommentVM.noOfLikes =comment.noOfLikes;
		return postCommentVM;
	}
}
