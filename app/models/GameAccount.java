package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import common.utils.ShortCodeGenerator;
import domain.GamificationConstants;
import email.EDMUtility;
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

    public String promoCode;
	public Long number_of_referral_signups = 0L;

    /**
     * Ctor
     */
	public GameAccount() {}

    /**
     * Look up. Create if not exists.
     * @param id
     * @return
     */
	public static GameAccount findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccount) q.getSingleResult();
	    } catch (NoResultException e) {
	        return createNewAccount(id);
	    } 
	}

    private static GameAccount createNewAccount(Long userId) {
        boolean uniquePromoCodeFound = false;
        String tmpPromoCode = null;

        while (!uniquePromoCodeFound) {
            tmpPromoCode = ShortCodeGenerator.genPromoCode();
            try {
                Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where promoCode = ?1");
                q.setParameter(1, tmpPromoCode);
                q.getSingleResult();
            } catch (NoResultException e) {
                uniquePromoCodeFound = true;
            }
        }

        GameAccount account = new GameAccount();
        account.user_id = userId;
        account.promoCode = tmpPromoCode;
        account.setCreatedDate(new Date());
        account.save();

        logger.underlyingLogger().info("[u="+userId+"] Gamification - New GameAccount. promoCode="+tmpPromoCode);
        return account;
    }

    /**
     * @param promoCode
     * @return
     */
    public static GameAccount findByPromoCode(String promoCode) {
	    try {
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where promoCode = ?1");
	        q.setParameter(1, promoCode);
	        return (GameAccount) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    }
    }

    /**
     * Sign up.
     * @param user
     */
	public static void setPointsForSignUp(User user, String promoCode) {
        GameAccount account = GameAccount.findByUserId(user.id);

		account.addPointsAcross(GamificationConstants.POINTS_SIGNUP);
        account.auditFields.setUpdatedDate(new Date());
		account.save();
		GameAccountTransaction.recordPoints(user.id,
                GamificationConstants.POINTS_SIGNUP,
                TransactionType.SystemCredit,
                GameAccountTransaction.TRANS_DESC_SIGNUP,
                account.getGamePoints());

        logger.underlyingLogger().info("[u="+user.id+"] Gamification - Credited signup");

        accountForSignupReferral(user.email, promoCode);
	}

    // check if this signup came from a referral.
	private static void accountForSignupReferral(String email, String promoCode) {
        GameAccount referrerAccount = null;

        if (promoCode != null) {
            referrerAccount = findByPromoCode(promoCode);
        }
        else {
            GameAccountReferral referral = GameAccountReferral.findBySignupEmail(email);
            if (referral != null) {
                referrerAccount = findByPromoCode(referral.getPromoCode());
            }
        }

        if (referrerAccount != null) {
            if (referrerAccount.number_of_referral_signups < GamificationConstants.LIMIT_REFERRAL_SIGNUP) {
                long referrerId = referrerAccount.user_id;
                long numReferralsNow = referrerAccount.number_of_referral_signups + 1;

                referrerAccount.addPointsAcross(GamificationConstants.POINTS_REFERRAL_SIGNUP);
                GameAccountTransaction.recordPoints(referrerId,
                        GamificationConstants.POINTS_REFERRAL_SIGNUP,
                        TransactionType.SystemCredit,
                        GameAccountTransaction.TRANS_DESC_REFERRAL,
                        referrerAccount.getGamePoints());

                logger.underlyingLogger().info("[u="+referrerId+"] Gamification - Credited referral. Num referrals = "+numReferralsNow);
            }
            referrerAccount.number_of_referral_signups++;
            referrerAccount.auditFields.setUpdatedDate(new Date());
            referrerAccount.merge();
        }
    }

    /**
     * Signin.
     * @param user
     */
	public static void setPointsForSignin(User user) {
        logger.underlyingLogger().info("[u="+user.id+"] Gamification - Crediting signin");

        GameAccount account = GameAccount.findByUserId(user.id);
        account.addPointsAcross(GamificationConstants.POINTS_SIGNIN);
        account.auditFields.setUpdatedDate(new Date());
        account.merge();

        GameAccountTransaction.recordPoints(user.id,
                GamificationConstants.POINTS_SIGNIN,
                TransactionType.SystemCredit,
                GameAccountTransaction.TRANS_DESC_SIGNIN,
                account.getGamePoints());
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
            GameAccountTransaction.recordPoints(user.id,
                    GamificationConstants.POINTS_UPLOAD_PROFILE_PHOTO,
                    TransactionType.SystemCredit,
                    GameAccountTransaction.TRANS_DESC_PROFILEPIC,
                    account.getGamePoints());
        }
	}

    /**
     * @param email
     */
    public void sendInvitation(String email) {
		EDMUtility edmUtility = new EDMUtility();
		edmUtility.sendMailToUser(email, this.promoCode);

        logger.underlyingLogger().info("[u="+user_id+"] Promocode="+promoCode+". Sent signup invitation to: "+email);
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
