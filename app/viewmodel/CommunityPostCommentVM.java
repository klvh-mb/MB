package viewmodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.Comment;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostCommentVM {
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public String creationDate;
	@JsonProperty("d") public String commentText;
	
	public static CommunityPostCommentVM communityPostCommentVM(Comment comment) {
		DateFormat df = new SimpleDateFormat("d MMM, yyyy");
		
		CommunityPostCommentVM postCommentVM = new CommunityPostCommentVM();
		postCommentVM.ownerId = comment.owner.id;
		postCommentVM.name = comment.owner.name;
		postCommentVM.creationDate = df.format(new Date());
		postCommentVM.commentText = comment.body;
		return postCommentVM;
	}
}
