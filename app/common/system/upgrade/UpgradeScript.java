package common.system.upgrade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class UpgradeScript {
    
    public static final String UPGRADE_METHOD_NAME = "upgrade";
    
    private String error = "";
    
    // TODO New upgrade scripts go here!
    private static List<UpgradeScript> newUpgradeScripts =  
            new ArrayList<UpgradeScript>(Arrays.asList(
                    new UpgradeScript_No_Opt()
                    //new UpgradeScript_1_0()
            ));
    
    abstract public String getVersion();
    
    abstract public void insertToSystemVersion();
    
    abstract public boolean upgrade() throws Exception;
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getError() {
        return this.error;
    }
    
    /**
     * Insert upgrade script to SystemVersion table to execute.
     * e.g. UpradeScript_0_1.insertToSystemVersion();
     */
    public static List<UpgradeScript> getNewUpgradeScripts() {
        return newUpgradeScripts;
    }
}
