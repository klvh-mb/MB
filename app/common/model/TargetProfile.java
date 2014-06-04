package common.model;

import models.User;
import models.UserChild;

import org.joda.time.DateTime;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 31/5/14
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TargetProfile {
    // parent
    private TargetGender parentGender;
    private String district;

    // children
    private int numChildren;
    private TargetGender childrenGender;
    private int childrenMinAgeMonths;
    private int childrenMaxAgeMonths;
    
    private List<TargetYear> childYears;
    
    // TODO: Add Twins Target Support

    public static TargetProfile fromUser(User user) {
        TargetProfile profile = new TargetProfile();
        List<TargetYear> childYears = new ArrayList<TargetYear>();
        
        // parent
        profile.parentGender = TargetGender.valueOfStr(user.getGender());
        profile.district = user.getLocation();

        // children
        TargetGender childrenGender = TargetGender.Both;
        Integer childrenMinAge = null;
        Integer childrenMaxAge = null;

        List<UserChild> children = user.getChildren();
        if (children != null) {
            profile.numChildren = children.size();

            boolean hasBoy = false, hasGirl = false;
            for (UserChild child : children) {
                if (TargetGender.valueOf(child.gender) == TargetGender.Male) {
                    hasBoy = true;
                }
                else if (TargetGender.valueOf(child.gender) == TargetGender.Female) {
                    hasGirl = true;
                }

                if (child.date_of_birth != null) {
                    DateTime birthDate = new DateTime(child.date_of_birth.getTime());
                    Months months = Months.monthsBetween(birthDate, DateTime.now());

                    if (childrenMinAge == null || months.getMonths() < childrenMinAge) {
                        childrenMinAge = months.getMonths();
                    }
                    if (childrenMaxAge == null || months.getMonths() > childrenMaxAge) {
                        childrenMaxAge = months.getMonths();
                    }
                    childYears.add(TargetYear.valueOf(birthDate.getYear()));
                }
            }
            if (hasBoy && hasGirl) {
                childrenGender = TargetGender.Both;
            } else if (hasBoy) {
                childrenGender = TargetGender.Male;
            } else if (hasGirl) {
                childrenGender = TargetGender.Female;
            }
        }
        
        // TODO
        if (childYears.isEmpty()) {
            childYears.add(TargetYear.valueOf(2013));    
        }
        
        profile.childrenGender = childrenGender;
        profile.childrenMinAgeMonths = (childrenMinAge == null) ? Integer.MIN_VALUE : childrenMinAge;
        profile.childrenMaxAgeMonths = (childrenMaxAge == null) ? Integer.MAX_VALUE : childrenMaxAge;
        profile.childYears = childYears;
        
        return profile;
    }

    public TargetGender getParentGender() {
        return parentGender;
    }

    public String getDistrict() {
        return district;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public TargetGender getChildrenGender() {
        return childrenGender;
    }

    public int getChildrenMinAgeMonths() {
        return childrenMinAgeMonths;
    }

    public int getChildrenMaxAgeMonths() {
        return childrenMaxAgeMonths;
    }

    public List<TargetYear> getChildYears() {
        return childYears;
    }
    
    @Override
    public String toString() {
        return "TargetProfile{" +
                "parentGender=" + parentGender +
                ", district='" + district + '\'' +
                ", numChildren=" + numChildren +
                ", childrenGender=" + childrenGender +
                ", childrenMinAgeMonths=" + childrenMinAgeMonths +
                ", childrenMaxAgeMonths=" + childrenMaxAgeMonths +
                '}';
    }
}
