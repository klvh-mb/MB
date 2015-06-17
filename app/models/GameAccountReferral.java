package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import java.util.Date;

/**
 *  GameAccountReferral should only be applicable to native signup.
 *  - Keeping record of which promoCode was used with which signup email.
 */
@Entity
public class GameAccountReferral extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountReferral.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Required
    private Long userId;
    
	@Required
    private String promoCode;

    private String signupEmail;

    /**
     * Ctor
     */
	public GameAccountReferral() {}


    /**
     * @param promoCode
     * @param user
     */
    public static void addReferralRecord(String promoCode, User user) {
        GameAccountReferral existingReferral = findByUserId(user.id);
        if (existingReferral != null) {
            throw new IllegalStateException("User already has referral: id="+user.id+" email="+user.email);
        }

        GameAccountReferral referral = new GameAccountReferral();
        referral.setPromoCode(promoCode);
        referral.setUserId(user.id);
        referral.setSignupEmail(user.email);
        referral.setCreatedDate(new Date());
        referral.save();
    }

    /**
     * @param userId
     * @return
     */
    public static GameAccountReferral findByUserId(Long userId) {
	    try {
	        Query q = JPA.em().createQuery("SELECT r FROM GameAccountReferral r where userId = ?1");
	        q.setParameter(1, userId);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}
    
    /**
     * @param email
     * @return
     */
    public static GameAccountReferral findBySignupEmail(String email) {
	    try {
	        Query q = JPA.em().createQuery("SELECT r FROM GameAccountReferral r where signupEmail = ?1");
	        q.setParameter(1, email);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getSignupEmail() {
        return signupEmail;
    }

    public void setSignupEmail(String signupEmail) {
        this.signupEmail = signupEmail;
    }
}
