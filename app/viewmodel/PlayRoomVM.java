package viewmodel;

import common.cache.LocationCache;
import common.utils.StringUtil;
import models.PlayRoom;
import models.User;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/5/15
 * Time: 11:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayRoomVM {
    private static final String MAPURL_PREFIX = "http://maps.google.com.hk/maps?q=";

    @JsonProperty("id")  public Long id;
    @JsonProperty("commId") public Long communityId;
    @JsonProperty("icon") public String icon;
    @JsonProperty("myd")  public boolean isMyDistrict;
    @JsonProperty("dis")  public String districtName;
    @JsonProperty("disId")  public Long districtId;
    @JsonProperty("n")    public String name;
    @JsonProperty("ne")   public String nameEn;
    @JsonProperty("desc") public String description;
    @JsonProperty("pho")  public String phoneText;
    @JsonProperty("phol") public String phoneUrl;
    @JsonProperty("url")  public String url;
    @JsonProperty("em")   public String email;
    @JsonProperty("adr")  public String address;
    @JsonProperty("map")  public String mapUrl;

    @JsonProperty("nop") public int noOfPosts;
    @JsonProperty("nol") public int noOfLikes;
    @JsonProperty("nov") public int noOfViews;
    @JsonProperty("nob") public int noOfBookmarks;


    /**
     * @param p
     * @param user
     */
    public PlayRoomVM(PlayRoom p, User user) {
        this(p, user, false);
    }

    /**
     * @param p
     * @param user
     * @param isBookmarked
     */
    public PlayRoomVM(PlayRoom p, User user, boolean isBookmarked) {
        this.id = p.id;
        this.communityId = p.communityId;

        this.isMyDistrict = false;
        if (user.userInfo != null && user.userInfo.location != null) {
            this.isMyDistrict = user.userInfo.location.id.equals(p.districtId);
        }
        this.districtName = LocationCache.getDistrict(p.districtId).getDisplayName();
        this.districtId = p.districtId;

        this.name = p.name;
        if (!p.name.equals(p.getNameEn())) {
        	this.nameEn = p.getNameEn();
        }
        this.icon = p.getIcon();
        this.description = p.getDescription();
        this.phoneText = p.getPhoneText();
        this.phoneUrl = StringUtil.removeNonDigits(p.getPhoneText());
        this.url = p.getUrl();
        this.email = p.getEmail();
        this.address = p.getAddress();
        if (p.getMapUrlSuffix() != null) {
            this.mapUrl = MAPURL_PREFIX + p.getMapUrlSuffix();
        }

        this.noOfPosts = p.noOfPosts;
        this.noOfLikes = p.noOfLikes;
        this.noOfViews = p.noOfViews;
        this.noOfBookmarks = p.noOfBookmarks;
    }

}
