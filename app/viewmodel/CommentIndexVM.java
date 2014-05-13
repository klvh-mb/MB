package viewmodel;

import indexing.CommentIndex;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommentIndexVM {
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public Long creationDate;
	@JsonProperty("d") public String commentText;
	
	public CommentIndexVM(CommentIndex comment) {
		this.ownerId = comment.owner_id;
		this.name = comment.name;
		this.creationDate = comment.creationDate;
		this.commentText = comment.commentText;
	}
}
