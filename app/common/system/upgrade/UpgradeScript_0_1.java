package common.system.upgrade;

import java.io.File;
import java.io.IOException;

import javax.persistence.Query;

import org.apache.commons.lang.exception.ExceptionUtils;

import play.db.jpa.JPA;
import controllers.Application;
import models.Resource;
import models.SystemVersion;
import models.User;

/**
 * 1) Insert SuperAdmin profile pic. 2) Update delete=false for articles.
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
                this.getClass().getName(), 
                "1) Insert SuperAdmin profile pic. 2) Update delete=false for articles.");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("Insert SuperAdmin profile pic...");
        User superAdmin = Application.getSuperAdmin();
        try {
            superAdmin.setPhotoProfile(new File(Resource.STORAGE_PATH + "/default/logo/logo-mB-1.png"));
        } catch (IOException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            setError(ExceptionUtils.getStackTrace(e));
            return false;
        }
        
        logger.underlyingLogger().info("Update delete=false for articles...");
        Query q = JPA.em().createQuery("Update Article a set deleted = ?1");
        q.setParameter(1, false);
        q.executeUpdate();
        
        return true;
    }
}