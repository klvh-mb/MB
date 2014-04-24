package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
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
	public void onLike(User user) {
		recordLike(user);
	}
  @Required @Lob
  public String body;
  
  @Required
  public CommentType commentType;

  @Override
  public int compareTo(Comment o) {
    return date.compareTo(o.date);
  }
  
  @Override
	public void save() {
		super.save();
		recordCommentOnCommunityPost(owner);
	}
}
