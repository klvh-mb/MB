package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

import java.util.Date;
import java.util.List;

/**
 *  GameAccountReferral
 *  - Keep record of which promoCode (Referrer) was used with which User (new Signup).
 */
@Entity
public class GameAccountReferral extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountReferral.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@Required
    private String promoCode;

    private Long userId;
    private String signupEmail;

    @Required
    private boolean validated = false;  // signup has been valideted.


    /**
     * Ctor
     */
	public GameAccountReferral() {}

    /**
     * For native signup - create nonValidated referral.
     * @param promoCode
     * @param email
     */
    public static void addNonValidatedReferral(String promoCode, String email) {
        GameAccountReferral existingReferral = findBySignupEmail(email);
        if (existingReferral != null) {
            logger.underlyingLogger().info("GameAccountReferral already exists. email="+email+" promoCode="+promoCode);
        } else {
            GameAccountReferral referral = new GameAccountReferral();
            referral.setPromoCode(promoCode);
            referral.setSignupEmail(email);
            referral.setValidated(false);
            referral.setCreatedDate(new Date());
            referral.save();
        }
    }

    /**
     * @param promoCode
     * @param user
     */
    public static void processAnyReferral(String promoCode, User user) {
        // 1) search by user id (FB flow)
        GameAccountReferral existingReferral = findByUserId(user.id);
        // 2) search by user email (native flow)
        if (existingReferral == null && user.email != null) {
            existingReferral = findBySignupEmail(user.email);
        }

        String referrerPromoCode = null;

        if (existingReferral != null) {
            if (existingReferral.validated) {
            	logger.underlyingLogger().error("User already referred and validated: id="+user.id+" email="+user.email+" promoCode="+promoCode);
                //throw new IllegalStateException("User already referred and validated: id="+user.id+" email="+user.email+" promoCode="+promoCode);
            } else {
                // set validated (from native signup flow)
                existingReferral.setValidated(true);
                existingReferral.setUserId(user.getId());
                existingReferral.merge();
                referrerPromoCode = existingReferral.getPromoCode();
            }
        } else if (!StringUtils.isEmpty(promoCode)) {
            GameAccountReferral referral = new GameAccountReferral();
            referral.setValidated(true);
            referral.setPromoCode(promoCode);
            referral.setUserId(user.id);
            referral.setSignupEmail(user.email);
            referral.setCreatedDate(new Date());
            referral.save();
            referrerPromoCode = promoCode;
        }

        // 3) Credit referrer points
        if (referrerPromoCode != null) {
            GameAccount.setPointsForReferral(referrerPromoCode);
        }
    }

    ///////////////////// Find APIs /////////////////////
    public static List<User> findSignedUpUsersReferredBy(Long referrerId) {
        Query q = JPA.em().createQuery(
                "SELECT us FROM GameAccountReferral r, GameAccount g, User us "+
                "where g.user_id=?1 and g.promoCode=r.promoCode and r.validated=?2 and r.userId=us.id "+
                "and us.deleted = false order by us.id");
        q.setParameter(1, referrerId);
        q.setParameter(2, true);    // validated only
        return (List<User>) q.getResultList();
    }

    public static GameAccountReferral findByUserId(Long userId) {
	    try {
	        Query q = JPA.em().createQuery("SELECT r FROM GameAccountReferral r where userId = ?1");
	        q.setParameter(1, userId);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

    public static GameAccountReferral findBySignupEmail(String email) {
	    try {
	        Query q = JPA.em().createQuery("SELECT r FROM GameAccountReferral r where signupEmail = ?1");
	        q.setParameter(1, email);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
	}

    ///////////////////// Getters/Setters /////////////////////
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

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
