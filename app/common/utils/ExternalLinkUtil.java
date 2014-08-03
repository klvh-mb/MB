package common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Note!!!: Not used for now. Logic already done in main.js.
 *
 * Created by IntelliJ IDEA.
 * Date: 8/3/14
 * Time: 9:13 AM
 */
public class ExternalLinkUtil {

    private static final int URL_TRUNCATE_LEN = 60;

    public static String convertTextWithLinks(String body) {
        String result = body;
        if (body != null && body.contains("http")) {
            String str = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
            Pattern patt = Pattern.compile(str);
            Matcher matcher = patt.matcher(body);

            while (matcher.find()) {
                String urlRef = matcher.group();
                String urlDisplay;
                if (urlRef.length() > URL_TRUNCATE_LEN) {
                    urlDisplay = urlRef.substring(0, URL_TRUNCATE_LEN)+"...";
                } else {
                    urlDisplay = urlRef;
                }
                result = result.replace(urlRef, "<a href=\""+urlRef+"\" target=\"_blank\">"+urlDisplay+"</a>");
            }
        }
        return result;
    }

//    @Test
//    public void testReplace() {
//        String test1 = "some text and then the URL http://www.google.com and then some https://www.yahoo.com other text.";
//        System.out.println("Before: "+test1);
//        System.out.println("After: "+convertTextWithLinks(test1));
//    }
//
//    @Test
//    public void testLongUrl() {
//        String test1 = "生痱滋用呢隻。超好，即時見效！ http://www.oralmedic.com.hk/01234567890123456789/products.html";
//        System.out.println("Before: "+test1);
//        System.out.println("After: "+convertTextWithLinks(test1));
//
//        String test2 = "生痱滋用呢隻。http://www.yahoo.com 超好，即時見效！ http://www.oralmedic.com.hk/01234567890123456789/products.html";
//        System.out.println("Before: "+test2);
//        System.out.println("After: "+convertTextWithLinks(test2));
//    }
//
//    @Test
//    public void testNoOp() {
//        String test1 = "some text and then the URL Hello Boy and then some other text.";
//        System.out.println("testNoOp Before: "+test1);
//        System.out.println("testNoOp  After: "+convertTextWithLinks(test1));
//    }
}