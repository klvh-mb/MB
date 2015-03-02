package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.joda.time.DateTime;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
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
	public Long recipient;
	 
	@Required
	public String message;
	
	public String URLs;
	
	@Enumerated(EnumType.STRING)
	public SocialObjectType targetType;
	
	public Long target;
	
	public long socialActionID;
	
	public String usersName;
	
	public Long count = 0L;

    // 0=unread, 1=read, 2=ignored, 3=accepted
	public int status = 0;
	
	@Enumerated(EnumType.STRING)
	public NotificationType notificationType;
	
	public static enum NotificationType {
        FRD_REQUEST,
        FRD_ACCEPTED,
		NEW_MESSAGE,
        COMM_JOIN_REQUEST,
        COMM_JOIN_APPROVED,
        COMM_INVITE_REQUEST,
		COMMENT,
		POSTED,
		LIKED,
		ANSWERED,
        QUESTIONED,
        WANTED_ANS,
        CAMPAIGN
	}


    public static Notification getNotification(Long recipient, NotificationType notificationType,
                                               Long target, SocialObjectType targetType) {
		String sql = "SELECT n FROM Notification n WHERE recipient=?1 and notificationType=?2 and target=?3 and targetType=?4 and status=0";
        Query query = JPA.em().createQuery(sql);
        query.setParameter(1, recipient);
        query.setParameter(2, notificationType);
        query.setParameter(3, target);
        query.setParameter(4, targetType);
        try {
            return (Notification) query.getSingleResult();
        } catch (NoResultException nre) {
        	return null;
        }
	}

	public String addToList(User addUser) {
        String addUserName = addUser.displayName;
        if (addUserName == null) {
            addUserName = "User";
        }

		if (this.usersName == null) {
            this.usersName = addUserName;
            this.count++;
		} else {
			if(this.usersName.toLowerCase().contains(addUserName.toLowerCase())){
				return this.usersName;
			}

            this.count++;

            int lastDelimIdx = this.usersName != null ? this.usersName.lastIndexOf(",") : -1;
			if(count >= 3 && lastDelimIdx != -1){
                long othersCount = count - 2;
				this.usersName = addUserName+", "+this.usersName.substring(0,lastDelimIdx)+" 與另外 "+othersCount+"人都";
			} else {
				this.usersName = addUserName+", "+this.usersName;
            }
		}
        return this.usersName;
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

	public Long getRecipient() {
		return recipient;
	}

	public void setRecipient(Long recipient) {
		this.recipient = recipient;
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

	public static void markAsRead(String ids) {
		
		String[] idsLong = ids.split(",");
		List<Long> data = new ArrayList<>(); 
		for (int i = 0; i < idsLong.length; i++) {     
		    data.add(Long.parseLong(idsLong[i]));     
		}  
		 Query query = JPA.em().createQuery("update Notification n set n.status = ?1, n.count = ?4 where n.id in ?3 and n.status = ?2");
		 query.setParameter(1, 1);
		 query.setParameter(2, 0);
		 query.setParameter(3, data);
		 query.setParameter(4, 0L);
		 query.executeUpdate();
	}

	@Transactional
	public static void purgeNotification() {
		Query query = JPA.em().createQuery("DELETE Notification n where n.status = ?1 and CREATED_DATE < ?2");
		 query.setParameter(1, 1);
		 DateTime sevenDaysBefore = (new DateTime()).minusDays(7);
		 query.setParameter(2, sevenDaysBefore.toDate());
		 query.executeUpdate();
	}
}
