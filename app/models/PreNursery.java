package models;

import play.data.format.Formats;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/7/14
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PreNursery {
    private static final play.api.Logger logger = play.api.Logger.apply(PreNursery.class);

    public PreNursery() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public Long regionId;

    public Long districtId;

    public String name;

    public String url;

    public String phoneText;

    public String email;

    public boolean couponSupport = false;

    public String formStartDateString;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date formStartDate;

    public String applicationStartDateString;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    public Date applicationStartDate;

    public String schoolYear;


    /**
     * @return
     */
    public static String getDeleteAllSql() {
        return "delete from PreNursery;";
    }

    /**
     * @return
     */
    public String getInsertSql() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append("insert into PreNursery (");
        sb.append("regionId, districtId, name, url, phoneText, email, couponSupport, formStartDateString, formStartDate, applicationStartDateString, applicationStartDate, schoolYear");
        sb.append(") values (");
        sb.append(regionId).append(", ");
        sb.append(districtId).append(", ");
        sb.append("'").append(name).append("', ");
        if (url != null) sb.append("'").append(url).append("', "); else sb.append("NULL, ");
        if (phoneText != null) sb.append("'").append(phoneText).append("', "); else sb.append("NULL, ");
        if (email != null) sb.append("'").append(email).append("', "); else sb.append("NULL, ");
        sb.append(couponSupport ? 1 : 0).append(", ");
        if (formStartDateString != null) sb.append("'").append(formStartDateString).append("', "); else sb.append("NULL, ");
        if (formStartDate != null) sb.append("'").append(dateFormat.format(formStartDate)).append("', "); else sb.append("NULL, ");
        if (applicationStartDateString != null) sb.append("'").append(applicationStartDateString).append("', "); else sb.append("NULL, ");
        if (applicationStartDate != null) sb.append("'").append(dateFormat.format(applicationStartDate)).append("', "); else sb.append("NULL, ");

        // last column (watch commas)
        if (schoolYear != null) sb.append("'").append(schoolYear).append("'"); else sb.append("NULL");
        sb.append(");");
        return sb.toString();
    }
}
