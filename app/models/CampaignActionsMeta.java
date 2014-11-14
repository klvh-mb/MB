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
public class CampaignActionsMeta {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long campaignId;
	
	public String validator;
    
	public CampaignActionsMeta(Long campaignId, String validator) {
	    this.campaignId = campaignId;
	    this.validator = validator;
	}
	
	@Transactional
    public static CampaignActionsMeta getMeta(Long campaignId) {
	    Query q = JPA.em().createQuery("Select m from CampaignActionsMeta m where campaignId = ?1");
        q.setParameter(1, campaignId);
        try {
            return (CampaignActionsMeta)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}
	
	@Transactional
	public void save() {
	    JPA.em().persist(this);
	    JPA.em().flush();
	}
	  
	@Transactional
	public void delete() {
	    JPA.em().remove(this);
	}
	  
	@Transactional
	public void merge() {
	    JPA.em().merge(this);
	}

	@Transactional
	public void refresh() {
	    JPA.em().refresh(this);
	}
	
    @Override
    public String toString() {
        return "CampaignActionsMeta{" +
                "campaignId='" + this.campaignId + '\'' +
                "validator='" + this.validator + '\'' +
                '}';
    }
}
