package common.schedule;

import common.thread.NotificationThreadLocal;
import data.DataBootstrap;
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
                // rename to prevent infinite loop.
                f.renameTo(new File("command_done.txt"));
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in performCommand", e);
            }
        }
    }

    /**
     * 1) indexTagWords
     * 2) gamificationEOD [daysBefore]
     * 3) communityStatistics [daysBefore]
     * 4) bootstrapPNCommunity
     */
    private static void performCommand(String commandLine) {
        if (commandLine.endsWith("DONE")) {
            return;
        }

        String[] tokens = commandLine.split("\\s");

        if (commandLine.startsWith("indexTagWords")) {
            TaggingEngine.indexTagWords();
        }
        else if (commandLine.startsWith("gamificationEOD")) {
            if (tokens.length > 1) {
                Integer daysBefore = Integer.valueOf(tokens[1]);
                GameAccountTransaction.performEndOfDayTasks(daysBefore);
            } else {
                logger.underlyingLogger().error("gamificationEOD missing daysBefore parameter");
            }
        }
        else if (commandLine.startsWith("communityStatistics")) {
            if (tokens.length > 1) {
                Integer daysBefore = Integer.valueOf(tokens[1]);
                CommunityStatistics.populatePastStats(daysBefore);
            } else {
                logger.underlyingLogger().error("communityStatistics missing daysBefore parameter");
            }
        }
        // PN communities
        else if (commandLine.startsWith("bootstrapPNCommunity")) {
            DataBootstrap.bootstrapPNCommunity();
        }
        // PN reviews
        else if (commandLine.startsWith("bootstrapPNReviews")) {
            if (tokens.length > 1) {
                String filePath = tokens[1];
                logger.underlyingLogger().info("Running bootstrapPNReviews with: "+filePath);

                DataBootstrap.bootstrapPNReviews(filePath);
            } else {
                logger.underlyingLogger().error("bootstrapPNReviews missing file path");
            }
        }
        // Community Posts
        else if (commandLine.startsWith("bootstrapCommunityPosts")) {
            if (tokens.length > 1) {
                String filePath = tokens[1];
                logger.underlyingLogger().info("Running bootstrapCommunityPosts with: "+filePath);
                NotificationThreadLocal.disableNotification(true);
                DataBootstrap.bootstrapCommunityPosts(filePath);
            } else {
                logger.underlyingLogger().error("bootstrapCommunityPosts missing file path");
            }
        }
    }
}
