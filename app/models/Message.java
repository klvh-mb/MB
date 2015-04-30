package models;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.DefaultValues;
import domain.SocialObjectType;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class Message extends SocialObject implements Comparable<Message> {
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
	public Date date = new Date();

	@Column(length=500)
	public String body;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
    public Folder folder;
	
	public User receiver() {
	    if(this.conversation.user1.equals(userFrom)){
	      return conversation.user2;
	    } else {
	      return conversation.user1;
	    }
	}

	@Override
	public int compareTo(Message o) {
		 return date.compareTo(o.date);
	}

	public static List<Message> findBetween(Conversation conversation, Long offset, User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT c from Message c  where conversation_id = ?2 and c.date > ?3 order by c.date desc ");
		q.setParameter(2, conversation);
		if(conversation.user1 == user){
			conversation.user1_time = new Date();
			if(conversation.user1_archive_time == null){
				q.setParameter(3, new Date(0));
			} else {
				q.setParameter(3, conversation.user1_archive_time);
			}
			
		} else { 
			conversation.user2_time = new Date();
			if(conversation.user2_archive_time == null){
				q.setParameter(3, new Date(0));
			} else {
				q.setParameter(3, conversation.user2_archive_time);
			}
		}
		
		try {
			q.setFirstResult((int) (offset*DefaultValues.CONVERSATION_MESSAGE_COUNT));
			q.setMaxResults(DefaultValues.CONVERSATION_MESSAGE_COUNT);
			return (List<Message>) q.getResultList();
		} catch (NoResultException e) {
			return null;
		}
		
	}

	public static Message findById(Long id) {
		 Query q = JPA.em().createQuery("SELECT m FROM Message m where id = ?1");
	     q.setParameter(1, id);
	     return (Message) q.getSingleResult();
	}
	
	 public Resource addPrivatePhoto(File source,User owner) throws IOException {
	        ensureAlbumExist(owner);
	        Resource cover_photo = this.folder.addFile(source,
	                SocialObjectType.PRIVATE_PHOTO);
	        cover_photo.save();
	        return cover_photo;
	    }
	    
    public void ensureAlbumExist(User owner) {
        if (this.folder == null) {
            this.folder = Folder.createAlbum(owner, "message-ps", "", true);
            this.merge();
        }
    }
}
