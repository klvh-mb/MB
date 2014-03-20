package models;

import java.io.Serializable;
import java.util.Date;
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

	@Required
	public Date date = new Date();

	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "conversation")
	public Set<Message> messages = new TreeSet<Message>();

	// public List<Message> messages = new ArrayList<Message>();
	/**
	 * mark readed all the messages for the user in the conversation
	 * 
	 * @param user
	 */
	public void markReaded(User user) {
		for (Message message : this.messages) {
			if (!message.readed && !message.userFrom.equals(user)) {
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

	public static Conversation findBetween(User u1, User u2) {
	//	System.out.println(user1);
		Query q = JPA
				.em()
				.createQuery(
						"SELECT c from Conversation c  where ((user1 = ?1 and user2 = ?2) or (user1 = ?2 and user2 = ?1))");
		q.setParameter(1, u1);
		q.setParameter(2, u2);
		
		try {
			
			return (Conversation) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("hi");
			return null;
		}
	}

}
