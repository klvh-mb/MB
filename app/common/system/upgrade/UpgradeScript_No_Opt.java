package common.system.upgrade;

import models.SystemVersion;

/**
 * No opt. Dummy sample script.
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_No_Opt extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_No_Opt.class);
    
    public UpgradeScript_No_Opt() {
    }
    
    @Override
    public String getVersion() {
        return "0.0";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                this.getClass().getName(), 
                "No opt.");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("No opt...");
        return true;
    }
}