package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import domain.Likeable;
import domain.SocialObjectType;

@Entity
public class CommunityPost extends SocialObject implements Likeable {

	public CommunityPost(){
		this.objectType = SocialObjectType.COMMUNITY_POST;
	}
	
	@Required @Lob
	public String body;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	public Community community;
	
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

}
