package viewmodel;

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
    @JsonProperty("url")  public String url;
    @JsonProperty("pho")  public String phoneText;
    @JsonProperty("phol") public String phoneUrl;
    @JsonProperty("em")   public String email;
    @JsonProperty("adr")  public String address;
    @JsonProperty("map")  public String mapUrl;

    @JsonProperty("n_c") public int noOfComments;
    @JsonProperty("cs") public List<ReviewCommentVM> reviews;
	@JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;

    @JsonProperty("isLike") public boolean isLike = false;

    // deprecated.
    @JsonProperty("fds")  public String formStartDateStr;
    @JsonProperty("ads")  public String applicationStartDateStr;
    @JsonProperty("eds")  public String applicationEndDateStr;
    @JsonProperty("fom")  public String formUrl;


    public PreNurseryVM(PreNursery pn, User user, boolean isMyDistrict, String districtName) {
        this.id = pn.id;
        this.isMyDistrict = isMyDistrict;
        this.districtName = districtName;
        this.districtId = pn.districtId;
        this.name = pn.name;
        this.url = pn.url;
        this.phoneText = pn.phoneText;
        this.phoneUrl = StringUtil.removeNonDigits(pn.phoneText);
        this.email = pn.email;
        this.address = pn.address;
        if (pn.mapUrlSuffix != null) {
            this.mapUrl = MAPURL_PREFIX + pn.mapUrlSuffix;
        }

        this.noOfComments = pn.noOfComments;
        this.noOfLikes = pn.noOfLikes;
        this.noOfViews = pn.noOfViews;

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
    }
}
