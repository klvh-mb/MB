package viewmodel;

import indexing.CommentIndex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommentIndexVM {
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("on") public String name;
	@JsonProperty("cd") public String creationDate;
	@JsonProperty("d") public String commentText;
	
	public CommentIndexVM(CommentIndex comment) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		this.ownerId = comment.owner_id;
		this.name = comment.name;
		this.creationDate = comment.creationDate;
		this.commentText = comment.commentText;
	}
}
