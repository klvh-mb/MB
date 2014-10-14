package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import play.db.jpa.JPA;

@Entity
public class GameAccountStatistics  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountStatistics.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long user_id;
	
	public Date activity_date;
	
	public Long num_sign_in = 0L;
	
	public Long num_new_posts = 0L;	
	
	public Long num_new_comments = 0L;	
	
	public Long num_likes = 0L;	
	 
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

	private static GameAccountStatistics getGameAccountStatistics(long userID) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date today = null;
		try {
			today = dateFormat.parse(dateFormat.format(new Date()));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
		statistics.num_likes ++;
		statistics.merge();
	}
	
	public static void recordunLike(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		if(statistics.num_likes > 0)
		statistics.num_likes --;
		statistics.merge();
	}

	public static void recordPost(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_new_posts ++;
		statistics.merge();
		
	}
	
	public static void recordComment(long userID) {
		GameAccountStatistics statistics = GameAccountStatistics.getGameAccountStatistics(userID);
		statistics.num_new_comments ++;
		statistics.merge();
		GameAccountTransaction.recordPointsAtEndOfDay();
		
	}

	public static List<Long> getUsersForPostPoints() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date today = null;
		try {
			today = dateFormat.parse(dateFormat.format(new Date().getTime()-24*60*60*1000));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try { 
	        Query q = JPA.em().createQuery("SELECT u.user_id FROM GameAccountStatistics u where activity_date = ?1 and num_new_posts > ?2");
	        q.setParameter(1, today);
	        q.setParameter(2, 4L);
	        List<Long> list = q.getResultList();
	        return list;
	    } catch (NoResultException e) {
	    	return null;
	    }
	}
	
	public static List<Long> getUsersForCommentPoints() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date today = null;
		try {
			today = dateFormat.parse(dateFormat.format(new Date().getTime()-24*60*60*1000));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try { 
	        Query q = JPA.em().createQuery("SELECT u.user_id FROM GameAccountStatistics u where activity_date =?1 and num_new_comments > ?2");
	        q.setParameter(1, today);
	        q.setParameter(2, 4L);
	        List<Long> list = q.getResultList();
	        return list;
	    } catch (NoResultException e) {
	    	return null;
	    }
	}
	
	public static List<Long> getUsersForLikePoints() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date today = null;
		try {
			today = dateFormat.parse(dateFormat.format(new Date().getTime()-24*60*60*1000));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try { 
	        Query q = JPA.em().createQuery("SELECT u.user_id FROM GameAccountStatistics u where activity_date =?1 and num_likes > ?2");
	        q.setParameter(1, today);
	        q.setParameter(2, 4L);
	        List<Long> list = q.getResultList();
	        return list;
	    } catch (NoResultException e) {
	    	return null;
	    }
	}

	 

}
