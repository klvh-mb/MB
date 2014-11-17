package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
	
    private String promoCode;

    private String signupEmail;

    /**
     * Ctor
     */
	public GameAccountReferral() {}


    /**
     * @param promoCode
     * @param signupEmail
     */
    public static void addReferralRecord(String promoCode, String signupEmail) {
        GameAccountReferral referral = new GameAccountReferral();
        referral.setPromoCode(promoCode);
        referral.setSignupEmail(signupEmail);
        referral.setCreatedDate(new Date());
        referral.save();
    }

    /**
     * @param email
     * @return
     */
    public static GameAccountReferral findBySignupEmail(String email) {
	    try {
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountReferral u where signupEmail = ?1");
	        q.setParameter(1, email);
	        return (GameAccountReferral) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
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
