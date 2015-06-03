package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.Commentable;
import domain.Likeable;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

@Entity
public class GameGift extends SocialObject implements Commentable, Likeable {
    private static play.api.Logger logger = play.api.Logger.apply(GameGift.class);
    
    public String image;

    public String imageThumb;
    
    @Column(length = 1024)
	public String description;
	
    @Column(length = 1024)
    public String redeemInfo;
    
    @Column(length = 1024)
    public String expirationInfo;
    
    @Column(length = 1024)
    public String shippingInfo;
    
    @Column(length = 1024)
    public String customerCareInfo;
    
    @Column(length = 1024)
    public String moreInfo;
    
    public long requiredPoints = 0L;
    
    public long requiredLevel = 0L;
    
    public long quantityTotal = 0L;
    
    public long quantityAvailable = 0L;
    
	public Date startDate;

    public Date endDate;

    @Enumerated(EnumType.STRING)
	public FeatureType featureType;
	
    @Enumerated(EnumType.STRING)
    public RedeemType redeemType;
	
    @Enumerated(EnumType.STRING)
	public GiftType giftType;
	
    @Enumerated(EnumType.STRING)
    public GiftState giftState;
    
    public static enum FeatureType {
        NONE,
        RECOMMEND,
        SPONSORED
    }

	public static enum RedeemType {
        POINTS,
        LEVEL
    }

	public static enum GiftType {
    	EXPIRATION,
        QUANTITY
    }
    
	public static enum GiftState {
        NEW,
        PUBLISHED,
        STARTED,
        ENDED,
        CLOSED
    }
	
    public int noOfLikes = 0;
    
	public int noOfViews = 0;
	
	public int noOfRedeems = 0;
    
    public GameGift() {}
    
    @Transactional
	public static List<GameGift> getAllGameGifts() {
		Query q = JPA.em().createQuery("Select g from GameGift g where g.giftState in (?1,?2,?3) and g.deleted = false order by startDate desc,id desc");
		q.setParameter(1, GiftState.PUBLISHED);
		q.setParameter(2, GiftState.STARTED);
		q.setParameter(3, GiftState.ENDED);
		return (List<GameGift>)q.getResultList();
	}
	
	private static GameGift getGameGiftByState(GiftState giftState) {
        Query q = JPA.em().createQuery("SELECT g FROM GameGift g where g.giftState = ?1 and g.deleted = false order by startDate desc");
        q.setParameter(1, giftState);
        q.setMaxResults(1);
        try {
            return (GameGift) q.getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }
	
	public static GameGift findById(Long id) {
		Query q = JPA.em().createQuery("SELECT g FROM GameGift g where g.id = ?1 and g.deleted = false");
		q.setParameter(1, id);
		try {
		    return (GameGift) q.getSingleResult();
		} catch(NoResultException e) {
            return null;
        }
	}
	
	@Override
	public void onLikedBy(User user) {
		recordLike(user);
		this.noOfLikes++;
		user.likesCount++;
	}

    @Override
    public void onUnlikedBy(User user) throws SocialObjectNotLikableException {
        this.noOfLikes--;
        user.likesCount--;
    }
    
	@Override
    public boolean equals(Object o) {
        if (o != null && o instanceof GameGift) {
            final GameGift other = (GameGift) o;
            return new EqualsBuilder().append(id, other.id).isEquals();
        } 
        return false;
    }
	
    @Override
    public String toString() {
        return "GameGift{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                "views='" + noOfViews + '\'' +
                "likes='" + noOfLikes + '\'' +
                "redeems='" + noOfRedeems + '\'' +
                "start='" + startDate + '\'' +
                "end='" + endDate + '\'' +
                "type='" + giftType.name() + '\'' +
                "state='" + giftState.name() + '\'' +
                '}';
    }
}