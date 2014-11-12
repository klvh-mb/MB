package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.GamificationConstants;
import models.GameAccountTransaction.TransactionType;

import play.db.jpa.JPA;

@Entity
public class GameAccount extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccount.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long user_id;
	
	private Long game_points = 0L;      // total capped points redeemable
    private Long activity_points = 0L;  // total points from activity

    // Redemption
	public Long redeemed_points;
	public Date last_redemption_time;

    // Contact information
	public String address_1;
	public String address_2;
	public String city;
	public Long phone;
	
	public Boolean has_upload_profile_pic = false;
	
	public Long number_of_referral_signups = 0L;

    /**
     * Ctor
     */
	public GameAccount() {}


	public static GameAccount findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccount) q.getSingleResult();
	    } catch (NoResultException e) {
	    	GameAccount account = new GameAccount();
	    	account.user_id = id;
            account.setCreatedDate(new Date());
	    	account.save();
            logger.underlyingLogger().info("[u="+id+"] Gamification - Created new GameAccount");
	        return account;
	    } 
	}

    /**
     * Sign up.
     * @param user
     */
	public static void setPointsForSignUp(User user) {
        GameAccount account = GameAccount.findByUserId(user.id);

		account.addPointsAcross(GamificationConstants.POINTS_SIGNUP);
        account.auditFields.setUpdatedDate(new Date());
		account.save();
		GameAccountTransaction.recordPoints(user.id, GamificationConstants.POINTS_SIGNUP, TransactionType.SystemCredit, account.getGamePoints());

        checkForReferral(user.id);
	}

	private static void checkForReferral(Long id) {
		GameAccountReferral referal = GameAccountReferral.findByInviteUserId(id);
		if(referal == null)
			return;
		setPointsForReferalSignUp(referal.getSender_user_id());
	}

	private static void setPointsForReferalSignUp(Long senderUserId) {
		GameAccount account = GameAccount.findByUserId(senderUserId);

        if (account.number_of_referral_signups < GamificationConstants.LIMIT_REFERRAL_SIGNUP) {
		    account.addPointsAcross(GamificationConstants.POINTS_REFERRAL_SIGNUP);
            GameAccountTransaction.recordPoints(senderUserId, GamificationConstants.POINTS_REFERRAL_SIGNUP, TransactionType.SystemCredit, account.getGamePoints());
        }
        account.number_of_referral_signups++;
        account.auditFields.setUpdatedDate(new Date());
		account.merge();
	}

    /**
     * Profile picture upload.
     * @param user
     */
	public static void setPointsForPhotoProfile(User user) {
		GameAccount account = GameAccount.findByUserId(user.id);

        if (!account.has_upload_profile_pic) {
            logger.underlyingLogger().info("[u="+user.id+"] Gamification - Crediting profile picture upload");

            account.addPointsAcross(GamificationConstants.POINTS_UPLOAD_PROFILE_PHOTO);
            account.has_upload_profile_pic = true;
            account.auditFields.setUpdatedDate(new Date());
            account.merge();
            GameAccountTransaction.recordPoints(user.id, GamificationConstants.POINTS_UPLOAD_PROFILE_PHOTO, TransactionType.SystemCredit, account.getGamePoints());
        }
	}


    public void addPointsAcross(long newPoints) {
        game_points += newPoints;
        activity_points += newPoints;
    }

    public void addPointsActivityOnly(long newPoints) {
        activity_points += newPoints;
    }

    public void addPointsGameOnly(long newPoints) {
        game_points += newPoints;
    }

    public Long getGamePoints() {
        return game_points;
    }

    public Long getActivityPoints() {
        return activity_points;
    }
}
