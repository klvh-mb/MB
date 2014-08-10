package viewmodel;

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

    @JsonProperty("myd")  public boolean isMyDistrict;
    @JsonProperty("dis")  public String districtName;
    @JsonProperty("n")    public String name;
    @JsonProperty("url")  public String url;
    @JsonProperty("pho")  public String phoneText;
    @JsonProperty("em")   public String email;
    @JsonProperty("adr")   public String address;
    @JsonProperty("coup") public boolean couponSupport;
    @JsonProperty("fds")  public String formStartDateStr;
    @JsonProperty("ads")  public String applicationStartDateStr;
    @JsonProperty("eds")  public String applicationEndDateStr;
    @JsonProperty("fom")  public String formUrl;
    @JsonProperty("map")  public String mapUrl;

    public PreNurseryVM(PreNursery pn, boolean isMyDistrict, String districtName) {
        this.isMyDistrict = isMyDistrict;
        this.districtName = districtName;
        this.name = pn.name;
        this.url = pn.url;
        this.phoneText = pn.phoneText;
        this.email = pn.email;
        this.address = pn.address;
        this.couponSupport = pn.couponSupport;
        this.formStartDateStr = pn.formStartDateString;
        this.applicationStartDateStr = pn.applicationStartDateString;
        this.applicationEndDateStr = pn.applicationEndDateString;
        this.formUrl = pn.formUrl;
        if (pn.mapUrlSuffix != null) {
            this.mapUrl = MAPURL_PREFIX + pn.mapUrlSuffix;
        }
    }
}
