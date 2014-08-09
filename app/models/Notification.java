package models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import domain.AuditListener;
import domain.Creatable;
import domain.SocialObjectType;
import domain.Updatable;

@Entity
@EntityListeners(AuditListener.class)
public class Notification  extends domain.Entity implements Serializable, Creatable, Updatable  {
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long id;
	
	/*To whom this notification is intended for*/
	 @Required
	public Long recipetent;
	 
	@Required
	public String message;
	
	public String URLs;
	
	public long socialActionID;
	
	public String usersName;
	
	public Long count = 0L;
	
	public int status = 0;
	
	@Enumerated(EnumType.STRING)
	public NotificationType notificationType;
	
	public static enum NotificationType {
		FRIEND_REQUEST,
		FRIEND_ACCEPTED,
		NEW_MESSAGE,
		COMMUNITY_JOIN_REQUEST, 
		COMMUNITY_JOIN_APPROVED,
		COMMUNITY_INVITE_REQUEST,
		COMMENT,
		POSTED,
		LIKED,
		ANSWERED, POSTED_QUESTION
	}

	

	public static Notification getNotification(Long socialActionID, Long recipetent,
			NotificationType notificationType) {
		String sql = "SELECT n FROM Notification n WHERE socialActionID=?1 and recipetent =?2 and notificationType = ?3";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, socialActionID);
        query.setParameter(2, recipetent);
        query.setParameter(3, notificationType);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        	return null;
        }
	}

	public void addToList(User addUser) {
		if(this.usersName == null){
			this.usersName = addUser.displayName;
		} else {
			if(this.usersName.toLowerCase().contains(addUser.displayName.toLowerCase())){
				return;
			}
			if(this.usersName.split(",").length > 3){
				this.usersName = this.usersName.substring(0,this.usersName.lastIndexOf(","));
			}
				this.usersName = addUser.displayName+","+this.usersName;
		}
	}
	
	
	
	
	
	public void changeStatus(int status) {
		this.status = status;
	    save();
	}

	public String getUsersName() {
		return usersName;
	}

	public void setUsersName(String usersName) {
		this.usersName = usersName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getRecipetent() {
		return recipetent;
	}

	public void setRecipetent(Long recipetent) {
		this.recipetent = recipetent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
	
    public static Notification findById(Long id) {
        String sql = "SELECT n FROM Notification n WHERE id=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, id);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    public static Notification findBySocialAction(SocialRelation sr) {
        String sql = "SELECT n FROM Notification n WHERE SocialAction_id=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, sr);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }
    
    public static Notification findBySocialActionID(Long id) {
        String sql = "SELECT n FROM Notification n WHERE socialActionID=?1";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, id);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }
}
