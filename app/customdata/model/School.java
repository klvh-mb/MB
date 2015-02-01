package customdata.model;

import customdata.LocationFK;
import models.PreNursery;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/24/14
 * Time: 10:18 PM
 */
public class School {
    public boolean hasPN;
    public String region;
    public String district;
    public String name;
    public String phoneText;
    public boolean couponSupport;
    public String formStartDate;
    public String applicationStartDate;
    public String applicationEndDate;
    public String email;
    public String url;
    public String formUrl;

    public String getKey() {
        return district+"_"+name;
    }

    public void mergeOverride(School override) {
        this.phoneText = (override.phoneText != null) ? override.phoneText : phoneText;
        this.couponSupport = override.couponSupport;
        this.formStartDate = (override.formStartDate != null) ? override.formStartDate : formStartDate;
        this.applicationStartDate = (override.applicationStartDate != null) ? override.applicationStartDate : applicationStartDate;
        this.applicationEndDate = (override.applicationEndDate != null) ? override.applicationEndDate : applicationEndDate;
        this.email = (override.email != null) ? override.email : email;
        this.url = (override.url != null) ? override.url : url;
        this.formUrl = (override.formUrl != null) ? override.formUrl : formUrl;
    }

    public PreNursery toPreNursery(String schoolYear) {
        PreNursery pn = new PreNursery();
        pn.regionId = LocationFK.REGION_MAP.get(region);
        pn.districtId = LocationFK.DISTRICT_MAP.get(district);
        pn.pnName = name;
        pn.url = url;
        pn.phoneText = beautifyPhoneText(phoneText);
        pn.email = email;
        pn.couponSupport = couponSupport;
        return pn;
    }

    private static String beautifyPhoneText(String phoneText) {
        if (phoneText != null && phoneText.length() == 8) {
            try {
                Integer.parseInt(phoneText);
                phoneText = phoneText.substring(0,4)+"-"+phoneText.substring(4);
            } catch (Exception e) { }
        }
        return phoneText;
    }

    private static LocalDate toLocalDate(String dateStr) {
        char[] chars = dateStr.toCharArray();

        int year=0, month=0, day=0;
        String value = "";
        for (char c : chars) {
            if (Character.isDigit(c)) {
                value += c;
            } else if (c == '年') {
                try {
                    year = Integer.parseInt(value);
                    value = "";
                    if (year < 2000) {
                        year += 2000;
                    }
                } catch (Exception e) { }
            } else if (c == '月') {
                try {
                    month = Integer.parseInt(value);
                    value = "";
                } catch (Exception e) { }
            } else if (c == '日') {
                try {
                    day = Integer.parseInt(value);
                    value = "";
                } catch (Exception e) { }
            }
        }

        DateTime now = new DateTime();
        if (year == 0) {
            year = now.getYear();
        }
        if (month == 0) {
            month = now.getMonthOfYear();
        }
        if (day == 0) {
            day = dateStr.contains("月中") ? 15 : 1;
        }
        return new LocalDate(year, month, day);
    }
}
