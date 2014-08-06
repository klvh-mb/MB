package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationInfoVM {
    @JsonProperty("baseUrl") private String baseUrl;
    @JsonProperty("mobile") private boolean isMobileUser;
    
    public ApplicationInfoVM(String baseUrl, boolean isMobileUser) {
        this.baseUrl = baseUrl;
        this.isMobileUser = isMobileUser;
    }
}
