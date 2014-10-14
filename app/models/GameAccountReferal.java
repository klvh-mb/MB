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
public class GameAccountReferal  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccountReferal.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long sender_user_id;
	
	public Long getSender_user_id() {
		return sender_user_id;
	}

	public void setSender_user_id(Long sender_user_id) {
		this.sender_user_id = sender_user_id;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Long getInvite_user_id() {
		return invite_user_id;
	}

	public void setInvite_user_id(Long invite_user_id) {
		this.invite_user_id = invite_user_id;
	}

	public String promoCode;
	
	public Long invite_user_id;
	
	public GameAccountReferal() {}
	
	public static GameAccountReferal findByInviteUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountReferal u where invite_user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccountReferal) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

	public static GameAccountReferal findByPromoCode(String promoCode) {
		try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccountReferal u where promoCode = ?1");
	        q.setParameter(1, promoCode);
	        return (GameAccountReferal) q.getSingleResult();
	    } catch (NoResultException e) {
	        return null;
	    } 
	}

	public static void setUser(Long id2,String promoCode) {
		GameAccountReferal referal = GameAccountReferal.findByPromoCode(promoCode);
	    referal.invite_user_id = id2;
	    referal.merge();
	}
	 
}
