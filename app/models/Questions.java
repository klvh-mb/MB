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
public class Questions extends SocialObject  implements Likeable, Commentable {
	
	public Questions(){
		this.objectType = SocialObjectType.COMMUNITY_QnA;
	}
	
	@Required @Lob
	public String body;
	
	
	@OneToMany
	public Set<Comment> comments;
		
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Community community;
	
	@Override
	public void onLike(User user) {
		recordLike(user);
	}
	
	public Questions(User actor, String QnA , Community community) {
		this();
		this.owner = actor;
		this.body = QnA;
		this.community = community;
	}
	
	@Override
	public void save() {
		super.save();
		super.recordQnA(owner);
	}
	
	
	//TOOD: Implementation
	@Override
	public Comment onComment(User user, String body, CommentType type) throws SocialObjectNotCommentableException {
		return null;
		
	}
	
}
