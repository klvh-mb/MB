package customdata.myschoolhk;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import common.utils.HTTPUtil;
import customdata.model.School;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/24/14
 * Time: 10:21 PM
 */
public class SchoolParser {
    public static final String DISTRICT_KEY = "地區";
    public static final String NAME_KEY = "學校名稱";
    public static final String PHONE_KEY = "電話";
    public static final String COUPON_KEY = "學券";
    public static final String FORM_START_KEY = "開始派表日期";
    public static final String APP_START_KEY = "開始報名日期";
    public static final String APP_END_KEY = "截止報名日期";
    public static final String IVIEW_KEY = "面試日期";
    public static final String EMAIL_KEY = "電郵";
    public static final String URL_KEY = "學校網址";

    private Map<String, School> allSchools = new HashMap<>();
    private Map<String, School> pNs = new HashMap<>();

    /**
     * @param mainHtml
     * @param uri
     */
    public void parse(String mainHtml, String uri) {
        try {
            String tableHtml = extractCoreTable(mainHtml);

            tableHtml = "<html><body>"+tableHtml+"</body></html>";
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(tableHtml.getBytes()));

            Document doc = Jsoup.parse(is, "UTF-8", uri);

            Elements rows = doc.getElementsByTag("tr");
            boolean isHeader = true;
            for (Element row : rows) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                final School school = new School();

                Elements columns = row.getElementsByTag("td");
                int colIdx = 0;
                for (Element column : columns) {
                    String text = null;
                    String url = null;
                    if (column.childNodes().size() > 0) {
                        Node node = column.childNode(0);
                        if (node instanceof TextNode) {
                            text = ((TextNode) node).getWholeText();
                        }
                        else if (node instanceof Element) {
                            Element ele = (Element) node;
                            if ("a".equals(ele.tagName())) {
                                if (ele.childNode(0) instanceof TextNode) {
                                    text = ((TextNode) ele.childNode(0)).getWholeText();
                                }
                                url = ele.attr("href");
                            }
                        }
                    }

                    if (text != null) {
                        text = text.trim();
                    }

                    switch (colIdx) {
                        case 0:
                            school.district = text;
                            break;
                        case 1:
                            school.name = text;
                            school.hasPN = url != null && isPN(url);
                            break;
                        case 2:
                            school.phoneText = text;
                            break;
                        case 3:
                            school.couponSupport = "Y".equalsIgnoreCase(text);
                            break;
                        case 4:
                            school.formStartDate = text;
                            break;
                        case 5:
                            school.applicationStartDate = text;
                            break;
                        case 6:
                            school.applicationEndDate = text;
                            break;
                        case 8:
                            if (url != null) {
                                school.email = url.replaceFirst("mailto:", "");
                            }
                            break;
                        case 9:
                            school.url = url;
                            break;
                        default:
                            break;
                    }
                    colIdx++;
                }

                if (school.name != null && school.district != null) {
                    allSchools.put(school.getKey(), school);
                    if (school.hasPN) {
                        pNs.put(school.getKey(), school);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractCoreTable(String mainHtml) {
        int districtIdx = mainHtml.indexOf("地區</td>");
        int schoolIdx = mainHtml.indexOf("學校名稱</td>");

        if (districtIdx != -1 && schoolIdx != -1) {
            int tableStartIdx = mainHtml.lastIndexOf("<table", districtIdx);
            int tableEndIdx = mainHtml.indexOf("table>", districtIdx);
            return mainHtml.substring(tableStartIdx, tableEndIdx+6);
        } else {
            System.err.println("Key tags not found");
            return null;
        }
    }

    private static boolean isPN(String schoolPageUrl) {
        try {
            String pageHtml = HTTPUtil.getHTML(schoolPageUrl);
            return pageHtml.contains("預備班(N1)</td><td>Y");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, School> getAllSchools() {
        return allSchools;
    }

    public Map<String, School> getPNs() {
        return pNs;
    }
}
