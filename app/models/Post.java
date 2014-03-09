package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotPostableException;

import play.data.validation.Constraints.Required;
import domain.CommentType;
import domain.Commentable;
import domain.Likeable;
import domain.PostType;
import domain.SocialObjectType;

@Entity
public class Post extends SocialObject implements Likeable, Commentable {

	public Post() {}
	
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
	
	public Post(User actor, String post , Community community) {
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
