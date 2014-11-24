package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * 
 */
@Entity
public class CampaignActionsUser extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(CampaignActionsUser.class);
    
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long campaignId;
	
	public Long userId;
	
	public Boolean withdraw = false;
	
	public CampaignActionsUser() {}
	
	public CampaignActionsUser(Long campaignId, Long userId) {
	    this.campaignId = campaignId;
	    this.userId = userId;
	}
	
	@Transactional
    public static Boolean isJoinedCampaign(Long userId, Long campaignId) {
	    Query q = JPA.em().createQuery("Select count(u) from CampaignActionsUser u where userId = ?1 and campaignId = ?2 and withdraw = false");
        q.setParameter(1, userId);
        q.setParameter(2, campaignId);
        try {
            Long count = (Long)q.getSingleResult();
            if (count > 1) {
                logger.underlyingLogger().error(String.format("[u=%d][c=%d] User joined campaign %d times!",userId,campaignId,count));
            }
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
	}
	
	@Transactional
    public static void withdrawFromCampaign(Long userId, Long campaignId) {
	    Query q = JPA.em().createQuery("update CampaignActionsUser u set u.withdraw = true where u.userId = ?1 and u.campaignId = ?2");
	    q.setParameter(1, userId);
        q.setParameter(2, campaignId);
        q.executeUpdate();
	}
	
    @Transactional
    public static Long getJoinedUsersCount(Long campaignId) {
        Query q = JPA.em().createQuery("Select count(u) from CampaignActionsUser u where campaignId = ?1 and withdraw = false");
        q.setParameter(1, campaignId);
        Long count = (Long)q.getSingleResult();
        return count;
    }
}
