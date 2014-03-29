package viewmodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Comment;
import models.Post;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostVM {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public String postedOn;
	@JsonProperty("pt") public String postedText;
	@JsonProperty("hasImage") public boolean hasImage;
	@JsonProperty("n_c") public int noOfComments;
	@JsonProperty("cs") public List<CommunityPostCommentVM> comments;
	
	public static CommunityPostVM communityPostVM(Post post) {
		DateFormat df = new SimpleDateFormat("MMMM d, yyyy");
		
		CommunityPostVM postVM = new CommunityPostVM();
		postVM.postId = post.id;
		postVM.ownerId = post.owner.id;
		postVM.postedBy = post.owner.name;
		postVM.postedOn = df.format(post.getCreatedDate());
		postVM.postedText = post.body;
		postVM.noOfComments = post.comments.size();
		
		boolean hasImage = true;
		postVM.hasImage = hasImage;
		
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		for(Comment comment : post.comments) {
			System.out.println(comment.owner.name);
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentsToShow.add(commentVM);
		}
		
		postVM.comments = commentsToShow;
		
		return postVM;
	}
}
