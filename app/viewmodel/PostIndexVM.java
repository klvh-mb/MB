package viewmodel;

import java.util.ArrayList;
import java.util.List;

import indexing.CommentIndex;
import indexing.PostIndex;

import org.codehaus.jackson.annotate.JsonProperty;

public class PostIndexVM {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public Long postedOn;
	@JsonProperty("pt") public String postedText;
	@JsonProperty("n_c") public int noOfComments;
	@JsonProperty("cs") public List<CommentIndexVM> comments;
	
	public PostIndexVM(PostIndex post) {
		this.postId = post.post_id;
		this.postedBy = post.postedBy;
		this.postedOn = post.postedOn.getTime();
		this.postedText = post.description;
		this.ownerId = post.owner_id;
		this.noOfComments = post.noOfComments;
		
		List<CommentIndexVM> comments = new ArrayList<>();
		for(CommentIndex comment: post.comments) {
			CommentIndexVM c = new CommentIndexVM(comment);
			comments.add(c);
		}
		this.comments = comments;
	}
}
