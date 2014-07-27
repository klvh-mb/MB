package customdata.file;

import customdata.model.School;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/27/14
 * Time: 12:06 PM
 */
public class SchoolFileCSVReader {
    public static final String REGION_KEY = "區";
    public static final String DISTRICT_KEY = "地區";
    public static final String NAME_KEY = "學校名稱";
    public static final String PHONE_KEY = "電話";
    public static final String COUPON_KEY = "學券";
    public static final String FORM_START_KEY = "開始派表日期";
    public static final String APP_START_KEY = "開始報名日期";
    public static final String APP_END_KEY = "截止日期";
    public static final String EMAIL_KEY = "電郵";
    public static final String URL_KEY = "學校網址";
    public static final String FORMURL_KEY = "FormUrl";
    public static final String HASPN_KEY = "PN";

    private Map<String, School> allSchools = new HashMap<>();
    private Map<String, School> pNs = new HashMap<>();

    /**
     * @param filePath
     */
    public void read(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        Map<Integer, String> headerMap = parseHeaderLine(br.readLine());

        String line;
        while ((line = br.readLine()) != null) {
            String[] row = line.split(",");

            School school = new School();
            for (int i = 0; i < row.length; i++) {
                String header = headerMap.get(i);
                if (header == null) {
                    System.out.println("Error in school entry, can't find header");
                } else {
                    String value = row[i];
                    if (value != null && !"".equals(value)) {
                        if (header.equals(HASPN_KEY)) {
                            school.hasPN = "Y".equalsIgnoreCase(value) || "Yes".equalsIgnoreCase(value);
                        } else if (header.equals(REGION_KEY)) {
                            school.region = value;
                        } else if (header.equals(DISTRICT_KEY)) {
                            school.district = value;
                        } else if (header.equals(NAME_KEY)) {
                            school.name = value;
                        } else if (header.equals(PHONE_KEY)) {
                            school.phoneText = value;
                        } else if (header.equals(COUPON_KEY)) {
                            school.couponSupport = "Y".equalsIgnoreCase(value) || "Yes".equalsIgnoreCase(value);
                        } else if (header.equals(FORM_START_KEY)) {
                            school.formStartDate = value;
                        } else if (header.equals(APP_START_KEY)) {
                            school.applicationStartDate = value;
                        } else if (header.equals(APP_END_KEY)) {
                            school.applicationEndDate = value;
                        } else if (header.equals(EMAIL_KEY)) {
                            school.email = value;
                        } else if (header.equals(URL_KEY)) {
                            school.url = value;
                        } else if (header.equals(FORMURL_KEY)) {
                            school.formUrl = value;
                        }
                    }
                }
            }

            allSchools.put(school.getKey(), school);
            if (school.hasPN) {
                pNs.put(school.getKey(), school);
            }
        }
        br.close();
    }

    private static Map<Integer, String> parseHeaderLine(String headerLine) {
        if (headerLine == null) {
            throw new IllegalStateException("Missing header");
        }

        Map<Integer, String> headerMap = new HashMap<>();
        String[] headers = headerLine.split(",");
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(i, headers[i]);
        }
        return headerMap;
    }

    public Map<String, School> getPNs() {
        return pNs;
    }
}
