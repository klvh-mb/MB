package common.schedule;

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
                    if (strLine.startsWith("indexTagWords")) {
                        TaggingEngine.indexTagWords();
                    }
                }

                in.close();
            } catch (Exception e) {
                logger.underlyingLogger().error("", e);
            }
        }
    }
}
