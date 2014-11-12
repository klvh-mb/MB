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
	
	public Long user_id;
	
	public Long transacted_points;	
	 
	public Date transacted_time;
	
	public Long new_Total_Points;
	
	public TransactionType transaction_type;

    /**
     * Ctor
     */
	public GameAccountTransaction() {}


	public static GameAccountTransaction findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccountTransaction) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}
	
	public static GameAccountTransaction findById(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where id = ?1");
	        q.setParameter(1, id);
	        return (GameAccountTransaction) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

	 
	public static void recordPoints(long userID, long transactedPoints, TransactionType type, long newTotalPoints) {
		GameAccountTransaction transaction = new GameAccountTransaction();
		transaction.user_id = userID;
		transaction.transacted_time = new Date();
		transaction.transacted_points = transactedPoints;
		transaction.transaction_type = type;
        transaction.new_Total_Points = newTotalPoints;
		transaction.save();
	}

    /**
     * EOD Tasks
     */
	public static void performEndOfDayTasks() {
        final int numDaysBefore = 2;

        // EOD accounting
        List<GameAccountStatistics> stats = GameAccountStatistics.getPendingStatisticsWithActivity(numDaysBefore);
        logger.underlyingLogger().info("Gamification - Begin EOD accounting on pending accounts: "+stats.size());

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
                        GameAccountTransaction.recordPoints(stat.user_id, pointsPostCredit, TransactionType.SystemCredit, account.getGamePoints());
                    }
                    if (pointsCommentCredit > 0) {
                        totalCredit += pointsCommentCredit;
                        account.addPointsGameOnly(pointsCommentCredit);
                        account.addPointsActivityOnly(pointsCommentActivity);
                        GameAccountTransaction.recordPoints(stat.user_id, pointsCommentCredit, TransactionType.SystemCredit, account.getGamePoints());
                    }
                    if (pointsLikeCredit > 0) {
                        totalCredit += pointsLikeCredit;
                        account.addPointsGameOnly(pointsLikeCredit);
                        account.addPointsActivityOnly(pointsLikeActivity);
                        GameAccountTransaction.recordPoints(stat.user_id, pointsLikeCredit, TransactionType.SystemCredit, account.getGamePoints());
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

        logger.underlyingLogger().info("Gamification - Done EOD accounting on pending accounts: "+stats.size());

        // purge old entries
		GameAccountStatistics.purge();
	}
}
