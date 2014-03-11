package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotJoinableException;

import domain.Joinable;
import domain.Likeable;
import domain.PostType;
import domain.Postable;
import domain.SocialObjectType;

@Entity
public class Community extends SocialObject  implements Likeable, Postable, Joinable {
	
	@OneToMany(cascade=CascadeType.REMOVE)
	public Set<Post> posts = new HashSet<Post>();
	
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
	public SocialObject onPost(User user, String body, PostType type) {
		Post post = new Post(user, body, this);
		
		if (type == PostType.QUESTION) {
			post.objectType = SocialObjectType.QUESTION;
			post.postType = type;
		}
		
		if (type == PostType.SIMPLE) {
			post.objectType = SocialObjectType.POST;
			post.postType = type;
		}
		post.save();
		this.posts.add(post);
		JPA.em().merge(this);
		//recordPostOn(user);
		return post;
		
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
	
	public static List<Community> search(String q) {
		CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Community> criteria = builder.createQuery(Community.class);
		Root<Community> root = criteria.from( Community.class );
		criteria.select(root);
		Predicate predicate = builder.or(builder.like(root.<String>get("name"), "%" + q + "%"));
		criteria.where(predicate);
		return JPA.em().createQuery(criteria).getResultList();
	}
	
}
