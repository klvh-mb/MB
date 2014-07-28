package viewmodel;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Months;

import common.model.TargetProfile;

public class UserTargetProfileVM {
    @JsonProperty("isSA") private boolean isSuperAdmin;
    @JsonProperty("gen") private String gender;
    @JsonProperty("loc") private String location;
    @JsonProperty("nc") private int numChildren;
    @JsonProperty("cgen") private String childrenGender;
    @JsonProperty("cmin") private int childrenMinAgeMonths;
    @JsonProperty("cmax") private int childrenMaxAgeMonths;
    @JsonProperty("soon") private boolean isSoonParent;
    @JsonProperty("new") private boolean isNewParent;
    
    // UI controlling flags
    @JsonProperty("pn") private boolean recommendPN;
    
    public UserTargetProfileVM(TargetProfile targetProfile) {
        this.isSuperAdmin = targetProfile.isSuperAdmin();
        this.gender = targetProfile.getParentGender().name();
        this.location = targetProfile.getLocation().getDisplayName();
        this.numChildren = targetProfile.getNumChildren();
        this.childrenGender = targetProfile.getChildrenGender().name();
        this.childrenMinAgeMonths = targetProfile.getChildrenMinAgeMonths();
        this.childrenMaxAgeMonths = targetProfile.getChildrenMaxAgeMonths();
        this.isSoonParent = targetProfile.isSoonParent();
        this.isNewParent = targetProfile.isNewParent();
        
        // UI controlling flags
        this.recommendPN = false;
        for (DateTime birthDate : targetProfile.getChildBirthDates()) {
            Months months = Months.monthsBetween(birthDate, DateTime.now());
            if (months.getMonths() >= 6 && months.getMonths() <= 30) {
                this.recommendPN = true;
            }
        }
    }
}