package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.mnt.exception.SocialObjectNotJoinableException;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import domain.Joinable;
import domain.Likeable;
import domain.Postable;
import domain.SocialObjectType;

@Entity
public class Community extends SocialObject  implements Likeable, Postable, Joinable {
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public Set<CommunityPost> posts = new HashSet<CommunityPost>();
	  
	
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
	
	@Override
	public void onJoinRequest(User user) throws SocialObjectNotJoinableException {
		recordJoinRequest(user);
	}
	
	 
	
}
