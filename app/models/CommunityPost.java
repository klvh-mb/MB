package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.mnt.exception.SocialObjectNotCommentableException;

import play.data.validation.Constraints.Required;
import domain.CommentType;
import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;

@Entity
public class CommunityPost extends SocialObject implements Likeable, Commentable {

	public CommunityPost(){
		this.objectType = SocialObjectType.COMMUNITY_POST;
	}
	
	@Required @Lob
	public String body;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Community community;
	
	@OneToMany
	public Set<Comment> comments;
	
	@Override
	public void onLike(User user) {
		recordLike(user);
	}
	
	public CommunityPost(User actor, String post , Community community) {
		this();
		this.owner = actor;
		this.body = post;
		this.community = community;
	}
	
	@Override
	public void save() {
		super.save();
		recordPost(owner);
	}
	
	//TOOD: Implementation
	@Override
	public void onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
		
	}

}
