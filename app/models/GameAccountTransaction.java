package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class GameAccountTransaction  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountTransaction.class);

    public static final String TRANS_DESC_SIGNUP = "新會員奬賞";
    public static final String TRANS_DESC_REFERRAL = "介紹新會員以FB加入";
    public static final String TRANS_DESC_REFERRAL_EMAIL = "介紹新會員以電郵加入";
    public static final String TRANS_DESC_PROFILEPIC = "上載個人頭像照片";
    public static final String TRANS_DESC_DAILY_SIGNIN = "每日簽到";
    public static final String TRANS_DESC_POSTS = "發佈新話題";
    public static final String TRANS_DESC_COMMENTS = "回覆話題";
    public static final String TRANS_DESC_LIKES = "讚好";
    public static final String TRANS_DESC_APP_LOGIN = "首次APP登入";
    public static final String TRANS_DESC_REDEEM = "換領";
    public static final String TRANS_DESC_ADJUSTMENT = "調整帳戶";
    public static final String TRANS_DESC_DAILYMAX_REACHED = "(已超出每日累積上限)";


    public static enum TransactionType {
		SystemCredit,
		Redeem,
		Bonus,
		Penalty,
		Adjustment
	}
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Required
	public Long userId;
	
	@Required
	public Long transactedPoints;
	
	@Required
	public Date transactedTime;
	
	@Required
	public Long newTotalPoints;
	
	@Required
	public TransactionType transactionType;

	@Required
    public String transactionDescription;

    @Required
    public Boolean deleted = false;
    
    /**
     * Ctor
     */
	public GameAccountTransaction() {}

    @Transactional(readOnly = true)
	public static int getTransactionPointsForDay(Long userId, LocalDate date) {
        DateTime start = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 0, 0, 0);
        DateTime end = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 23, 59, 59);

        Query q = JPA.em().createQuery(
                "SELECT transactedPoints FROM GameAccountTransaction where userId=?1 and transactedTime >= ?2 and transactedTime <= ?3 and deleted = false");
        q.setParameter(1, userId);
        q.setParameter(2, start.toDate());
        q.setParameter(3, end.toDate());

        List<Long> points = (List<Long>) q.getResultList();
        int dateTotal = 0;
        for (Long pt : points) {
            dateTotal += pt.intValue();
        }
        return dateTotal;
	}

    @Transactional(readOnly = true)
	public static List<GameAccountTransaction> getTransactions(Long userId, int offset, int pageSize) {
        Query q = JPA.em().createQuery("SELECT u FROM GameAccountTransaction u where userId = ?1 and deleted = false order by transactedTime desc");
        q.setParameter(1, userId);

        q.setFirstResult(offset);
        q.setMaxResults(pageSize);
        return (List<GameAccountTransaction>) q.getResultList();
	}
    
    @Transactional(readOnly = true)
	public static List<GameAccountTransaction> getLatestTransactions(int pageSize) {
        Query q = JPA.em().createQuery("SELECT u FROM GameAccountTransaction u where deleted = false order by transactedTime desc");
        q.setMaxResults(pageSize);
        return (List<GameAccountTransaction>) q.getResultList();
	}

    /**
     * @param userId
     * @param transactedPoints
     * @param type
     * @param desc
     * @param newTotalPoints
     */
    @Transactional
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
//    @Transactional
//	public static void performEndOfDayTasks(Integer daysBefore) {
//        final int numDaysBefore = (daysBefore == null) ? 1 : daysBefore;
//
//        // EOD accounting
//        List<GameAccountStatistics> stats = GameAccountStatistics.getPendingStatisticsWithActivity(numDaysBefore);
//        logger.underlyingLogger().info("Gamification - Begin EOD accounting (daysBefore="+numDaysBefore+") on pending accounts: "+stats.size());
//
//        for (GameAccountStatistics stat : stats) {
//            GameAccount account = GameAccount.findByUserId(stat.user_id);
//            if (account != null) {
//                double firstPersonMulti = account.getFirstPersonMultiplier();
//
//                long numPostCredit = Math.min(stat.num_new_posts, GamificationConstants.LIMIT_POST);
//                long numCommentCredit = Math.min(stat.num_new_comments, GamificationConstants.LIMIT_COMMENT);
//                long numLikeCredit = Math.min(stat.num_likes, GamificationConstants.LIMIT_LIKE);
//
//                boolean toCredit = (numPostCredit + numCommentCredit + numLikeCredit) > 0;
//
//                if (toCredit) {
//                    long totalCredit = 0;
//                    long pointsPostCredit = (long)(numPostCredit * GamificationConstants.POINTS_POST * firstPersonMulti);
//                    long pointsCommentCredit = (long)(numCommentCredit * GamificationConstants.POINTS_COMMENT * firstPersonMulti);
//                    long pointsLikeCredit = numLikeCredit * GamificationConstants.POINTS_LIKE;
//
//                    long pointsPostActivity = stat.num_new_posts * GamificationConstants.POINTS_POST;
//                    long pointsCommentActivity = stat.num_new_comments * GamificationConstants.POINTS_COMMENT;
//                    long pointsLikeActivity = stat.num_likes * GamificationConstants.POINTS_LIKE;
//
//                    if (pointsPostCredit > 0) {
//                        totalCredit += pointsPostCredit;
//                        account.addPointsGameOnly(pointsPostCredit);
//                        account.addPointsActivityOnly(pointsPostActivity);
//                        GameAccountTransaction.recordPoints(stat.user_id, pointsPostCredit,
//                                TransactionType.SystemCredit, GameAccountTransaction.TRANS_DESC_POSTS, account.getGamePoints());
//                    }
//                    if (pointsCommentCredit > 0) {
//                        totalCredit += pointsCommentCredit;
//                        account.addPointsGameOnly(pointsCommentCredit);
//                        account.addPointsActivityOnly(pointsCommentActivity);
//                        GameAccountTransaction.recordPoints(stat.user_id, pointsCommentCredit,
//                                TransactionType.SystemCredit, GameAccountTransaction.TRANS_DESC_COMMENTS, account.getGamePoints());
//                    }
//                    if (pointsLikeCredit > 0) {
//                        totalCredit += pointsLikeCredit;
//                        account.addPointsGameOnly(pointsLikeCredit);
//                        account.addPointsActivityOnly(pointsLikeActivity);
//                        GameAccountTransaction.recordPoints(stat.user_id, pointsLikeCredit,
//                                TransactionType.SystemCredit, GameAccountTransaction.TRANS_DESC_LIKES, account.getGamePoints());
//                    }
//                    account.auditFields.setUpdatedDate(new Date());
//                    account.merge();
//
//                    logger.underlyingLogger().info("[u="+stat.user_id+"] Gamification - Total points credited: "+totalCredit);
//                }
//                else {
//                    logger.underlyingLogger().info("[u="+stat.user_id+"] Gamification - Nothing to credit for");
//                }
//            }
//            else {
//                logger.underlyingLogger().error("[u="+stat.user_id+"] Gamification - Corrupted State. Missing GameAccount");
//            }
//
//            stat.accounted_for = true;
//            stat.save();
//        }
//
//        logger.underlyingLogger().info("Gamification - Done EOD accounting (daysBefore="+numDaysBefore+") on pending accounts: "+stats.size());
//
//        // purge old entries
//		GameAccountStatistics.purge();
//	}
}
