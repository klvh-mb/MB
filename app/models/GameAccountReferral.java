package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class GameAccountReferral extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountReferral.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long sender_user_id;

    public String promoCode;

	public Long invite_user_id;

    /**
     * Ctor
     */
	public GameAccountReferral() {}

    public Long getSender_user_id() {
		return sender_user_id;
	}

	public void setSender_user_id(Long sender_user_id) {
		this.sender_user_id = sender_user_id;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Long getInvite_user_id() {
		return invite_user_id;
	}

	public void setInvite_user_id(Long invite_user_id) {
		this.invite_user_id = invite_user_id;
	}

	public static GameAccountReferral findByInviteUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountReferral u where invite_user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

	public static GameAccountReferral findByPromoCode(String promoCode) {
		try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountReferral u where promoCode = ?1");
	        q.setParameter(1, promoCode);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

	public static void setUser(Long id2, String promoCode) {
		GameAccountReferral referral = GameAccountReferral.findByPromoCode(promoCode);
	    referral.invite_user_id = id2;
	    referral.merge();
	}
}
