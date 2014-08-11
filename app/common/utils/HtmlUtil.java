package common.utils;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * 
 */
public class HtmlUtil {

    public static String escapeSpecialCharacters(String text) {
        //String escaped = text.replace("&", "&amp;").replace("<", "&lt;").replace(">","&gt;");
        String escaped = StringEscapeUtils.escapeHtml(text);
        return escaped;
    }
}
