package models;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.elasticsearch.common.joda.time.LocalDate;
import play.db.jpa.JPA;

@Entity
public class GameAccountStatistics  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountStatistics.class);

    private static final int NUM_DAYS_TO_KEEP = 30;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long user_id;
	
	public Date activity_date;
	
	public Long num_sign_in = 0L;
	
	public Long num_new_posts = 0L;	
	
	public Long num_new_comments = 0L;	
	
	public Long num_likes = 0L;

    public boolean accounted_for = false;

    /**
     * Ctor
     */
	public GameAccountStatistics() {}
	
	public static GameAccountStatistics findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountStatistics u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccountStatistics) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

    // Get, create if not found
	private static GameAccountStatistics getGameAccountStatistics(long userID) {
        Date today = (new LocalDate()).toDate();        // today without time
		try {
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountStatistics u where user_id = ?1 and activity_date =?2");
	        q.setParameter(1, userID);
	        q.setParameter(2, today);
	        return (GameAccountStatistics) q.getSingleResult();
	    } catch (NoResultException e) {
	    	GameAccountStatistics statistics = new GameAccountStatistics();
	    	statistics.user_id = userID;
	    	statistics.activity_date = today;
	    	statistics.save();
	    	return statistics;
	    } 
	}
	
	public static void recordLike(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_likes++;
		statistics.merge();
	}
	
	public static void recordunLike(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		if(statistics.num_likes > 0) {
		    statistics.num_likes--;
            statistics.merge();
        }
	}

	public static void recordPost(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_new_posts++;
		statistics.merge();
	}
	
	public static void recordDeletePost(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_new_posts--;
		statistics.merge();
	}
	
	public static void recordComment(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_new_comments++;
		statistics.merge();
	}
	
	public static void recordDeleteComment(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_new_comments--;
		statistics.merge();
	}

    /**
     * @param numDaysBefore
     * @return
     */
    public static List<GameAccountStatistics> getPendingStatisticsWithActivity(int numDaysBefore) {
        Date eodDate = (new LocalDate()).minusDays(numDaysBefore).toDate();
		try {
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountStatistics u where u.activity_date = ?1 and u.accounted_for = false");
	        q.setParameter(1, eodDate);
	        return (List<GameAccountStatistics>) q.getResultList();
	    } catch (NoResultException e) {
	    	return Collections.EMPTY_LIST;
	    }
    }

    /**
     * Purge
     */
    public static void purge() {
        Date purgeDate = (new LocalDate()).minusDays(NUM_DAYS_TO_KEEP).toDate();
        logger.underlyingLogger().info("Purging GameAccountStatistics before "+purgeDate);

        Query q = JPA.em().createQuery("DELETE FROM GameAccountStatistics where activity_date < ?1");
        q.setParameter(1, purgeDate);
        q.executeUpdate();
	}
}
