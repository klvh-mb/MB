package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import play.data.format.Formats;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.DefaultValues;
import domain.Likeable;
import domain.SocialObjectType;

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

	 
	public static void recordPoints(long userID,GameAccount account, int points, Transaction_type type) {
		GameAccountTransaction transaction = new GameAccountTransaction();
		transaction.user_id = userID;
		transaction.new_Total_Points = account.total_points;
		transaction.transacted_time = account.getUpdatedDate();
		transaction.transacted_points = (long) points;
		transaction.transaction_type = type;
		transaction.save();
	}

	public static void recordPointsAtEndOfDay() {
		GameAccount.setPointsForPost();
		GameAccount.setPointsForComment();
		GameAccount.setPointsForLike();
		GameAccount.purging();
	}

}
