package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import domain.GamificationConstants;
import play.db.jpa.JPA;

@Entity
public class GameAccountTransaction  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountTransaction.class);

    public static final String TRANS_DESC_SIGNUP = "miniBean account signup";
    public static final String TRANS_DESC_REFERRAL = "miniBean signup referral";
    public static final String TRANS_DESC_PROFILEPIC = "Profile picture upload";
    public static final String TRANS_DESC_POSTS = "New posts";
    public static final String TRANS_DESC_COMMENTS = "Post comments";
    public static final String TRANS_DESC_LIKES = "Likes";

    public static enum TransactionType {
		SystemCredit,
		Redemption,
		Bonus,
		Penalty,
		End_of_day
	}
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long userId;
	
	public Long transactedPoints;
	 
	public Date transactedTime;
	
	public Long newTotalPoints;
	
	public TransactionType transactionType;

    public String transactionDescription;

    /**
     * Ctor
     */
	public GameAccountTransaction() {}


	public static GameAccountTransaction findByUserId(Long userId) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountTransaction u where userId = ?1");
	        q.setParameter(1, userId);
	        return (GameAccountTransaction) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

	 
	public static void recordPoints(long userId, long transactedPoints, TransactionType type, String desc,
                                    long newTotalPoints) {
		GameAccountTransaction transaction = new GameAccountTransaction();
		transaction.userId = userId;
		transaction.transactedTime = new Date();
		transaction.transactedPoints = transactedPoints;
		transaction.transactionType = type;
        transaction.transactionDescription = desc;
        transaction.newTotalPoints = newTotalPoints;
		transaction.save();
	}

    /**
     * EOD Tasks
     */
	public static void performEndOfDayTasks(Integer daysBefore) {
        final int numDaysBefore = (daysBefore == null) ? 1 : daysBefore;

        // EOD accounting
        List<GameAccountStatistics> stats = GameAccountStatistics.getPendingStatisticsWithActivity(numDaysBefore);
        logger.underlyingLogger().info("Gamification - Begin EOD accounting (daysBefore="+numDaysBefore+") on pending accounts: "+stats.size());

        for (GameAccountStatistics stat : stats) {
            GameAccount account = GameAccount.findByUserId(stat.user_id);
            if (account != null) {
                long numPostCredit = Math.min(stat.num_new_posts, GamificationConstants.LIMIT_POST);
                long numCommentCredit = Math.min(stat.num_new_comments, GamificationConstants.LIMIT_COMMENT);
                long numLikeCredit = Math.min(stat.num_likes, GamificationConstants.LIMIT_LIKE);

                boolean toCredit = (numPostCredit + numCommentCredit + numLikeCredit) > 0;

                if (toCredit) {
                    long totalCredit = 0;
                    long pointsPostCredit = numPostCredit * GamificationConstants.POINTS_POST;
                    long pointsCommentCredit = numCommentCredit * GamificationConstants.POINTS_COMMENT;
                    long pointsLikeCredit = numLikeCredit * GamificationConstants.POINTS_LIKE;

                    long pointsPostActivity = stat.num_new_posts * GamificationConstants.POINTS_POST;
                    long pointsCommentActivity = stat.num_new_comments * GamificationConstants.POINTS_COMMENT;
                    long pointsLikeActivity = stat.num_likes * GamificationConstants.POINTS_LIKE;

                    if (pointsPostCredit > 0) {
                        totalCredit += pointsPostCredit;
                        account.addPointsGameOnly(pointsPostCredit);
                        account.addPointsActivityOnly(pointsPostActivity);
                        GameAccountTransaction.recordPoints(stat.user_id, pointsPostCredit,
                                TransactionType.SystemCredit, GameAccountTransaction.TRANS_DESC_POSTS, account.getGamePoints());
                    }
                    if (pointsCommentCredit > 0) {
                        totalCredit += pointsCommentCredit;
                        account.addPointsGameOnly(pointsCommentCredit);
                        account.addPointsActivityOnly(pointsCommentActivity);
                        GameAccountTransaction.recordPoints(stat.user_id, pointsCommentCredit,
                                TransactionType.SystemCredit, GameAccountTransaction.TRANS_DESC_COMMENTS, account.getGamePoints());
                    }
                    if (pointsLikeCredit > 0) {
                        totalCredit += pointsLikeCredit;
                        account.addPointsGameOnly(pointsLikeCredit);
                        account.addPointsActivityOnly(pointsLikeActivity);
                        GameAccountTransaction.recordPoints(stat.user_id, pointsLikeCredit,
                                TransactionType.SystemCredit, GameAccountTransaction.TRANS_DESC_LIKES, account.getGamePoints());
                    }
                    account.auditFields.setUpdatedDate(new Date());
                    account.merge();

                    logger.underlyingLogger().info("[u="+stat.user_id+"] Gamification - Total points credited: "+totalCredit);
                }
                else {
                    logger.underlyingLogger().info("[u="+stat.user_id+"] Gamification - Nothing to credit for");
                }
            }
            else {
                logger.underlyingLogger().error("[u="+stat.user_id+"] Gamification - Corrupted State. Missing GameAccount");
            }

            stat.accounted_for = true;
            stat.save();
        }

        logger.underlyingLogger().info("Gamification - Done EOD accounting (daysBefore="+numDaysBefore+") on pending accounts: "+stats.size());

        // purge old entries
		GameAccountStatistics.purge();
	}
}
