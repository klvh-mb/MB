package common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Emoticon;

/**
 *  Note!!!: Logic also available in main.js.
 */
public class HtmlUtil {

    private static final int URL_TRUNCATE_LEN = 60;

    /**
     * Convert the given text to Html, with href links.
     * @param text
     * @return
     */
    public static String convertTextToHtml(String text) {
        // escape html special chars
        text = escapeHtmlSpecialChars(text);

        // convert any url text to href click-able links
        String result = text;
        if (text != null && text.contains("http")) {
            String str = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:\'\".,<>?«»“”‘’]))";
            Pattern patt = Pattern.compile(str);
            Matcher matcher = patt.matcher(text);

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
        return Emoticon.replace(result);
    }

    private static String escapeHtmlSpecialChars(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }


//    @Test
//    public void testReplace() {
//        String test1 = "some text and then the URL http://www.google.com and then some https://www.yahoo.com other text.";
//        System.out.println("Before: "+test1);
//        System.out.println("After: "+convertTextToHtml(test1));
//    }
//
//    @Test
//    public void testLongUrl() {
//        String test1 = "生痱滋用呢隻。超好，即時見效！ http://www.oralmedic.com.hk/01234567890123456789/products.html";
//        System.out.println("Before: "+test1);
//        System.out.println("After: "+convertTextToHtml(test1));
//
//        String test2 = "生痱滋用呢隻。http://www.yahoo.com 超好，即時見效！ http://www.oralmedic.com.hk/01234567890123456789/products.html";
//        System.out.println("Before: "+test2);
//        System.out.println("After: "+convertTextToHtml(test2));
//    }
//
//    @Test
//    public void testNoOp() {
//        String test1 = "some text and then the URL Hello Boy and then some other text.";
//        System.out.println("testNoOp Before: "+test1);
//        System.out.println("testNoOp  After: "+convertTextToHtml(test1));
//    }
//
//    @Test
//    public void testHttps() {
//        String test1 = "some text and then the URL https://mail.google.com and then some other text.";
//        System.out.println("testHttps Before: "+test1);
//        System.out.println("testHttps  After: "+convertTextToHtml(test1));
//    }
//
//    @Test
//    public void testChineseUrl() {
//        String test1 = "some text and then the URL http://news.mingpao.com/ins/%E6%96%B0%E8%81%9E%E8%99%95%E7%99%BC%E7%A8%BF%E4%BF%AE%E6%94%B9%E6%A2%81%E6%8C%AF%E8%8B%B1%E8%81%B2%E6%98%8E%20%20%E5%88%AA%E3%80%8C%E5%85%B6%E4%BB%96%E4%B8%89%E5%80%8B%E9%9D%9E%E6%99%AE%E9%81%B8%E6%96%B9%E6%A1%88%E4%B9%9F%E6%B2%92%E6%9C%89%E5%85%AC%E6%B0%91%E6%8F%90%E5%90%8D%E3%80%8D%E4%B8%80%E5%8F%A5/web_tc/article/20150405/s00001/1428234873780 and then some other text.";
//        System.out.println("testChineseUrl Before: "+test1);
//        System.out.println("testChineseUrl  After: "+convertTextToHtml(test1));
//    }
}
