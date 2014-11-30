package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import email.EDMUtility;
import play.db.jpa.JPA;

@Entity
public class GameRedemption  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameRedemption.class);
    
    public static enum Redemption_state{
    	InProgress,
    	Delivered
    }
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long user_id;
	
	public Long redemption_points;
	
	public Date date;
	
	public Redemption_state redemption_state; 
	
	public GameRedemption() {}
	
	public static GameRedemption findById(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameRedemption u where transaction_id = ?1");
	        q.setParameter(1, id);
	        return (GameRedemption) q.getSingleResult();
	    } catch (NoResultException e) {
	    	GameRedemption account = new GameRedemption();
	    	account.save();
	        return account;
	    } 
	}

    public static void requestToRedemption(User user, Long points) {
		GameRedemption redemption = new GameRedemption();
		redemption.redemption_state = GameRedemption.Redemption_state.InProgress;
		redemption.redemption_points = points;
		redemption.user_id = user.id;
		redemption.date = new Date();
		redemption.save();

		EDMUtility edmUtility = new EDMUtility();
		edmUtility.requestRedemptionMail(user.name);
	}
	 
}
