package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.Likeable;
import domain.SocialObjectType;

@Entity
public class Campaign extends SocialObject implements Commentable, Likeable {
    private static final play.api.Logger logger = play.api.Logger.apply(Campaign.class);

	public String image;
	
	@Lob
	public String description;
	
	public int noOfLikes = 0;
    
	public int noOfViews = 0;
	
	public Date startDate;

    public Date endDate;
	
	@Enumerated(EnumType.STRING)
    public CampaignState campaignState;
	
	public static enum CampaignState {
        NEW,
        PUBLISHED,
        STARTED,
        ENDED,
        ANNOUNCED,
        CLOSED
    }
	
	@Enumerated(EnumType.STRING)
	public CampaignType campaignType;
	
    public static enum CampaignType {
        ACTIONS,
        QUESTIONS,
        VOTING,
        PHOTO_CONTEST
    }
    
    @Lob
    public String announcement;
	
    @Enumerated(EnumType.STRING)
    public AnnouncementType announcementType;
    
    public static enum AnnouncementType {
        WINNERS,
        CUSTOM
    }
    
    public Campaign() {}
    
	public Campaign(String name, String description, CampaignType campaignType, Date startDate, Date endDate) {
		this.name = name;
		this.description = description;
		this.campaignType = campaignType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.objectType = SocialObjectType.CAMPAIGN;
	}
	
	@Transactional
	public static List<Campaign> getAllCampaigns() {
		Query q = JPA.em().createQuery("Select c from Campaign c where c.campaignState in (?1,?2,?3,?4) and c.deleted = false order by startDate desc,id desc");
		q.setParameter(1, CampaignState.PUBLISHED);
		q.setParameter(2, CampaignState.STARTED);
		q.setParameter(3, CampaignState.ENDED);
		q.setParameter(4, CampaignState.ANNOUNCED);
		//q.setMaxResults(100);
		return (List<Campaign>)q.getResultList();
	}
	
	public static Campaign findById(Long id) {
		Query q = JPA.em().createQuery("SELECT c FROM Campaign c where c.id = ?1 and c.deleted = false");
		q.setParameter(1, id);
		try {
		    return (Campaign) q.getSingleResult();
		} catch(NoResultException e) {
            return null;
        }
	}
	
	@Override
	public void onLikedBy(User user) {
		recordLike(user);
		this.noOfLikes++;
		user.likesCount++;
	}

    @Override
    public void onUnlikedBy(User user) throws SocialObjectNotLikableException {
        this.noOfLikes--;
        user.likesCount--;
    }
    
	public boolean isLikedBy(User user) throws SocialObjectNotLikableException {
		Query q = JPA.em().createQuery("Select sr from PrimarySocialRelation sr where sr.action=?1 and sr.actor=?2 " +
				"and sr.target=?3 and sr.targetType=?4");
		q.setParameter(1, PrimarySocialRelation.Action.LIKED);
		q.setParameter(2, user.id);
		q.setParameter(3, this.id);
		q.setParameter(4, this.objectType);
		PrimarySocialRelation sr = null;
		try {
			sr = (PrimarySocialRelation)q.getSingleResult();
		}
		catch(NoResultException nre) {
			return false;
		}
		return true;
	}
	
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Campaign) {
            final Campaign other = (Campaign) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
	
    @Override
    public String toString() {
        return "Campaign{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "views='" + noOfViews + '\'' +
                "likes='" + noOfLikes + '\'' +
                "start='" + startDate + '\'' +
                "end='" + endDate + '\'' +
                "type='" + campaignType.name() + '\'' +
                "state='" + campaignState.name() + '\'' +
                '}';
    }
}
