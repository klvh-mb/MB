package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;

@Entity
public class Message  extends SocialObject implements Comparable<Message> {
	/*
	@Id
	public Long id;*/
	
	@Required
	@ManyToOne
	public Conversation conversation;
	  
	@Required
	@ManyToOne
	public User userFrom;
	  
	@Required
	public Boolean readed = false;
	  
	@Required
	public Date date = new Date();

	@Required @Lob
	public String body;
	
	public User receiver() {
	    if(this.conversation.user1.equals(userFrom)){
	      return conversation.user2;
	    } else {
	      return conversation.user1;
	    }
	  }

	  public void markReaded() {
	    this.readed = true;
	    //this.save();
	  }

	@Override
	public int compareTo(Message o) {
		 return date.compareTo(o.date);
	}

	
}
