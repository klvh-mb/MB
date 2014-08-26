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
	
	public Date user1_archive_time;
	
	public Date user2_archive_time;

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
						"SELECT c from Conversation c  where ((user1 = ?1 and (user1_archive_time < conv_time or user1_archive_time is NULL )) or (user2 = ?1 and (user2_archive_time < conv_time or user2_archive_time is NULL))) order by updated_date desc");
		q.setParameter(1, user);
		
		try {
			return  q.setMaxResults(latest).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Conversation startConversation(User sender, User receiver) {
		Conversation conversation;
		conversation = findBetween(sender, receiver);
		if(conversation == null)
		conversation = new Conversation(sender, receiver);
		Date date =  new Date();
		conversation.user1_time = date;
		conversation.conv_time = date;
		conversation.save();
		return conversation;
	}

	public String getLastMessage(User user) {
		Message message;
		
		Query q = JPA
					.em()
					.createQuery(
							"SELECT m FROM Message m WHERE m.date=(SELECT MAX(date) FROM Message WHERE conversation_id = ?1) and m.date > ?2");
			q.setParameter(1, this.id);
			if(this.user1 == user){
				if(this.user2_archive_time == null){
					q.setParameter(2, new Date(0));
				} else {
					q.setParameter(2, this.user2_archive_time);
				}
			} else {
				if(this.user1_archive_time == null){
					q.setParameter(2, new Date(0));
				} else {
					q.setParameter(2, this.user1_archive_time);
				}
			}
		try{
			message = (Message) q.getSingleResult();
			String body = message.body;
			String data = null;
			int i = body.indexOf("<img");
			int pointer = i;
			if(i < 21 && i != -1){
				data = body.substring(0, i);
				while( i < 20 ){
					if(body.length() == pointer){
						break;
					}
					if(body.indexOf("<img",pointer-1) == pointer){
						data = data.concat(body.substring(body.indexOf("<img",pointer-1), body.indexOf("\">",pointer-1)+2));
						pointer = body.indexOf("\">",pointer-1)+2;
					} else { 
						data = data + body.charAt(Math.min(body.length(), pointer));
						pointer++;
					}
					i++;
				}
				
			} else {
				data = body.substring(0, Math.min(body.length(), 20));
			}
			return data;
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
		if(msgText != null){
			for(Emoticon emoticon : Emoticon.getEmoticons()){
				msgText = msgText.replace(emoticon.name, emoticon.url);
			}
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
	
	public static void archiveConversation(Long id, User user) {
		// TODO Auto-generated method stub
		Conversation conversation = Conversation.findById(id);
		conversation.setArchiveTime(user);
	}
	
	private void deleteConversation() {
		// TODO Auto-generated method stub
		Query q = JPA.em().createQuery("DELETE FROM Message where conversation_id = ?1");
		q.setParameter(1, this.id);
		q.executeUpdate();
		q = JPA.em().createQuery("DELETE FROM Conversation where id = ?1");
        q.setParameter(1, id);
        q.executeUpdate();
	}
	
	private void setArchiveTime(User user){
		if(this.user1 == user){
			if(this.user2_archive_time == null){
				user1_archive_time = new Date();
			} else {
				if (this.user2_archive_time.compareTo(this.conv_time) < 0) {
					this.user1_archive_time = new Date();
		        } else {
		        	this.deleteConversation();
		        }
			}
		} else {
			if(this.user1_archive_time == null){
				user2_archive_time = new Date();
			} else {
				if (this.user1_archive_time.compareTo(this.conv_time) < 0) {
					this.user2_archive_time = new Date();
		        } else {
		        	this.deleteConversation();
		        }
			}
		}
	}

	public Long getMessageCount(User user) {
		 Query q = JPA.em().createQuery("Select count(c) from Conversation c where c.id = ?2 and ( c.user1.id = ?1 and (c.user1_time < c.conv_time or c.user1_time is null)) or (c.user2.id = ?1 and (user2_time < c.conv_time or c.user2_time is null ))");
	        q.setParameter(1, user.id);
	        q.setParameter(2, this.id);
	        Long ret = (Long) q.getSingleResult();
	        return ret;
	}
}
