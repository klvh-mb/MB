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
import org.joda.time.LocalDate;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class GameAccount extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccount.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Required
	public Long user_id;

	private Long game_points = 0L;      // total capped points redeemable
    private Long activity_points = 0L;  // total points from activity

    // Points multiplier
    private Double firstPersonMultiplier = 1d;

    // Redemption
	public Long redeemed_points = 0L;
	public Date last_redemption_time;

    // Contact information
    public String realName;
	public String phone;
    public String email;
    public String address_1;
	public String address_2;
	public String city;

	public Boolean has_upload_profile_pic = false;
	public Boolean app_login = false;

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
	public static void setPointsForSignUp(User user) {
        GameAccount account = GameAccount.findByUserId(user.id);

		long addedPoints = account.addPointsAcross(GamificationConstants.POINTS_SIGNUP);
        account.auditFields.setUpdatedDate(new Date());
		account.save();

        if (addedPoints > 0) {
            GameAccountTransaction.recordPoints(user.id,
                    GamificationConstants.POINTS_SIGNUP,
                    TransactionType.SystemCredit,
                    GameAccountTransaction.TRANS_DESC_SIGNUP,
                    account.getGamePoints());
            logger.underlyingLogger().info("[u="+user.id+"] Gamification - Credited signup");
        } else {
            GameAccountTransaction.recordPoints(user.id,
                    0, TransactionType.SystemCredit,
                    GameAccountTransaction.TRANS_DESC_SIGNUP+GameAccountTransaction.TRANS_DESC_DAILYMAX_REACHED,
                    account.getGamePoints());
            logger.underlyingLogger().info("[u="+user.id+"] Gamification - NOT Credited signup. Daily max reached.");
        }
	}

    /**
     * Referral.
     * @param referrerPromoCode
     */
	public static void setPointsForReferral(String referrerPromoCode, boolean creditReferral) {
        GameAccount referrerAccount = GameAccount.findByPromoCode(referrerPromoCode);
        if (referrerAccount != null) {
            if (referrerAccount.number_of_referral_signups < GamificationConstants.LIMIT_REFERRAL_SIGNUP) {
                long referrerId = referrerAccount.user_id;

                int points;
                String desc;
                if (creditReferral) {
                    referrerAccount.number_of_referral_signups++;

                    long addedPoints = referrerAccount.addPointsAcross(GamificationConstants.POINTS_REFERRAL_SIGNUP);
                    if (addedPoints > 0) {
                        points = GamificationConstants.POINTS_REFERRAL_SIGNUP;
                        desc = GameAccountTransaction.TRANS_DESC_REFERRAL;
                        logger.underlyingLogger().info("[u="+referrerId+"] Gamification - Credited referral. Num referrals="+referrerAccount.number_of_referral_signups);
                    } else {
                        points = 0;
                        desc = GameAccountTransaction.TRANS_DESC_REFERRAL+GameAccountTransaction.TRANS_DESC_DAILYMAX_REACHED;
                        logger.underlyingLogger().info("[u="+referrerId+"] Gamification - NOT Credited Referral. Daily max reached.");
                    }
                } else {
                    points = 0;
                    desc = GameAccountTransaction.TRANS_DESC_REFERRAL_EMAIL;
                    logger.underlyingLogger().info("[u="+referrerId+"] Gamification - NOT Credited Referral. Email signup");
                }

                GameAccountTransaction.recordPoints(referrerId,
                        points, TransactionType.SystemCredit, desc,
                        referrerAccount.getGamePoints());
            }
            referrerAccount.auditFields.setUpdatedDate(new Date());
            referrerAccount.merge();
        }
        else {
            logger.underlyingLogger().error("Error Gamification. No GameAccount found from "+referrerPromoCode);
        }
    }

    /**
     * Signin.
     * @param user
     */
	public static void setPointsForSignin(User user) {
        GameAccount account = GameAccount.findByUserId(user.id);
        long addedPoints = account.addPointsAcross(GamificationConstants.POINTS_DAILY_SIGNIN);
        account.auditFields.setUpdatedDate(new Date());
        account.merge();

        if (addedPoints > 0) {
            GameAccountTransaction.recordPoints(user.id,
                    GamificationConstants.POINTS_DAILY_SIGNIN,
                    TransactionType.SystemCredit,
                    GameAccountTransaction.TRANS_DESC_DAILY_SIGNIN,
                    account.getGamePoints());
            logger.underlyingLogger().info("[u="+user.id+"] Gamification - Credited Signin.");
        } else {
            GameAccountTransaction.recordPoints(user.id,
                    0, TransactionType.SystemCredit,
                    GameAccountTransaction.TRANS_DESC_DAILY_SIGNIN+GameAccountTransaction.TRANS_DESC_DAILYMAX_REACHED,
                    account.getGamePoints());
            logger.underlyingLogger().info("[u="+user.id+"] Gamification - NOT Credited Signin. Daily max reached.");
        }
	}

    /**
     * Profile picture upload.
     * @param user
     */
	public static void setPointsForPhotoProfile(User user) {
		GameAccount account = GameAccount.findByUserId(user.id);

        if (!account.has_upload_profile_pic) {
            long addedPoints = account.addPointsAcross(GamificationConstants.POINTS_UPLOAD_PROFILE_PHOTO);
            account.has_upload_profile_pic = true;
            account.auditFields.setUpdatedDate(new Date());
            account.merge();

            if (addedPoints > 0) {
                GameAccountTransaction.recordPoints(user.id,
                        GamificationConstants.POINTS_UPLOAD_PROFILE_PHOTO,
                        TransactionType.SystemCredit,
                        GameAccountTransaction.TRANS_DESC_PROFILEPIC,
                        account.getGamePoints());
                logger.underlyingLogger().info("[u="+user.id+"] Gamification - Credited profile picture upload");
            } else {
                GameAccountTransaction.recordPoints(user.id,
                        0, TransactionType.SystemCredit,
                        GameAccountTransaction.TRANS_DESC_PROFILEPIC+GameAccountTransaction.TRANS_DESC_DAILYMAX_REACHED,
                        account.getGamePoints());
                logger.underlyingLogger().info("[u="+user.id+"] Gamification - NOT Credited profile picture upload. Daily max reached.");
            }
        }
	}
	
	/**
     * App login.
     * @param user
     */
	public static void setPointsForAppLogin(User user) {
		GameAccount account = GameAccount.findByUserId(user.id);

        if (!account.app_login) {
            long addedPoints = account.addPointsAcross(GamificationConstants.POINTS_APP_LOGIN);
            account.app_login = true;
            account.auditFields.setUpdatedDate(new Date());
            account.merge();

            if (addedPoints > 0) {
                GameAccountTransaction.recordPoints(user.id,
                        GamificationConstants.POINTS_APP_LOGIN,
                        TransactionType.SystemCredit,
                        GameAccountTransaction.TRANS_DESC_APP_LOGIN,
                        account.getGamePoints());
                logger.underlyingLogger().info("[u="+user.id+"] Gamification - Credited app login");
            } else {
                GameAccountTransaction.recordPoints(user.id,
                        0, TransactionType.SystemCredit,
                        GameAccountTransaction.TRANS_DESC_APP_LOGIN+GameAccountTransaction.TRANS_DESC_DAILYMAX_REACHED,
                        account.getGamePoints());
                logger.underlyingLogger().info("[u="+user.id+"] Gamification - NOT Credited app login. Daily max reached.");
            }
        }
	}
	
	/**
     * Redeem game gift.
     * @param user
     */
	public static void redeemGameGift(User user, GameGift gameGift) {
		GameAccount account = GameAccount.findByUserId(user.id);

        logger.underlyingLogger().info("[u="+user.id+"] Gamification - Redeem game gift");

        account.redeemPoints(gameGift.requiredPoints);
        account.auditFields.setUpdatedDate(new Date());
        account.merge();
        GameAccountTransaction.recordPoints(user.id,
        		gameGift.requiredPoints,
                TransactionType.Redeem,
                GameAccountTransaction.TRANS_DESC_REDEEM + gameGift.name,
                account.getGamePoints());
	}

	/**
     * Adjust points.
     * @param user
     */
	public static void adjustPoints(User user, long points) {
		GameAccount account = GameAccount.findByUserId(user.id);

        logger.underlyingLogger().info("[u="+user.id+"] Gamification - Adjust points="+points);

        account.addPointsAcross(points);
        account.auditFields.setUpdatedDate(new Date());
        account.merge();
        GameAccountTransaction.recordPoints(user.id,
                points,
                TransactionType.Adjustment,
                GameAccountTransaction.TRANS_DESC_ADJUSTMENT,
                account.getGamePoints());
	}
	
    /**
     * @param email
     */
    public void sendInvitation(String email) {
		EDMUtility edmUtility = new EDMUtility();
		edmUtility.sendMailInvitationToUser(email, this.promoCode);

        logger.underlyingLogger().info("[u="+user_id+"] Promocode="+promoCode+". Sent signup invitation to: "+email);
    }

    public long addPointsAcross(long newPoints) {
        int ptsToday = GameAccountTransaction.getTransactionPointsForDay(this.user_id, new LocalDate());

        if (ptsToday < GamificationConstants.LIMIT_DAILY_POINTS) {
            game_points += newPoints;
            activity_points += newPoints;
            return newPoints;
        } else {
            return 0;
        }
    }

    public void redeemPoints(long points) {
        game_points -= points;
        redeemed_points += points;
    }
    
    public Long getGamePoints() {
        return game_points;
    }

    public Long getActivityPoints() {
        return activity_points;
    }

    public Long getRedeemedPoints() {
    	return redeemed_points;
    }
    
    public Double getFirstPersonMultiplier() {
        return (firstPersonMultiplier == null) ? 1d : firstPersonMultiplier;
    }

    public void setContactInfo(String realName, String phone, String email) {
    	if (realName != null)
    		this.realName = realName;
    	if (phone != null)
    		this.phone = phone;
    	if (email != null)
    		this.email = email;
    }
}
