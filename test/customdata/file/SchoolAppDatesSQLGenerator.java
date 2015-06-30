package customdata.file;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 30/6/15
 * Time: 12:06 PM
 */
public class SchoolAppDatesSQLGenerator {

    private static final String INCLUDE_COL = "Include";
    private static final String ID_COL = "Id";
    private static final String NAME_COL = "Name";
    private static final String START_COL = "Application Start Date";

    // Note: Change to your local path to tsv file
    private static final String FILE_PATH = "/Users/vichoty/Downloads/AppDates.tsv";

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));

        Map<Integer, String> headerMap = parseHeaderLine(br.readLine());
        List<SchoolEntry> entries = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] row = line.split("\t");

            boolean include = false;
            SchoolEntry entry = new SchoolEntry();
            for (int i = 0; i < row.length; i++) {
                String header = headerMap.get(i);
                if (header == null) {
                    System.out.println("Error in school entry, can't find header");
                } else {
                    String value = row[i];
                    if (value != null && !"".equals(value)) {
                        value = value.trim();

                        if (header.equals(INCLUDE_COL)) {
                            include = "Y".equals(value);
                        } else if (header.equals(ID_COL)) {
                            entry.id = value;
                        } else if (header.equals(NAME_COL)) {
                            entry.name = value;
                        } else if (header.equals(START_COL)) {
                            entry.appDateText = value;
                        }
                    }
                }
            }

            if (include && entry.id != null && entry.name != null && StringUtils.isNotBlank(entry.appDateText)) {
                entries.add(entry);
            }
        }

        br.close();

        System.out.println("=== Number of entries: "+entries.size());

        for (SchoolEntry entry : entries) {
            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE PRENURSERY set applicationDateText='").append(entry.appDateText).append("' ");
            sb.append("WHERE id=").append(entry.id).append(" AND name='").append(entry.name).append("';");
            System.out.println(sb.toString());
        }
    }

    private static Map<Integer, String> parseHeaderLine(String headerLine) {
        if (headerLine == null) {
            throw new IllegalStateException("Missing header");
        }

        Map<Integer, String> headerMap = new HashMap<>();
        String[] headers = headerLine.split("\t");
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(i, headers[i]);
        }
        return headerMap;
    }

    private static class SchoolEntry {
        public String id;
        public String name;
        public String appDateText;

        @Override
        public String toString() {
            return "SchoolEntry{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", appDateText='" + appDateText + '\'' +
                    '}';
        }
    }
}
