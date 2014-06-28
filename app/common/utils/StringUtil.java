package common.utils;

import java.io.*;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA.
 * Date: 28/6/14
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    public static String collectionToString(Collection collection, String separator) {
        if (collection == null || collection.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (Object obj : collection) {
            sb.append(delim).append(obj);
            delim = separator;
        }
        return sb.toString();
    }

    public static String compress(String in) {
        if (in == null || "".equals(in)) {
            return in;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(in.getBytes("ISO-8859-1"));
            gzip.flush();
            gzip.finish();
            gzip.close();
            return baos.toString("ISO-8859-1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decompress(String in) {
        if (in == null || "".equals(in)) {
            return in;
        }
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(in.getBytes("ISO-8859-1")));
            InputStreamReader ir = new InputStreamReader(gzip, "ISO-8859-1");
            StringWriter sw = new StringWriter();
            char[] buffer = new char[10240];
            for (int len; (len = ir.read(buffer)) > 0; ) {
                sw.write(buffer, 0, len);
            }
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
