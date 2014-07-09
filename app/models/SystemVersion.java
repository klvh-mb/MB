package models;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
    private static final play.api.Logger logger = play.api.Logger.apply(System.class);
    
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
    public String description;
    
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
            logger.underlyingLogger().error(e.getLocalizedMessage());
        }
        return null;
    }
    
    public static SystemVersion getCurrentVersion() {
        Query q = JPA.em().createQuery("Select s from SystemVersion s where current = ?1");
        q.setParameter(1, true);
        try {
            return (SystemVersion)q.getSingleResult();
        } catch (NoResultException e) {
            logger.underlyingLogger().error(e.getLocalizedMessage());
        }
        return null;
    }
    
    public static List<SystemVersion> getVersionsToApply() {
        Query q = JPA.em().createQuery("Select s from SystemVersion s where executed = ?1 order by CREATED_DATE");
        q.setParameter(1, false);
        try {
            return (List<SystemVersion>)q.getResultList();
        } catch (NoResultException e) {
            logger.underlyingLogger().error(e.getLocalizedMessage());
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
        // new scripts
        insertNewUpgradeScriptsIfAny();
        
        // apply
        List<SystemVersion> versionsToApply = getVersionsToApply();
        if (versionsToApply == null)
            return true;    // no error, success case
        
        boolean success = true;
        for (SystemVersion versionToApply : versionsToApply) {
            // execute the upgrade class
            try {
                Class<?> upgradeScript = 
                        Class.forName(versionToApply.execClassName);
                Method upgradeMethod = upgradeScript.getMethod(UpgradeScript.UPGRADE_METHOD_NAME);
                upgradeMethod.invoke(upgradeScript.newInstance());
                
                markVersionCurrent(versionToApply);
                versionToApply.error = "";
            } catch (Exception e) {
                logger.underlyingLogger().error(e.getLocalizedMessage());
                versionToApply.error = e.getLocalizedMessage();
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
