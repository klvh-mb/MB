package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class CampaignWinner extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(CampaignWinner.class);
    
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long campaignId;
	
	public Long userId;
	
	@Column(length = 1024)
    public String note;
	
	@Enumerated(EnumType.STRING)
    public WinnerState winnerState = WinnerState.SELECTED;
    
    public static enum WinnerState {
        SELECTED,
        ANNOUNCED,
        ACCEPTED,
        DELIVERED,
        REMOVED,
        PROBLEM,
        OTHER
    }
    
    public CampaignWinner() {}
    
    public CampaignWinner(Long campaignId, Long userId) {
        this(campaignId, userId, "");
    }
    
	public CampaignWinner(Long campaignId, Long userId, String note) {
	    this.campaignId = campaignId;
	    this.userId = userId;
	    this.note = note;
	}
	
	public static CampaignWinner findById(Long id) {
        Query q = JPA.em().createQuery("SELECT w FROM CampaignWinner w where w.id = ?1");
        q.setParameter(1, id);
        try {
            return (CampaignWinner) q.getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }
	
	@Transactional
    public static CampaignWinner getWinner(Long campaignId, Long userId) {
        Query q = JPA.em().createQuery("Select u from CampaignWinner u where campaignId = ?1 and userId = ?2");
        q.setParameter(1, campaignId);
        q.setParameter(2, userId);
        try {
            return (CampaignWinner)q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
	
	@Transactional
    public static List<CampaignWinner> getWinners(Long campaignId) {
	    Query q = JPA.em().createQuery("Select u from CampaignWinner u where campaignId = ?1");
        q.setParameter(1, campaignId);
        try {
            return (List<CampaignWinner>)q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
	}
	
    public void update() {
        this.merge();
    }
    
    public void save() {
        super.save();
    }
}
