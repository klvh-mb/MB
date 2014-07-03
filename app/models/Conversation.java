package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.Creatable;
import domain.Updatable;

@Entity
public class Conversation extends domain.Entity implements Serializable,
		Creatable, Updatable {

	public Conversation(){}
	
	public Conversation(User user1, User user2) {
		this.user1 = user1;
		this.user2 = user2;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Required
	@ManyToOne
	public User user1;

	@Required
	@ManyToOne
	public User user2;

	public Date conv_time;
	
	public Date user1_time;
	
	public Date user2_time;

	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "conversation")
	public Set<Message> messages = new TreeSet<Message>();

	// public List<Message> messages = new ArrayList<Message>();
	/**
	 * mark readed all the messages for the user in the conversation
	 * 
	 * @param user
	 */
	

	public Message addMessage(User sender, String body) {
		Message message = new Message();
		message.body = body;
		message.userFrom = sender;
		message.conversation = this;
		this.messages.add(message);
		message.save();
		message.setCreatedDate(new Date());
		this.setUpdatedDate(new Date());
		this.save();
		if(this.user1 == sender){
			this.user1_time = new Date();
			this.conv_time = new Date();
		} else {
			this.user2_time = new Date();
			this.conv_time = new Date();
		}
		return message;

	}

	public static Conversation findBetween(User u1, User u2) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT c from Conversation c  where ((user1 = ?1 and user2 = ?2) or (user1 = ?2 and user2 = ?1))");
		q.setParameter(1, u1);
		q.setParameter(2, u2);
		
		try {
			
			return (Conversation) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static List<Conversation> findAllConversations(User user, int latest) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT c from Conversation c  where ((user1 = ?1 or user2 = ?1)) order by updated_date desc");
		q.setParameter(1, user);
	
		
		try {
			
			return  q.setMaxResults(latest).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Conversation startConversation(User sender, User receiver) {
		Conversation conversation = new Conversation(sender, receiver);
		Date date =  new Date();
		conversation.user1_time = date;
		conversation.conv_time = date;
		conversation.save();
		return conversation;
	}

	public String getLastMessage() {
		Message message;
		
		Query q = JPA
					.em()
					.createQuery(
							"SELECT m FROM Message m WHERE m.date=(SELECT MAX(date) FROM Message WHERE conversation_id = ?1)");
			q.setParameter(1, this.id);
		try{
			message = (Message) q.getSingleResult();
			return message.body;
		}catch(Exception e){
			return null;
		}
	}

	public static Conversation findById(Long id) {
		Query q = JPA.em().createQuery("SELECT c FROM Conversation c where id = ?1");
        q.setParameter(1, id);
        return (Conversation) q.getSingleResult();
	}

	public static Message sendMessage(User sender, User receiver, String msgText) {
		Conversation conversation = Conversation.findBetween(sender, receiver);
		if(conversation == null){
			conversation = Conversation.startConversation(sender, receiver);
		}
		return conversation.addMessage(sender, msgText);
	}

	public Boolean isReadedBy(User user) {
		if(this.user1 == user){
			return (this.user1_time.getTime() >= this.conv_time.getTime());
		} else { 
			return this.user2_time.getTime() >= this.conv_time.getTime();
		}
		
	}
	
}
