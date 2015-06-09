package viewmodel;

import java.util.Date;

import models.GameGift;
import models.GameGift.GiftState;
import models.RedeemTransaction;
import models.User;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

public class GameGiftVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("imt") public String imageThumb;
	@JsonProperty("ds") public String description;
	@JsonProperty("ri") public String redeemInfo;
	@JsonProperty("ei") public String expirationInfo;
	@JsonProperty("si") public String shippingInfo;
	@JsonProperty("ci") public String customerCareInfo;
	@JsonProperty("mi") public String moreInfo;
	@JsonProperty("ft") public String featureType;
	@JsonProperty("rt") public String redeemType;
	@JsonProperty("gt") public String giftType;
	@JsonProperty("gs") public String giftState;
	@JsonProperty("sd") public Date startDate;
	@JsonProperty("ed") public Date endDate;
	@JsonProperty("rp") public Long requiredPoints;
	@JsonProperty("qt") public Long quantityTotal;
	@JsonProperty("qa") public Long quantityAvailable;
	@JsonProperty("cd") public Date createdDate;
    @JsonProperty("cb") public String createdBy;
    
	@JsonProperty("ac") public boolean isActive = false;
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("isPending") public boolean isPending = false;
    @JsonProperty("isLike") public boolean isLike = false;

    @JsonProperty("rc") public Long redeemedUsersCount = -1L;
    
	public GameGiftVM(GameGift gameGift) {
	    this(gameGift, false);
	}
	
	public GameGiftVM(GameGift gameGift, boolean preview) {
	    this.id = gameGift.id;
	    this.name = gameGift.name;
	    this.image = gameGift.image;
	    this.imageThumb = StringUtils.isEmpty(gameGift.imageThumb)? gameGift.image : gameGift.imageThumb;
	    if (!preview) {
	    	this.description = gameGift.description;
		    this.redeemInfo = gameGift.redeemInfo;
		    this.expirationInfo = gameGift.expirationInfo;
		    this.shippingInfo = gameGift.shippingInfo;
		    this.customerCareInfo = gameGift.customerCareInfo;
		    this.moreInfo = gameGift.moreInfo;
	    }
	    
	    this.featureType = gameGift.featureType.name();
	    this.redeemType = gameGift.redeemType.name();
		this.giftType = gameGift.giftType.name();
		this.giftState = gameGift.giftState.name();
		if ((gameGift.giftState == GiftState.NEW || 
				gameGift.giftState == GiftState.PUBLISHED || 
				gameGift.giftState == GiftState.STARTED) && 
				gameGift.endDate.after(new Date())) {
		    this.isActive = true;
		}
		this.startDate = gameGift.startDate;
		this.endDate = gameGift.endDate;
		
		this.requiredPoints = gameGift.requiredPoints;
		this.quantityTotal = gameGift.quantityTotal;
		this.quantityAvailable = gameGift.quantityAvailable;
		
		this.noOfLikes = gameGift.noOfLikes;
		this.noOfViews = gameGift.noOfViews;
	}

    public GameGiftVM(GameGift gameGift, User user) {
        this(gameGift);
        
        RedeemTransaction redeemTransaction = 
        		RedeemTransaction.getPendingRedeemTransaction(user, gameGift.id, RedeemTransaction.RedeemType.GAME_GIFT);
        if (redeemTransaction != null) {
        	this.isPending = true;
        }
        
        try {
            this.isLike = gameGift.isLikedBy(user);
        } catch (SocialObjectNotLikableException e) {
            ;
        }
        
        if (user.isLoggedIn() && user.isEditor()) {
            this.redeemedUsersCount = 0L;	//GameController.getRedeemedUsersCount(gameGift.id);
        }
    }
}
