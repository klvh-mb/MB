package viewmodel;

import common.cache.LocationCache;
import common.utils.StringUtil;
import domain.DefaultValues;
import models.PlayGroup;
import models.User;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 20/6/15
 * Time: 2:50 PM
 */
public class PlayGroupVM {
    @JsonProperty("id")  public Long id;
    @JsonProperty("commId") public Long communityId;
    @JsonProperty("icon") public String icon;
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

    @JsonProperty("targ")  public String target;
    @JsonProperty("tclz")  public String trailClass;
    @JsonProperty("isEng")  public boolean inEnglish;
    @JsonProperty("isCant")  public boolean inCantonese;
    @JsonProperty("isMand")  public boolean inMandarin;

    @JsonProperty("nop") public int noOfPosts;
    @JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("nob") public int noOfBookmarks;

    @JsonProperty("isLike") public boolean isLike = false;
    @JsonProperty("isBookmarked") public boolean isBookmarked = false;

    /**
     * Ctor
     */
    public PlayGroupVM(PlayGroup pg, User user) {
        this(pg, user, false);
        this.isBookmarked = pg.isBookmarkedBy(user);
    }

    public PlayGroupVM(PlayGroup pg, User user, boolean isBookmarked) {
        this.id = pg.id;
        this.communityId = pg.communityId;
        this.icon = pg.icon;

        this.isMyDistrict = false;
        if (user.userInfo != null && user.userInfo.location != null) {
            this.isMyDistrict = user.userInfo.location.id.equals(pg.districtId);
        }
        this.districtName = LocationCache.getDistrict(pg.districtId).getDisplayName();
        this.districtId = pg.districtId;
        this.name = pg.name;
        if (!pg.name.equals(pg.nameEn)) {
        	this.nameEn = pg.nameEn;
        }
        this.url = pg.url;
        this.phoneText = pg.phoneText;
        this.phoneUrl = StringUtil.removeNonDigits(pg.phoneText);
        this.email = pg.email;
        this.address = pg.address;
        if (pg.mapUrlSuffix != null) {
            this.mapUrl = DefaultValues.GOOGLEMAP_PREFIX + pg.mapUrlSuffix;
        }

        this.target = pg.target;
        this.trailClass = pg.trailClass;
        this.inEnglish = pg.inEnglish;
        this.inCantonese = pg.inCantonese;
        this.inMandarin = pg.inMandarin;

        this.noOfPosts = pg.noOfPosts;
        this.noOfLikes = pg.noOfLikes;
        this.noOfViews = pg.noOfViews;
        this.noOfBookmarks = pg.noOfBookmarks;

        if (User.isLoggedIn(user)){
            try {
    		    this.isLike = pg.isLikedBy(user);
            } catch (Exception e) {
                this.isLike = false;
            }
        }
        this.isBookmarked = isBookmarked;
    }
}
