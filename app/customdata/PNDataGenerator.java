package customdata;

import customdata.myschoolhk.MySchoolFetcher;
import customdata.myschoolhk.School;
import models.PreNursery;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/25/14
 * Time: 10:39 PM
 */
public class PNDataGenerator {
    private static final String SCHOOL_YEAR = "2014";
    private static final String OUT_SQL_FILE = "C:\\Tmp\\PN\\pn_"+SCHOOL_YEAR+".sql";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        writeSqlFile(OUT_SQL_FILE, SCHOOL_YEAR);
    }

    private static void writeSqlFile(String outFilePath, String schoolYear) throws Exception {
        File fileDir = new File(outFilePath);
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDir), "UTF8"));
        try {
            List<PreNursery> pNs = genFromMySchoolHkSite(schoolYear);

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

    private static List<PreNursery> genFromMySchoolHkSite(String schoolYear) {
        List<PreNursery> allPNs = new ArrayList<>();
        Map<String, School> hkPNs = MySchoolFetcher.fetchPNs(MySchoolFetcher.Region.HK);
        for (School school : hkPNs.values()) {
            allPNs.add(school.toPreNursery(LocationFK.REGION_HK_ID, schoolYear));
        }
        System.out.println("Fetched HK PNs. Count="+hkPNs.size());

        Map<String, School> klPNs = MySchoolFetcher.fetchPNs(MySchoolFetcher.Region.KL);
        for (School school : klPNs.values()) {
            allPNs.add(school.toPreNursery(LocationFK.REGION_KL_ID, schoolYear));
        }
        System.out.println("Fetched KL PNs. Count="+klPNs.size());

        Map<String, School> ntPNs = MySchoolFetcher.fetchPNs(MySchoolFetcher.Region.NT);
        for (School school : ntPNs.values()) {
            allPNs.add(school.toPreNursery(LocationFK.REGION_NT_ID, schoolYear));
        }
        System.out.println("Fetched NT PNs. Count="+ntPNs.size());
        return allPNs;
    }
}
