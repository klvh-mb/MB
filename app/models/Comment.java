package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import models.SocialRelation.Action;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.CommentType;
import domain.Creatable;
import domain.Likeable;

/**
 * A Comment by an User on a SocialObject 
 *
 */

@Entity
public class Comment extends SocialObject implements Comparable<Comment>, Likeable,Serializable, Creatable {
  
  public Comment(){}
  public Comment(SocialObject socialObject, User user, String body) {
    this.owner = user;
    this.socialObject = socialObject.id;
    this.body = body;
  }

  @Required
  
  public Long socialObject;
  
  @Required
  public Date date = new Date();
  @Override
	public void onLikedBy(User user) {
		recordLike(user);
	}
  @Required @Lob
  public String body;
  
  @Required
  public CommentType commentType;

  public int noOfLikes=0;
  
  @Override
  public int compareTo(Comment o) {
    return date.compareTo(o.date);
  }
  
  @Override
	public void save() {
		super.save();
		recordCommentOnCommunityPost(owner);
	}
  
  public static Comment findById(Long id) {
		Query q = JPA.em().createQuery("SELECT c FROM Comment c where id = ?1");
		q.setParameter(1, id);
		return (Comment) q.getSingleResult();
	}
  
  public boolean isLikedBy(User user) {
		Query q = JPA.em().createQuery("Select sr from SocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, Action.LIKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		SocialRelation sr = null;
		try {
			sr = (SocialRelation)q.getSingleResult();
			System.out.println("SR ::"+sr.id);
		}
		catch(NoResultException nre) {
			System.out.println("No Result For SR");
			return true;
		}
		return false;
	}
  
}
