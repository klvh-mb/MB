package models;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import common.system.upgrade.UpgradeScript;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

/**
 * Takes care of system versioning and upgrade.
 * Could be data patch, corrupted data fix etc.
 */
@Entity
public class SystemVersion extends domain.Entity {
    private static final play.api.Logger logger = play.api.Logger.apply(SystemVersion.class);
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    @Required
    @Column(unique = true)
    public String version;
    
    @Required
    public String execClassName;
    
    @Required
    public Boolean executed = false;
    
    @Required
    public Boolean current = false;
    
    @Required
    @Column(length = 2000)
    public String description;
    
    @Lob
    public String error;
    
    public SystemVersion() {}

    public SystemVersion(String version, String execClassName, String description) {
        this.version = version;
        this.execClassName = execClassName;
        this.description = description;
        setCreatedDate(new Date());
    }
    
    public static SystemVersion getVersion(String version) {
        Query q = JPA.em().createQuery("Select s from SystemVersion s where version = ?1");
        q.setParameter(1, version);
        try {
            return (SystemVersion)q.getSingleResult();
        } catch (NoResultException e) {
        }
        return null;
    }
    
    public static SystemVersion getCurrentVersion() {
        Query q = JPA.em().createQuery("Select s from SystemVersion s where current = ?1");
        q.setParameter(1, true);
        try {
            return (SystemVersion)q.getSingleResult();
        } catch (NoResultException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
    
    public static List<SystemVersion> getVersionsToApply() {
        Query q = JPA.em().createQuery("Select s from SystemVersion s where executed = ?1 order by CREATED_DATE");
        q.setParameter(1, false);
        try {
            return (List<SystemVersion>)q.getResultList();
        } catch (NoResultException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
    
    @Transactional
    public static void insertNewUpgradeScriptsIfAny() {
        for (UpgradeScript script : UpgradeScript.getNewUpgradeScripts()) {
            SystemVersion version = getVersion(script.getVersion());
            if (version == null)
                script.insertToSystemVersion();
        }
    }
    
    @Transactional
    public static boolean versionUpgradeIfAny() {
        logger.underlyingLogger().info("versionUpgradeIfAny()");
        
        // new scripts
        insertNewUpgradeScriptsIfAny();
        
        // apply
        List<SystemVersion> versionsToApply = getVersionsToApply();
        if (versionsToApply == null) {
            logger.underlyingLogger().info("No script to apply");
            return true;    // no error, success case
        }
            
        boolean success = true;
        for (SystemVersion versionToApply : versionsToApply) {
            logger.underlyingLogger().info("execClassName: " + versionToApply.execClassName);
            logger.underlyingLogger().info("description: " + versionToApply.description);
            
            // execute the upgrade class
            try {
                Class<?> upgradeScriptClass = 
                        Class.forName(versionToApply.execClassName);
                UpgradeScript upgradeScript = (UpgradeScript)upgradeScriptClass.newInstance();
                Method upgradeMethod = upgradeScriptClass.getMethod(UpgradeScript.UPGRADE_METHOD_NAME);
                Boolean ret = (Boolean)upgradeMethod.invoke(upgradeScript);
                if (ret) {
                    markVersionCurrent(versionToApply);
                    versionToApply.error = "";
                } else {
                    versionToApply.error = upgradeScript.getError();
                }
                
                logger.underlyingLogger().info(versionToApply.execClassName + " completed successfully");
            } catch (Exception e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
                versionToApply.error = ExceptionUtils.getStackTrace(e);
                success = false;
            }
            versionToApply.executed = true;    // set to true even for failed script, dont apply again 
            versionToApply.setUpdatedDate(new Date());
        }
        return success;
    }
    
    @Transactional
    public static void markVersionCurrent(SystemVersion version) {
        // unmark all to not current
        Query q = JPA.em().createQuery("Update SystemVersion s set current = ?1");
        q.setParameter(1, false);
        q.executeUpdate();
        
        // mark this version to current
        version.current = true;
        version.save();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Location) {
            final SystemVersion other = (SystemVersion) o;
            return new EqualsBuilder().append(version, other.version).isEquals();
        } 
        return false;
    }
    
    @Override
    public  String toString() {
        return "[" + id + "|" + version + "|" + description + "|" + execClassName + 
                "|" + executed + "|" + current + "]";
    }
}
