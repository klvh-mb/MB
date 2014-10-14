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
public class GameAccount  extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(GameAccount.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public Long User_id;
	
	public Long total_points = 0L;
	
	public Long redeemed_points;	
	 
	public Date last_redemption_time;
	
	public Date previous_day_accumulated_points;
	
	public String address_1;
	
	public String address_2;
	
	public String city;
	
	public Long phone;
	
	public Boolean has_upload_profile_pic = true;
	
	public Long number_of_referral_signups;
	
	public GameAccount() {}
	
	public static GameAccount findByUserId(Long id) {
	    try { 
	        Query q = JPA.em().createQuery("SELECT u FROM GameAccount u where user_id = ?1");
	        q.setParameter(1, id);
	        return (GameAccount) q.getSingleResult();
	    } catch (NoResultException e) {
	    	GameAccount account = new GameAccount();
	    	account.User_id = id;
	    	account.save();
	        return account;
	    } 
	}

	 
	public static void setPointsForSignUp(User user) {
		GameAccount account = new GameAccount();
		account.auditFields.setCreatedDate(new Date());
		account.auditFields.setUpdatedDate(new Date());
		account.User_id = user.id;
		account.total_points = account.total_points + DefaultValues.POINTS_SIGNUP;
		account.save();
		GameAccountTransaction.recordPoints(user.id, account, DefaultValues.POINTS_SIGNUP, Transaction_type.SystemCredit);
		checkForReferal(user.id);
	}

	private static void checkForReferal(Long id) {
		GameAccountReferal referal = GameAccountReferal.findByInviteUserId(id);
		if(referal == null)
			return;
		setPointsForReferalSignUp(referal.getSender_user_id());
	}

	private static void setPointsForReferalSignUp(Long sender_user_id) {
		GameAccount account = GameAccount.findByUserId(sender_user_id);
		account.auditFields.setUpdatedDate(new Date());
		account.total_points = account.total_points + DefaultValues.POINTS_SIGNUP;
		account.merge();
	}

	public static void setPointsForPhotoProfile(User user) {
		GameAccount account = GameAccount.findByUserId(user.id);
		account.auditFields.setUpdatedDate(new Date());
		account.has_upload_profile_pic = true;
		account.total_points = account.total_points + DefaultValues.POINTS_UPLOAD_PROFILE_PHOTO;
		account.merge();
		GameAccountTransaction.recordPoints(user.id, account, DefaultValues.POINTS_UPLOAD_PROFILE_PHOTO, Transaction_type.SystemCredit);
	}
	
	public static void setPointsForPostDelete(User user) {
		GameAccount account = GameAccount.findByUserId(user.id);
		account.auditFields.setUpdatedDate(new Date());
		account.total_points = account.total_points - DefaultValues.POINTS_POST_DELETE;
		account.merge();
		GameAccountTransaction.recordPoints(user.id, account, DefaultValues.POINTS_POST_DELETE, Transaction_type.SystemCredit);
	}

	public static void setPointsForPost() {
		List<Long> users = GameAccountStatistics.getUsersForPostPoints();
		for(Long user : users){
			GameAccount account = GameAccount.findByUserId(user);
			account.auditFields.setUpdatedDate(new Date());
			account.total_points = account.total_points + DefaultValues.POINTS_POSTS;
			account.merge();
			GameAccountTransaction.recordPoints(user, account, DefaultValues.POINTS_POSTS, Transaction_type.SystemCredit);
		}
	}

	public static void setPointsForComment() {
		List<Long> users = GameAccountStatistics.getUsersForCommentPoints();
		for(Long user : users){
			GameAccount account = GameAccount.findByUserId(user);
			account.auditFields.setUpdatedDate(new Date());
			account.total_points = account.total_points + DefaultValues.POINTS_COMMENT;
			account.merge();
			GameAccountTransaction.recordPoints(user, account, DefaultValues.POINTS_COMMENT, Transaction_type.SystemCredit);
		}
		
	}

	public static void setPointsForLike() {
		List<Long> users = GameAccountStatistics.getUsersForCommentPoints();
		for(Long user : users){
			GameAccount account = GameAccount.findByUserId(user);
			account.auditFields.setUpdatedDate(new Date());
			account.total_points = account.total_points + DefaultValues.POINTS_LIKES;
			account.merge();
			GameAccountTransaction.recordPoints(user, account, DefaultValues.POINTS_LIKES, Transaction_type.SystemCredit);
		}
		
	}
}
