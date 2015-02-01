package models;

import domain.Commentable;
import domain.Likeable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/7/14
 * Time: 9:41 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class PreNursery extends SocialObject implements Likeable, Commentable {
    private static final play.api.Logger logger = play.api.Logger.apply(PreNursery.class);

    public Long regionId;

    public Long districtId;

    public String pnName;

    public String phoneText;
    public String url;
    public String email;
    public String address;
    public String mapUrlSuffix;

    public boolean couponSupport = false;
    public String classTimes;       // comma separated (AM,PM,WD)


    // Ctor
    public PreNursery() {}

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
        StringBuilder sb = new StringBuilder();
        sb.append("insert into PreNursery (");
        sb.append("regionId, districtId, pnName, url, phoneText, email, address, couponSupport, ");
        sb.append("formUrl, mapUrlSuffix, classTimes");
        sb.append(") values (");

        sb.append(regionId).append(", ");
        sb.append(districtId).append(", ");
        sb.append("'").append(pnName).append("', ");
        if (url != null) sb.append("'").append(url).append("', "); else sb.append("NULL, ");
        if (phoneText != null) sb.append("'").append(phoneText).append("', "); else sb.append("NULL, ");
        if (email != null) sb.append("'").append(email).append("', "); else sb.append("NULL, ");
        if (address != null) sb.append("'").append(address.replace("'","")).append("', "); else sb.append("NULL, ");
        sb.append(couponSupport ? 1 : 0).append(", ");
        if (mapUrlSuffix != null) sb.append("'").append(mapUrlSuffix.replace("'","")).append("', "); else sb.append("NULL, ");
        if (classTimes != null) sb.append("'").append(classTimes.replace("'","")).append("', "); else sb.append("NULL, ");

        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(");");
        return sb.toString();
    }

    public String getKey() {
        return districtId+"_"+pnName;
    }
}
