package customdata.myschoolhk;

import common.utils.HTTPUtil;
import customdata.model.School;

import java.util.Collections;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/25/14
 * Time: 10:48 PM
 */
public class MySchoolFetcher {
    public enum Region {
        HK, KL, NT
    }

    private static final String URI = "http://www.myschool.hk";
    private static final String HK_URL = "http://www.myschool.hk/education-4-20.php";
    private static final String KL_URL = "http://www.myschool.hk/education-4-21.php";
    private static final String NT_URL = "http://www.myschool.hk/education-4-22.php";

    public static Map<String, School> fetchPNs(Region region) {
        String srcUrl = HK_URL;
        if (region == Region.KL) {
            srcUrl = KL_URL;
        } else if (region == Region.NT) {
            srcUrl = NT_URL;
        }

        Map<String, School> schools = Collections.EMPTY_MAP;
        try {
            SchoolParser parser = new SchoolParser();
            parser.parse(HTTPUtil.getHTML(srcUrl), URI);
            schools = parser.getPNs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schools;
    }
}
