package models;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.SocialObjectType;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.i18n.Messages;

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
	public Date date = new Date();

	@Lob
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

	public static List<Message> findBetween(Conversation id, Long offset, User user) {
		Query q = JPA
				.em()
				.createQuery(
						"SELECT c from Message c  where conversation_id = ?2 order by c.date desc ");
		q.setParameter(2, id);
	
		if(id.user1 == user){
			id.user1_time = new Date();
		} else { 
			id.user2_time = new Date();
		}
		
		try {
			q.setFirstResult((int) (offset*20));
			q.setMaxResults(20);
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
            this.folder = createAlbum("message-photos",
                    Messages.get("message-photos.message-profile.description"), true, owner);
            this.merge();
        }
    }
    
    private Folder createAlbum(String name, String description, Boolean system, User owner) {
        Folder folder = createFolder(name, description,
                SocialObjectType.FOLDER, system, owner);
        return folder;
    }
    
    private Folder createFolder(String name, String description,
            SocialObjectType type, Boolean system, User owner) {

        Folder folder = new Folder(name);
        folder.owner = owner;
        folder.name = name;
        folder.description = description;
        folder.objectType = type;
        folder.system = system;
        folder.save();
        return folder;
    }

	
}
