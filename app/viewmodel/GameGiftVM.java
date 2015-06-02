package viewmodel;

import java.util.Date;

import models.GameGift;
import models.GameGift.GiftState;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.DefaultValues;

public class GameGiftVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("im") public String image;
	@JsonProperty("ds") public String description;
	@JsonProperty("ri") public String redeemInfo;
	@JsonProperty("furl") public String formUrl;
	@JsonProperty("ft") public String featureType;
	@JsonProperty("rt") public String redeemType;
	@JsonProperty("gt") public String giftType;
	@JsonProperty("gs") public String giftState;
	@JsonProperty("sd") public Date startDate;
	@JsonProperty("ed") public Date endDate;
	@JsonProperty("qt") public Long quantity;
	@JsonProperty("cd") public Date createdDate;
    @JsonProperty("cb") public String createdBy;
    
	@JsonProperty("ac") public boolean isActive = false;
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("isRedeemed") public boolean isRedeemed = false;
    @JsonProperty("isLike") public boolean isLike = false;

    @JsonProperty("rc") public Long redeemedUsersCount = -1L;
    
	public GameGiftVM(GameGift gameGift) {
	    this(gameGift, false);
	}
	
	public GameGiftVM(GameGift gameGift, boolean preview) {
	    this.id = gameGift.id;
	    this.name = gameGift.name;
	    this.image = gameGift.image;
	    if (preview && gameGift.description.length() > DefaultValues.DEFAULT_PREVIEW_CHARS) {
	        this.description = gameGift.description.substring(0, DefaultValues.DEFAULT_PREVIEW_CHARS);
	    } else {
	        this.description = gameGift.description;
	    }
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
		
		this.noOfLikes = gameGift.noOfLikes;
		this.noOfViews = gameGift.noOfViews;
	}

    public GameGiftVM(GameGift gameGift, User user) {
        this(gameGift);
        
        this.isRedeemed = false;	//GameGiftRedeemTransaction.isRedeemed(user.id, gameGift.id);
        
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
