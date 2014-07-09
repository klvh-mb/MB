package common.system.upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UpgradeScript {
    
    public static final String UPGRADE_METHOD_NAME = "upgrade";
    
    // TODO declare new upgrade scripts here!
    private static List<UpgradeScript> newUpgradeScripts = 
            new ArrayList<UpgradeScript>(Arrays.asList(
                new UpgradeScript_0_1()
            ));
    
    abstract public String getVersion();
    
    abstract public void insertToSystemVersion();
    
    abstract public boolean upgrade();
    
    /**
     * Insert upgrade script to SystemVersion table to execute.
     * e.g. UpradeScript_0_1.insertToSystemVersion();
     */
    public static List<UpgradeScript> getNewUpgradeScripts() {
        return newUpgradeScripts;
    }
}
