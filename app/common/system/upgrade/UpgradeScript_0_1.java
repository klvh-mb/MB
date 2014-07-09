package common.system.upgrade;

import java.io.File;
import java.io.IOException;

import controllers.Application;
import models.Resource;
import models.SystemVersion;
import models.User;

/**
 * 1. insert super admin profile pic
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_0_1 extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_0_1.class);
    
    public UpgradeScript_0_1() {
    }
    
    @Override
    public String getVersion() {
        return "0.1";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                UpgradeScript_0_1.class.getName(), 
                "Insert SuperAdmin profile pic");
        version.save();
    }
    
    @Override
    public boolean upgrade() {
        User superAdmin = Application.getSuperAdmin();
        try {
            superAdmin.setPhotoProfile(new File(Resource.STORAGE_PATH + "/default/logo/logo-mB-1.png"));
        } catch (IOException e) {
            logger.underlyingLogger().error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }
}
