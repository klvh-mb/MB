package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotJoinableException;

import domain.Joinable;
import domain.Likeable;
import domain.Postable;
import domain.SocialObjectType;

@Entity
public class Community extends SocialObject  implements Likeable, Postable, Joinable {
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public Set<CommunityPost> posts = new HashSet<CommunityPost>();
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public Set<CommunityQnA> qnA = new HashSet<CommunityQnA>();
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public Set<User> members = new HashSet<User>();
	
	@Enumerated(EnumType.ORDINAL)
	public CommunityType communityType = CommunityType.CLOSE;
	
	public static enum CommunityType {
		OPEN,
		CLOSE,
		PRIVATE
	}
	
	public Community(){
		this.objectType = SocialObjectType.COMMUNITY;
	}
	
	public Community(String name,User owner) {
		this();
		this.name = name;
		this.owner = owner;
	}
	
	@Override
	public void onLike(User user) {
		recordLike(user);
	}
	
	@Override
	@Transactional
	public void onPost(User user, String body) {
		CommunityPost post = new CommunityPost(user, body, this);
		post.save();
		this.posts.add(post);
		JPA.em().merge(this);
		//recordPostOn(user);
	}
	
	public void onQuestionPost(User user, String question) {
		
	}
	
	public void onAnswerPost(User user, String answer) {
		
	}
	
	@Override
	public void onJoinRequest(User user) throws SocialObjectNotJoinableException {
		if( communityType != CommunityType.OPEN) {
			recordJoinRequest(user);
		} else {
			this.members.add(user);
			JPA.em().merge(this);
		}
	}
	
	 
	@Override
	@Transactional
	public void onJoinRequestAccepted(User user)
			throws SocialObjectNotJoinableException {
		this.members.add(user);
		JPA.em().merge(this);
		recordJoinRequestAccepted(user);
	}
	
}
