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

import models.GameAccountTransaction.Transaction_type;

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

	 
}
