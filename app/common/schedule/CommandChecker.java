package common.schedule;

import models.CommunityStatistics;
import models.GameAccountTransaction;
import tagword.TaggingEngine;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 24/10/14
 * Time: 11:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandChecker {
    private static play.api.Logger logger = play.api.Logger.apply(CommandChecker.class);

    public static void checkCommandFiles() {
        File f = new File("command.txt");
        if (f.exists()) {
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(f));
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String strLine;
                while ((strLine = br.readLine()) != null)   {
                    performCommand(strLine);
                }

                in.close();
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in performCommand", e);
            }
        }
    }

    private static void performCommand(String commandLine) {
        if (commandLine.endsWith("DONE")) {
            return;
        }

        String[] tokens = commandLine.split("\\s");

        if (commandLine.startsWith("indexTagWords")) {
            TaggingEngine.indexTagWords();
        }
        else if (commandLine.startsWith("gamificationEOD")) {
            Integer daysBefore = null;
            if (tokens.length > 1) {
                daysBefore = Integer.valueOf(tokens[1]);
            }
            GameAccountTransaction.performEndOfDayTasks(daysBefore);
        }
        else if (commandLine.startsWith("communityStatistics")) {
            Integer daysBefore = null;
            if (tokens.length > 1) {
                daysBefore = Integer.valueOf(tokens[1]);
            }
            CommunityStatistics.populatePastStats(daysBefore);
        }
    }
}
