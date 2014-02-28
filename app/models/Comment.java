package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import domain.AuditListener;
import domain.CommentType;
import domain.Creatable;

/**
 * A Comment by an User on a SocialObject 
 *
 */

@Entity
@EntityListeners(AuditListener.class)
public class Comment extends domain.Entity implements Comparable<Comment>, Serializable, Creatable {
  
  @Id
  public Long id;
  
  public Comment(SocialObject socialObject, User user, String body) {
    this.user = user;
    this.socialObject = socialObject;
    this.body = body;
  }
  
  @Required
  @ManyToOne
  public User user;

  @Required
  @ManyToOne
  public SocialObject socialObject;
  
  @Required
  public Date date = new Date();
  
  @Required @Lob
  public String body;
  
  @Required
  public CommentType commentType;

  @Override
  public int compareTo(Comment o) {
    return date.compareTo(o.date);
  }
  
}
