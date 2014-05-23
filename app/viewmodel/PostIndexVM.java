package viewmodel;

import indexing.CommentIndex;
import indexing.PostIndex;

import java.util.ArrayList;
import java.util.List;

import models.Resource;

import org.codehaus.jackson.annotate.JsonProperty;

public class PostIndexVM {
	@JsonProperty("id") public Long postId;
	@JsonProperty("oid") public Long ownerId;
	@JsonProperty("p") public String postedBy;
	@JsonProperty("t") public Long postedOn;
	@JsonProperty("pt") public String postedText;
	@JsonProperty("n_c") public int noOfComments;
	@JsonProperty("cs") public List<CommentIndexVM> comments;
	@JsonProperty("hasImage") public boolean hasImage;
	@JsonProperty("imgs") public List<Long> images;
	@JsonProperty("cn") public String communityName;
	
	public PostIndexVM(PostIndex post) {
		this.postId = post.post_id;
		this.postedBy = post.postedBy;
		this.postedOn = post.postedOn.getTime();
		this.postedText = post.description;
		this.ownerId = post.owner_id;
		this.noOfComments = post.noOfComments;
		this.communityName = post.communityName;
		this.images = new ArrayList<Long>();
		
		List<Resource> resources = null;
		if(post.hasImages != false ) {
			this.hasImage = true;
			resources = Resource.findAllResourceOfFolder(post.folder_id);
			
			if(resources.size()>0){
				for(Resource rs : resources) {
					this.images.add(rs.id);
				}
			}
		}
		
		List<CommentIndexVM> comments = new ArrayList<>();
		for(CommentIndex comment: post.comments) {
			CommentIndexVM c = new CommentIndexVM(comment);
			comments.add(c);
		}
		this.comments = comments;
	}
}
