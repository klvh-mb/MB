package common.utils;

import common.collection.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Set;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 23/5/15
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class HtmlUtilTest {

    @Before
    public void before() {
        HtmlUtil.IN_TEST = true;
    }

    @After
    public void after() {
        HtmlUtil.IN_TEST = false;
    }

    @Test
    public void testReplace() {
        String before = "some text and then the URL http://www.google.com and then some https://www.yahoo.com other text.";
        String after = HtmlUtil.convertTextToHtml(before);
        System.out.println("Before: "+before);
        System.out.println("After: "+after);
        assertTrue(after.contains("href"));
    }

    @Test
    public void testLongUrl() {
        String before = "生痱滋用呢隻。超好，即時見效！ http://www.oralmedic.com.hk/01234567890123456789/products.html";
        String after = HtmlUtil.convertTextToHtml(before);
        System.out.println("Before: "+before);
        System.out.println("After: "+after);
        assertTrue(after.contains("href"));

        before = "生痱滋用呢隻。http://www.yahoo.com 超好，即時見效！ http://www.oralmedic.com.hk/01234567890123456789/products.html";
        after = HtmlUtil.convertTextToHtml(before);
        System.out.println("Before: "+before);
        System.out.println("After: "+after);
        assertTrue(after.contains("href"));
    }

    @Test
    public void testNoOp() {
        String before = "some text and then the URL Hello Boy and then some other text.";
        String after = HtmlUtil.convertTextToHtml(before);
        System.out.println("testNoOp Before: "+before);
        System.out.println("testNoOp After: "+after);
        assertFalse(after.contains("href"));
    }

    @Test
    public void testHttps() {
        String before = "some text and then the URL https://mail.google.com and then some other text.";
        String after = HtmlUtil.convertTextToHtml(before);
        System.out.println("testHttps Before: "+before);
        System.out.println("testHttps After: "+after);
        assertTrue(after.contains("href"));
    }

    @Test
    public void testChineseUrl() {
        String before = "some text and then the URL http://news.mingpao.com/ins/%E6%96%B0%E8%81%9E%E8%99%95%E7%99%BC%E7%A8%BF%E4%BF%AE%E6%94%B9%E6%A2%81%E6%8C%AF%E8%8B%B1%E8%81%B2%E6%98%8E%20%20%E5%88%AA%E3%80%8C%E5%85%B6%E4%BB%96%E4%B8%89%E5%80%8B%E9%9D%9E%E6%99%AE%E9%81%B8%E6%96%B9%E6%A1%88%E4%B9%9F%E6%B2%92%E6%9C%89%E5%85%AC%E6%B0%91%E6%8F%90%E5%90%8D%E3%80%8D%E4%B8%80%E5%8F%A5/web_tc/article/20150405/s00001/1428234873780 and then some other text.";
        String after = HtmlUtil.convertTextToHtml(before);
        System.out.println("testChineseUrl Before: "+before);
        System.out.println("testChineseUrl After: "+after);
        assertTrue(after.contains("href"));
    }

    @Test
    public void testTagWords() {
        //String before = "#!OpenDay #!School";
        String before = "#!Playroom helarear #!學券 #!OpenDay asdsad #!deadline";
        Pair<String, String> after = HtmlUtil.convertTextWithTagWords(before);

        System.out.println("testTagWords Before: "+before);
        System.out.println("testTagWords After: "+after.first);
        System.out.println("testTagWords Tagwords: "+after.second);

        assertFalse(after.first.contains(HtmlUtil.TAGWORD_MARKER));
        assertTrue(after.second.contains("PLAYROOM"));
        assertTrue(after.second.contains("OPENDAY"));
        assertTrue(after.second.contains("DEADLINE"));
        assertTrue(after.second.contains("學券"));
    }

}
