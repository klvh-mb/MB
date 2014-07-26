package customdata.myschoolhk;

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
    public String district;
    public String name;
    public String phoneText;
    public boolean couponSupport;
    public String formStartDate;
    public String applicationStartDate;
    public String email;
    public String url;

    public PreNursery toPreNursery(long regionId, String schoolYear) {
        PreNursery pn = new PreNursery();
        pn.regionId = regionId;
        pn.districtId = LocationFK.DISTRICT_MAP.get(district);
        pn.name = name;
        pn.url = url;
        pn.phoneText = phoneText;
        pn.email = email;
        pn.couponSupport = couponSupport;
        pn.formStartDateString = formStartDate;
        if (formStartDate != null) {
            pn.formStartDate = toLocalDate(formStartDate).toDate();
        }
        pn.applicationStartDateString = applicationStartDate;
        if (applicationStartDate != null) {
            pn.applicationStartDate = toLocalDate(applicationStartDate).toDate();
        }
        pn.schoolYear = schoolYear;
        return pn;
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

    @Override
    public String toString() {
        return "School{" +
                "hasPN=" + hasPN +
                ", district='" + district + '\'' +
                ", name='" + name + '\'' +
                ", phoneText='" + phoneText + '\'' +
                ", couponSupport=" + couponSupport +
                ", formStartDate='" + formStartDate + '\'' +
                ", applicationStartDate='" + applicationStartDate + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
