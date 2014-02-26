package models;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import domain.AuditListener;
import domain.Creatable;
import domain.Updatable;

//@Entity
//@EntityListeners(AuditListener.class)
public class Conversation extends domain.Entity  implements Serializable, Creatable, Updatable   {

	@Id
	public Long id;
	
	public Conversation(User user1, User user2) {
	    this.user1 = user1;
	    this.user2 = user2;
	 }
	  
	 @Required
	 @ManyToOne
	 public User user1;
	  
	 @Required
	 @ManyToOne
	 public User user2;
	  
	 @Required
	 public Date date = new Date();
	 
	 @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conversation")
	 public Set<Message> messages = new TreeSet<Message>();
	 
	 /**
	   * mark readed all the messages for the user in the conversation
	   * @param user
	   */
	  public void markReaded(User user) {
	    for (Message message : this.messages) {
	      if(!message.readed && !message.userFrom.equals(user)) {
	        message.markReaded();
	      }
	    }
	  }
	  
	  public Message addMessage(User sender, String body) {
		    Message message = new Message();
		    message.body = body;
		    message.userFrom = sender;
		    message.conversation = this;
		    this.messages.add(message);
		    this.save(); 
		    return message;
		    
	 }

}
