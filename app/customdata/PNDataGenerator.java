package customdata;

import customdata.file.SchoolFileCSVReader;
import customdata.myschoolhk.MySchoolFetcher;
import customdata.model.School;
import models.PreNursery;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/25/14
 * Time: 10:39 PM
 */
public class PNDataGenerator {
    private static final String SCHOOL_YEAR = "2015";
    private static final String OVERRIDE_CSV = "C:\\Tmp\\PN\\pn_override.csv";
    private static final String OUT_SQL_FILE = "C:\\Tmp\\PN\\pn_"+SCHOOL_YEAR+".sql";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<PreNursery> result = generateResults(SCHOOL_YEAR);

        System.out.println("Merged results. Count="+result.size());
        writeSqlFile(result, OUT_SQL_FILE);
    }

    private static List<PreNursery> generateResults(String schoolYear) throws Exception {
        // 1) Get from Site
        Map<String, School> masterMap = genFromMySchoolHkSite();
        // Testing
//        Map<String, School> masterMap = new HashMap<>();

        // 2) Get overrides from file
        Map<String, School> overrides = genFromFile();

        // 3) Merge
        for(String key : overrides.keySet()) {
            School override = overrides.get(key);

            School pn = masterMap.get(key);
            if (pn != null) {
                pn.mergeOverride(override);
            } else {
                masterMap.put(key, override);
            }
        }

        List<PreNursery> pNs = new ArrayList<>();
        for (School entry : masterMap.values()) {
            pNs.add(entry.toPreNursery(schoolYear));
        }
        return pNs;
    }

    private static void writeSqlFile(List<PreNursery> pNs, String outFilePath) throws Exception {
        File fileDir = new File(outFilePath);
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDir), "UTF8"));
        try {
            System.out.println("Total PNs="+pNs.size()+". Writing SQL file: "+ outFilePath);

            out.append(PreNursery.getDeleteAllSql()).append("\n\n");
            for (PreNursery pn : pNs) {
                out.append(pn.getInsertSql()).append("\n");
            }

            out.flush();
        } finally {
            out.close();
        }
    }

    private static Map<String, School> genFromFile() {
        Map<String, School> result = Collections.EMPTY_MAP;
        try {
            SchoolFileCSVReader reader = new SchoolFileCSVReader();
            reader.read(OVERRIDE_CSV);
            result = reader.getPNs();

            System.out.println("Fetched Overrides. Count="+result.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Map<String, School> genFromMySchoolHkSite() {
        Map<String, School> allPNs = new HashMap<>();

        Map<String, School> hkPNs = MySchoolFetcher.fetchPNs(MySchoolFetcher.Region.HK);
        for (School school : hkPNs.values()) {
            school.region = LocationFK.REGION_HK;
            allPNs.put(school.getKey(), school);
        }
        System.out.println("Fetched HK PNs. Count="+hkPNs.size());

        Map<String, School> klPNs = MySchoolFetcher.fetchPNs(MySchoolFetcher.Region.KL);
        for (School school : klPNs.values()) {
            school.region = LocationFK.REGION_KL;
            allPNs.put(school.getKey(), school);
        }
        System.out.println("Fetched KL PNs. Count="+klPNs.size());

        Map<String, School> ntPNs = MySchoolFetcher.fetchPNs(MySchoolFetcher.Region.NT);
        for (School school : ntPNs.values()) {
            school.region = LocationFK.REGION_NT;
            allPNs.put(school.getKey(), school);
        }
        System.out.println("Fetched NT PNs. Count="+ntPNs.size());
        return allPNs;
    }
}
