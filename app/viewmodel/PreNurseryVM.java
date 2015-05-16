package viewmodel;

import common.cache.LocationCache;
import common.utils.StringUtil;

import models.User;
import models.PreNursery;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/7/14
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class PreNurseryVM {
    private static final String MAPURL_PREFIX = "http://maps.google.com.hk/maps?q=";

    @JsonProperty("id")  public Long id;
    @JsonProperty("commId") public Long communityId;
    @JsonProperty("icon") public String icon;
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
    @JsonProperty("feeHd")  public String annualFeeHD;
    @JsonProperty("feeWd")  public String annualFeeWD;
    @JsonProperty("nadm")  public String numAdmitted;
    @JsonProperty("sufee")  public String summerUniformFee;
    @JsonProperty("wufee")  public String winterUniformFee;
    @JsonProperty("sbfee")  public String schoolBagFee;
    @JsonProperty("tsfee")  public String teaAndSnacksFee;
    @JsonProperty("tbfee")  public String textBooksFee;
    @JsonProperty("wbfee")  public String workBooksFee;
    
    @JsonProperty("nop") public int noOfPosts;
    @JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("nob") public int noOfBookmarks;

    @JsonProperty("appTxt") public String applicationDateText = null;

    @JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isBookmarked") public boolean isBookmarked = false;

    /**
     * @param pn
     * @param user
     */
    public PreNurseryVM(PreNursery pn, User user) {
        this(pn, user, false);
        this.isBookmarked = pn.isBookmarkedBy(user);
    }

    /**
     * @param pn
     * @param user
     * @param isBookmarked
     */
    public PreNurseryVM(PreNursery pn, User user, boolean isBookmarked) {
        this.id = pn.id;
        this.communityId = pn.communityId;
        this.icon = pn.icon;

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
        this.govUrl = pn.govUrl;
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
        this.curriculumType = pn.curriculumType;
        this.annualFeeHD = pn.getAnnualFeeHD();
        this.annualFeeWD = pn.getAnnualFeeWD();
        this.numAdmitted = pn.getNumAdmitted();
        this.summerUniformFee = pn.getSummerUniformFee();
        this.winterUniformFee = pn.getWinterUniformFee();
        this.schoolBagFee = pn.getSchoolBagFee();
        this.teaAndSnacksFee = pn.getTeaAndSnacksFee();
        this.textBooksFee = pn.getTextBooksFee();
        this.workBooksFee = pn.getWorkBooksFee();

        this.noOfPosts = pn.noOfPosts;
        this.noOfLikes = pn.noOfLikes;
        this.noOfViews = pn.noOfViews;
        this.noOfBookmarks = pn.noOfBookmarks;

        this.applicationDateText = pn.applicationDateText;

        if (User.isLoggedIn(user)){
            try {
    		    this.isLike = pn.isLikedBy(user);
            } catch (Exception e) {
                this.isLike = false;
            }
        }
        this.isBookmarked = isBookmarked;
    }
}
