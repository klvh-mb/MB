package customdata.file;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 30/6/15
 * Time: 12:06 PM
 */
public class SchoolAppDatesSQLGenerator {

    private static final String TABLE = "Kindergarten";

    private static final String INCLUDE_COL = "Include";
    private static final String ID_COL = "Id";
    private static final String NAME_COL = "Name";
    private static final String START_COL = "Application Start Date";
    private static final String OPENDAY_COL = "Open Day";
    private static final String SORT_COL = "Sort Date";

    private static SimpleDateFormat fromFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat toFormatter = new SimpleDateFormat("yyyy-MM-dd");

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
                        } else if (header.equals(OPENDAY_COL)) {
                            if (!"N".equalsIgnoreCase(value)) {
                                entry.openDayText = value;
                            }
                        } else if (header.equals(SORT_COL)) {
                            Date d = fromFormatter.parse(value);
                            entry.sortDateText = toFormatter.format(d);
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
            sb.append("UPDATE "+TABLE+" set applicationDateText='").append(entry.appDateText).append("' ");
            if (entry.openDayText != null) {
                sb.append(", openDayText='").append(entry.openDayText).append("' ");
            }
            if (entry.sortDateText != null) {
                sb.append(", applicationDate='").append(entry.sortDateText).append("' ");
            }
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
        public String openDayText;
        public String sortDateText;

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
