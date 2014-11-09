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

    public static enum Transaction_type{
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
	
	public Transaction_type transaction_type;

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

	 
	public static void recordPoints(long userID, long transactedPoints, Transaction_type type, long newTotalPoints) {
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
                    long pointsPostCredit = numPostCredit * GamificationConstants.POINTS_POST;
                    long pointsCommentCredit = numCommentCredit * GamificationConstants.POINTS_COMMENT;
                    long pointsLikeCredit = numLikeCredit * GamificationConstants.POINTS_LIKE;
                    long totalCredit = 0;

                    if (pointsPostCredit > 0) {
                        totalCredit += pointsPostCredit;
                        account.total_points += pointsPostCredit;
                        GameAccountTransaction.recordPoints(stat.user_id, pointsPostCredit, Transaction_type.SystemCredit, account.total_points);
                    }
                    if (pointsCommentCredit > 0) {
                        totalCredit += pointsCommentCredit;
                        account.total_points += pointsCommentCredit;
                        GameAccountTransaction.recordPoints(stat.user_id, pointsCommentCredit, Transaction_type.SystemCredit, account.total_points);
                    }
                    if (pointsLikeCredit > 0) {
                        totalCredit += pointsLikeCredit;
                        account.total_points += pointsLikeCredit;
                        GameAccountTransaction.recordPoints(stat.user_id, pointsLikeCredit, Transaction_type.SystemCredit, account.total_points);
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
