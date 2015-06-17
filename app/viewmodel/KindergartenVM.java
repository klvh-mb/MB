package viewmodel;

import java.text.DecimalFormat;

import common.cache.LocationCache;
import common.utils.StringUtil;
import models.Kindergarten;
import models.User;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;

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
    
    @JsonProperty("hasPN")  public boolean hasPN;

    @JsonProperty("feeAmN")  public String annualFeeAM_N;
    @JsonProperty("feePmN")  public String annualFeePM_N;
    @JsonProperty("feeWdN")  public String annualFeeWD_N;
    @JsonProperty("feeAmL")  public String annualFeeAM_LKG;
    @JsonProperty("feePmL")  public String annualFeePM_LKG;
    @JsonProperty("feeWdL")  public String annualFeeWD_LKG;
    @JsonProperty("feeAmU")  public String annualFeeAM_UKG;
    @JsonProperty("feePmU")  public String annualFeePM_UKG;
    @JsonProperty("feeWdU")  public String annualFeeWD_UKG;
    
    @JsonProperty("cpFeeAmN")  public String cpAnnualFeeAM_N;
    @JsonProperty("cpFeePmN")  public String cpAnnualFeePM_N;
    @JsonProperty("cpFeeWdN")  public String cpAnnualFeeWD_N;
    @JsonProperty("cpFeeAmL")  public String cpAnnualFeeAM_LKG;
    @JsonProperty("cpFeePmL")  public String cpAnnualFeePM_LKG;
    @JsonProperty("cpFeeWdL")  public String cpAnnualFeeWD_LKG;
    @JsonProperty("cpFeeAmU")  public String cpAnnualFeeAM_UKG;
    @JsonProperty("cpFeePmU")  public String cpAnnualFeePM_UKG;
    @JsonProperty("cpFeeWdU")  public String cpAnnualFeeWD_UKG;
    
    @JsonProperty("nadAmN")  public String numEnrollAM_N;
    @JsonProperty("nadPmN")  public String numEnrollPM_N;
    @JsonProperty("nadWdN")  public String numEnrollWD_N;
    @JsonProperty("nadAmL")  public String numEnrollAM_LKG;
    @JsonProperty("nadPmL")  public String numEnrollPM_LKG;
    @JsonProperty("nadWdL")  public String numEnrollWD_LKG;
    @JsonProperty("nadAmU")  public String numEnrollAM_UKG;
    @JsonProperty("nadPmU")  public String numEnrollPM_UKG;
    @JsonProperty("nadWdU")  public String numEnrollWD_UKG;
    @JsonProperty("nadAmT")  public String numEnrollAM_T;
    @JsonProperty("nadPmT")  public String numEnrollPM_T;
    @JsonProperty("nadWdT")  public String numEnrollWD_T;

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

    @JsonProperty("appDateTxt") public String applicationDateText = null;
    @JsonProperty("openDayTxt") public String openDayText = null;
    
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
        this.icon = kg.icon;

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

        this.hasPN = kg.hasPN;
        
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
        this.summerUniformFee = kg.getSummerUniformFee() == null? "-" : kg.getSummerUniformFee();
        this.winterUniformFee = kg.getWinterUniformFee() == null? "-" : kg.getSummerUniformFee();
        this.schoolBagFee = kg.getSchoolBagFee() == null? "-" : kg.getSchoolBagFee();
        this.teaAndSnacksFee = kg.getTeaAndSnacksFee() == null? "-" : kg.getTeaAndSnacksFee();
        this.textBooksFee = kg.getTextBooksFee() == null? "-" : kg.getTextBooksFee();
        this.workBooksFee = kg.getWorkBooksFee() == null? "-" : kg.getWorkBooksFee();

        // fee total
        try {
	        this.numEnrollAM_T = String.valueOf(Integer.parseInt(kg.numEnrollAM_N) + Integer.parseInt(kg.numEnrollAM_LKG) + Integer.parseInt(kg.numEnrollAM_UKG));
	        this.numEnrollPM_T = String.valueOf(Integer.parseInt(kg.numEnrollPM_N) + Integer.parseInt(kg.numEnrollPM_LKG) + Integer.parseInt(kg.numEnrollPM_UKG));
	        this.numEnrollWD_T = String.valueOf(Integer.parseInt(kg.numEnrollWD_N) + Integer.parseInt(kg.numEnrollWD_LKG) + Integer.parseInt(kg.numEnrollWD_UKG));
        } catch (Exception e) {
        	this.numEnrollAM_T = null;
        	this.numEnrollPM_T = null;
        	this.numEnrollWD_T = null;
        }
        
        // fee after coupon
    	if (couponSupport) {
    		this.cpAnnualFeeAM_N = getFeeAfterCoupon(kg.annualFeeAM_N);
            this.cpAnnualFeePM_N = getFeeAfterCoupon(kg.annualFeePM_N);
            this.cpAnnualFeeWD_N = getFeeAfterCoupon(kg.annualFeeWD_N);
            this.cpAnnualFeeAM_LKG = getFeeAfterCoupon(kg.annualFeeAM_LKG);
            this.cpAnnualFeePM_LKG = getFeeAfterCoupon(kg.annualFeePM_LKG);
            this.cpAnnualFeeWD_LKG = getFeeAfterCoupon(kg.annualFeeWD_LKG);
            this.cpAnnualFeeAM_UKG = getFeeAfterCoupon(kg.annualFeeAM_UKG);
            this.cpAnnualFeePM_UKG = getFeeAfterCoupon(kg.annualFeePM_UKG);
            this.cpAnnualFeeWD_UKG = getFeeAfterCoupon(kg.annualFeeWD_UKG);
    	}
        
        this.noOfPosts = kg.noOfPosts;
        this.noOfLikes = kg.noOfLikes;
        this.noOfViews = kg.noOfViews;
        this.noOfBookmarks = kg.noOfBookmarks;

        this.applicationDateText = kg.applicationDateText;
        this.openDayText = kg.openDayText;

        if (User.isLoggedIn(user)){
            try {
    		    this.isLike = kg.isLikedBy(user);
            } catch (Exception e) {
                this.isLike = false;
            }
        }
        this.isBookmarked = isBookmarked;
    }
    
    private String getFeeAfterCoupon(String value) {
        if (value == null) return null;

    	int fee = parseFee(value);
    	if (fee == -1)
    		return value;
    	
    	String feeSuffix = " "+value.substring(value.indexOf("("));
    	
    	if (fee <= DefaultValues.KG_COUPON_FIX_2014_15)
    		return "$0"+feeSuffix;
    	return formatFee(fee-DefaultValues.KG_COUPON_FIX_2014_15)+feeSuffix;
    }
    
    private String formatFee(int fee) {
    	DecimalFormat formatter = new DecimalFormat("#,###");
    	return "$"+formatter.format(fee);
    }
    
    private int parseFee(String value) {
    	value = value.trim();
    	if (StringUtils.isEmpty(value) || "-".equals(value))
    		return -1;
    	
    	int start = 0;
    	int end = value.length();
    	
    	if (value.contains("$")) {
    		start = value.indexOf("$") + 1;
    	}
    	if (value.contains("(")) {
			end = value.indexOf("(");
		}
    	String fee = value.substring(start, end).trim();
    	fee = StringUtils.remove(fee, ",");
    	try {
    		return Integer.parseInt(fee);	
    	} catch (Exception e) {
    		return -1;
    	}
    }
}
