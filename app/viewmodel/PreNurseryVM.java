package viewmodel;

import common.cache.LocationCache;
import common.utils.StringUtil;

import models.ReviewComment;
import models.User;
import models.PreNursery;
import models.PrimarySocialRelation;
import org.codehaus.jackson.annotate.JsonProperty;
import processor.PrimarySocialRelationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/7/14
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class PreNurseryVM {
    private static final String MAPURL_PREFIX = "http://maps.google.com.hk/maps?q=";

    @JsonProperty("id")  public Long id;
    @JsonProperty("myd")  public boolean isMyDistrict;
    @JsonProperty("dis")  public String districtName;
    @JsonProperty("disId")  public Long districtId;
    @JsonProperty("n")    public String name;
    @JsonProperty("ne")   public String nameEn;
    @JsonProperty("url")  public String url;
    @JsonProperty("pho")  public String phoneText;
    @JsonProperty("phol") public String phoneUrl;
    @JsonProperty("em")   public String email;
    @JsonProperty("adr")  public String address;
    @JsonProperty("map")  public String mapUrl;

    @JsonProperty("org")  public String organization;
    @JsonProperty("orgt") public String organizationType;
    @JsonProperty("cp")  public boolean couponSupport;
    @JsonProperty("ct")  public String classTimes;
    @JsonProperty("cur")  public String curriculum;
    
    @JsonProperty("n_c") public int noOfComments;
    @JsonProperty("cs") public List<ReviewCommentVM> reviews;
    @JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("nob") public int noOfBookmarks;

    @JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isBookmarked") public boolean isBookmarked = false;

    public PreNurseryVM(PreNursery pn, User user) {
        this(pn, user, false);
        this.isBookmarked = pn.isBookmarkedBy(user);
    }

    public PreNurseryVM(PreNursery pn, User user, boolean isBookmarked) {
        this.id = pn.id;

        this.isMyDistrict = false;
        if (user.userInfo != null && user.userInfo.location != null) {
            this.isMyDistrict = user.userInfo.location.id.equals(pn.districtId);
        }
        this.districtName = LocationCache.getDistrict(pn.districtId).getDisplayName();
        this.districtId = pn.districtId;
        this.name = pn.name;
        if (!pn.name.equals(pn.nameEn)) {
        	this.nameEn = pn.nameEn;
        }
        this.url = pn.url;
        this.phoneText = pn.phoneText;
        this.phoneUrl = StringUtil.removeNonDigits(pn.phoneText);
        this.email = pn.email;
        this.address = pn.address;
        if (pn.mapUrlSuffix != null) {
            this.mapUrl = MAPURL_PREFIX + pn.mapUrlSuffix;
        }

        this.organization = pn.organization;
        this.organizationType = pn.organizationType;
        this.couponSupport = pn.couponSupport;
        this.classTimes = pn.classTimes;
        this.curriculum = pn.curriculum;

        this.noOfComments = pn.noOfComments;
        this.noOfLikes = pn.noOfLikes;
        this.noOfViews = pn.noOfViews;
        this.noOfBookmarks = pn.noOfBookmarks;

        List<ReviewCommentVM> commentsToShow = new ArrayList<>();
        List<ReviewComment> reviewComments = pn.getReviewComments();

        List<Long> likeCheckIds = new ArrayList<>();
        for(ReviewComment rc : reviewComments) {
            likeCheckIds.add(rc.getId());
        }

        if (User.isLoggedIn(user)){
            Set<PrimarySocialRelationManager.PrimarySocialResult> srByUser = PrimarySocialRelationManager.getSocialRelationBy(user, likeCheckIds);

    		for(int i = reviewComments.size() - 1; i >= 0 ; i--) {
                ReviewComment rc = reviewComments.get(i);
    			ReviewCommentVM commentVM = ReviewCommentVM.toVM(rc, user, pn.noOfComments - i);
    			commentVM.isLike = srByUser.contains(new PrimarySocialRelationManager.PrimarySocialResult(rc.id, rc.objectType, models.PrimarySocialRelation.Action.LIKED));
    			commentsToShow.add(commentVM);
    		}
    		this.isLike = srByUser.contains(new PrimarySocialRelationManager.PrimarySocialResult(pn.id, pn.objectType, PrimarySocialRelation.Action.LIKED));
        } else {
            for(int i = reviewComments.size() - 1; i >= 0 ; i--) {
                ReviewComment rc = reviewComments.get(i);
                ReviewCommentVM commentVM = ReviewCommentVM.toVM(rc, user, pn.noOfComments - i);
                commentVM.isLike = false;
                commentsToShow.add(commentVM);
            }
            this.isLike = false;
        }

        this.reviews = commentsToShow;
        this.isBookmarked = isBookmarked;
    }
}
