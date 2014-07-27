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
    @JsonProperty("myd")  public boolean isMyDistrict;
    @JsonProperty("dis")  public String districtName;
    @JsonProperty("n")    public String name;
    @JsonProperty("url")  public String url;
    @JsonProperty("pho")  public String phoneText;
    @JsonProperty("em")   public String email;
    @JsonProperty("coup") public boolean couponSupport;
    @JsonProperty("fds")  public String formStartDateStr;
    @JsonProperty("ads")  public String applicationStartDateStr;
    @JsonProperty("eds")  public String applicationEndDateStr;
    @JsonProperty("fom")  public String formUrl;

    public PreNurseryVM(PreNursery pn, boolean isMyDistrict, String districtName) {
        this.isMyDistrict = isMyDistrict;
        this.districtName = districtName;
        this.name = pn.name;
        this.url = pn.url;
        this.phoneText = pn.phoneText;
        this.email = pn.email;
        this.couponSupport = pn.couponSupport;
        this.formStartDateStr = pn.formStartDateString;
        this.applicationStartDateStr = pn.applicationStartDateString;
        this.applicationEndDateStr = pn.applicationEndDateString;
        this.formUrl = pn.formUrl;
    }
}
