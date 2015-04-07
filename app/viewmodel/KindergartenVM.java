package viewmodel;

import common.cache.LocationCache;
import common.utils.StringUtil;
import models.Kindergarten;
import models.User;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/4/15
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class KindergartenVM {
    private static final String MAPURL_PREFIX = "http://maps.google.com.hk/maps?q=";

    @JsonProperty("id")  public Long id;
    @JsonProperty("commId") public Long communityId;
    @JsonProperty("myd")  public boolean isMyDistrict;
    @JsonProperty("dis")  public String districtName;
    @JsonProperty("disId")  public Long districtId;
    @JsonProperty("n")    public String name;
    @JsonProperty("ne")   public String nameEn;
    @JsonProperty("url")  public String url;
    @JsonProperty("govUrl")  public String govUrl;
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
    @JsonProperty("curt")  public String curriculumType;

    @JsonProperty("feeAmN")  public String annualFeeAM_N;
    @JsonProperty("feePmN")  public String annualFeePM_N;
    @JsonProperty("feeWdN")  public String annualFeeWD_N;
    @JsonProperty("feeAmL")  public String annualFeeAM_LKG;
    @JsonProperty("feePmL")  public String annualFeePM_LKG;
    @JsonProperty("feeWdL")  public String annualFeeWD_LKG;
    @JsonProperty("feeAmU")  public String annualFeeAM_UKG;
    @JsonProperty("feePmU")  public String annualFeePM_UKG;
    @JsonProperty("feeWdU")  public String annualFeeWD_UKG;

    @JsonProperty("nadAmN")  public String numEnrollAM_N;
    @JsonProperty("nadPmN")  public String numEnrollPM_N;
    @JsonProperty("nadWdN")  public String numEnrollWD_N;
    @JsonProperty("nadAmL")  public String numEnrollAM_LKG;
    @JsonProperty("nadPmL")  public String numEnrollPM_LKG;
    @JsonProperty("nadWdL")  public String numEnrollWD_LKG;
    @JsonProperty("nadAmU")  public String numEnrollAM_UKG;
    @JsonProperty("nadPmU")  public String numEnrollPM_UKG;
    @JsonProperty("nadWdU")  public String numEnrollWD_UKG;

    @JsonProperty("nop") public int noOfPosts;
    @JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("nob") public int noOfBookmarks;

    @JsonProperty("appTxt") public String applicationDateText = null;

    @JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isBookmarked") public boolean isBookmarked = false;

    /**
     * @param kg
     * @param user
     */
    public KindergartenVM(Kindergarten kg, User user) {
        this(kg, user, false);
        this.isBookmarked = kg.isBookmarkedBy(user);
    }

    /**
     * @param kg
     * @param user
     * @param isBookmarked
     */
    public KindergartenVM(Kindergarten kg, User user, boolean isBookmarked) {
        this.id = kg.id;
        this.communityId = kg.communityId;

        this.isMyDistrict = false;
        if (user.userInfo != null && user.userInfo.location != null) {
            this.isMyDistrict = user.userInfo.location.id.equals(kg.districtId);
        }
        this.districtName = LocationCache.getDistrict(kg.districtId).getDisplayName();
        this.districtId = kg.districtId;
        this.name = kg.name;
        if (!kg.name.equals(kg.nameEn)) {
        	this.nameEn = kg.nameEn;
        }
        this.url = kg.url;
        this.govUrl = kg.govUrl;
        this.phoneText = kg.phoneText;
        this.phoneUrl = StringUtil.removeNonDigits(kg.phoneText);
        this.email = kg.email;
        this.address = kg.address;
        if (kg.mapUrlSuffix != null) {
            this.mapUrl = MAPURL_PREFIX + kg.mapUrlSuffix;
        }

        this.organization = kg.organization;
        this.organizationType = kg.organizationType;
        this.couponSupport = kg.couponSupport;
        this.classTimes = kg.classTimes;
        this.curriculum = kg.curriculum;
        this.curriculumType = kg.curriculumType;

        this.annualFeeAM_N = kg.annualFeeAM_N;
        this.annualFeePM_N = kg.annualFeePM_N;
        this.annualFeeWD_N = kg.annualFeeWD_N;
        this.annualFeeAM_LKG = kg.annualFeeAM_LKG;
        this.annualFeePM_LKG = kg.annualFeePM_LKG;
        this.annualFeeWD_LKG = kg.annualFeeWD_LKG;
        this.annualFeeAM_UKG = kg.annualFeeAM_UKG;
        this.annualFeePM_UKG = kg.annualFeePM_UKG;
        this.annualFeeWD_UKG = kg.annualFeeWD_UKG;
        this.numEnrollAM_N = kg.numEnrollAM_N;
        this.numEnrollPM_N = kg.numEnrollPM_N;
        this.numEnrollWD_N = kg.numEnrollWD_N;
        this.numEnrollAM_LKG = kg.numEnrollAM_LKG;
        this.numEnrollPM_LKG = kg.numEnrollPM_LKG;
        this.numEnrollWD_LKG = kg.numEnrollWD_LKG;
        this.numEnrollAM_UKG = kg.numEnrollAM_UKG;
        this.numEnrollPM_UKG = kg.numEnrollPM_UKG;
        this.numEnrollWD_UKG = kg.numEnrollWD_UKG;

        this.noOfPosts = kg.noOfPosts;
        this.noOfLikes = kg.noOfLikes;
        this.noOfViews = kg.noOfViews;
        this.noOfBookmarks = kg.noOfBookmarks;

        this.applicationDateText = kg.applicationDateText;

        if (User.isLoggedIn(user)){
            try {
    		    this.isLike = kg.isLikedBy(user);
            } catch (Exception e) {
                this.isLike = false;
            }
        }
        this.isBookmarked = isBookmarked;
    }
}
