package models;

import javax.persistence.Entity;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.mnt.exception.SocialObjectNotPostableException;

import domain.CommentType;
import domain.SocialObjectType;

@Entity
public class User extends SocialObject {

	public String firstName;
	
	public User(){
		this.objectType = SocialObjectType.USER;
	}
	
	public User (String firstName) {
		this();
		this.firstName = firstName;
		this.name = firstName;
	}
	
	public void likesOn(SocialObject target) throws SocialObjectNotLikableException {
		target.onLike(this);
	}
	
	public void postedOn(SocialObject target, String post) throws SocialObjectNotPostableException {
		target.onPost(this, post);
	}
	
	public void requestedToJoin(SocialObject target) throws SocialObjectNotJoinableException {
		target.onJoinRequest(this);
	}
	
	//TODO: Write Test
	public void commentedOn(SocialObject target, String comment) throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.SIMPLE);
	}
	
	//TODO: Write Test
	public void answeredOn(SocialObject target, String comment) throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.ANSWER);
	}
	
	//TODO: Write Test
	public void questionedOn(SocialObject target, String comment) throws SocialObjectNotCommentableException {
		target.onComment(this, comment, CommentType.QUESTION);
	}
	
	public void joinRequestAccepted(SocialObject target, User toBeMemeber) throws SocialObjectNotJoinableException {
		target.onJoinRequestAccepted(toBeMemeber);
	}
	
	public void markNotificationRead(Notification notification) {
		notification.markNotificationRead();
	}
	
	

}
