package models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.mnt.exception.SocialObjectNotPostableException;

import domain.AuditListener;
import domain.Commentable;
import domain.Creatable;
import domain.Joinable;
import domain.Likeable;
import domain.Postable;
import domain.SocialObjectType;
import domain.Updatable;


@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@EntityListeners(AuditListener.class)
public abstract class SocialObject extends domain.Entity  implements Serializable, 
	Creatable, Updatable, Likeable, Postable, Commentable, Joinable  {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType objectType;
	
	public String name;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public SocialObject owner;
	
	protected  final void recordLike(User user) {
		SocialAction action = new SocialAction();
		action.action = SocialAction.Action.LIKED;
		action.target = this;
		action.actor = user;
		action.validateUniquenessAndCreate();
	}
	
	protected  final void recordJoinRequest(User user) {
		SocialAction action = new SocialAction();
		action.action = SocialAction.Action.JOIN_REQUESTED;
		action.target = this;
		action.actor = user;
		action.validateUniquenessAndCreate();
	}
	
	protected  final void recordPostOn(User user) {
		SocialAction action = new SocialAction();
		action.action = SocialAction.Action.POSTED_ON;
		action.target = this;
		action.actor = user;
		action.save();
	}
	
	protected  final void recordPost(SocialObject user) {
		SocialAction action = new SocialAction();
		action.action = SocialAction.Action.POSTED;
		action.target = this;
		action.actor = user;
		action.save();
	}
	
	@Override
	public void onLike(User user) throws SocialObjectNotLikableException {
		throw new SocialObjectNotLikableException("Please make sure Social Object you are liking is Likable");
	}
	
	@Override
	public void onComment(User user, String body) throws SocialObjectNotCommentableException {
		throw new SocialObjectNotCommentableException("Please make sure Social Object you are commenting is Commentable");
	}
	
	@Override
	public void onPost(User user, String body) throws SocialObjectNotPostableException {
		throw new SocialObjectNotPostableException("Please make sure Social Object you are posting  is Postable");
		
	}

	@Override
	public void onJoinRequest(User user) throws SocialObjectNotJoinableException {
		throw new SocialObjectNotJoinableException("Please make sure Social Object you are joining  is Joinable");
	}
	
	

}
