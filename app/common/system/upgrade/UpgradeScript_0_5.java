package common.system.upgrade;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.exception.ExceptionUtils;

import play.db.jpa.JPA;
import targeting.community.CommunityTargetingEngine;
import controllers.Application;
import models.Community;
import models.Icon;
import models.SystemVersion;
import models.User;
import models.Community.CommunityType;
import models.Icon.IconType;
import models.TargetingSocialObject.TargetingType;

/**
 * 1) Community icons.
 * 2) Targeting communities.
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_0_5 extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_0_5.class);
    
    public UpgradeScript_0_5() {
    }
    
    @Override
    public String getVersion() {
        return "0.5";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                this.getClass().getName(), 
                "1) Community icons. 2) Targeting communities.");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("Community icons...");
        
        Icon icon = null;
        icon = new Icon("cat", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/cat.png");
        icon.save();
        icon = new Icon("helmet", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/helmet.png");
        icon.save();
        
        logger.underlyingLogger().info("Targeting communities...");
        
        // SOON_MOMS_DADS
        Community community = Community.findByTargetingTypeTargetingInfo(TargetingType.SOON_MOMS_DADS, "SOON_MOMS_DADS");
        if (community == null) {
            String name = "準媽媽準爸爸♥";
            String desc = "準媽媽準爸爸♥";
            createTargetingCommunity(name, desc, 
                    "/assets/app/images/general/icons/community/boy.png",
                    TargetingType.SOON_MOMS_DADS,
                    "SOON_MOMS_DADS");
        }
        
        // NEW_MOMS_DADS
        community = Community.findByTargetingTypeTargetingInfo(TargetingType.NEW_MOMS_DADS, "NEW_MOMS_DADS");
        if (community == null) {
            String name = "新手媽媽爸爸♥";
            String desc = "新手媽媽爸爸♥";
            createTargetingCommunity(name, desc, 
                    "/assets/app/images/general/icons/community/boy.png",
                    TargetingType.NEW_MOMS_DADS,
                    "NEW_MOMS_DADS");
        }
        
        // ALL_MOMS_DADS
        community = Community.findByTargetingTypeTargetingInfo(TargetingType.ALL_MOMS_DADS, "BABY_FRIENDLY_PLACES");
        if (community == null) {
            String name = "親子好去處♥";
            String desc = "親子好去處♥";
            createTargetingCommunity(name, desc, 
                    "/assets/app/images/general/icons/community/stroller.png",
                    TargetingType.ALL_MOMS_DADS,
                    "BABY_FRIENDLY_PLACES");
        }
        
        // PUBLIC
        community = Community.findByTargetingTypeTargetingInfo(TargetingType.PUBLIC, "TRAVEL");
        if (community == null) {
            String name = "小寶寶去旅行♥";
            String desc = "小寶寶去旅行♥";
            createTargetingCommunity(name, desc, 
                    "/assets/app/images/general/icons/community/plane.png",
                    TargetingType.PUBLIC,
                    "TRAVEL");
        }
        
        community = Community.findByTargetingTypeTargetingInfo(TargetingType.PUBLIC, "PETS");
        if (community == null) {
            String name = "寵物好朋友♥";
            String desc = "寵物好朋友♥";
            createTargetingCommunity(name, desc, 
                    "/assets/app/images/general/icons/community/cat.png",
                    TargetingType.PUBLIC,
                    "PETS");
        }
        
        community = Community.findByTargetingTypeTargetingInfo(TargetingType.PUBLIC, "BABY_SAFETY");
        if (community == null) {
            String name = "安全知多D♥";
            String desc = "安全知多D♥";
            createTargetingCommunity(name, desc, 
                    "/assets/app/images/general/icons/community/helmet.png",
                    TargetingType.PUBLIC,
                    "BABY_SAFETY");
        }
        
        logger.underlyingLogger().info("Assign system communities to all users...");
        
        Query q = JPA.em().createQuery("SELECT u FROM User u where system = ?1 and deleted = false");
        q.setParameter(1, false);
        List<User> users = (List<User>)q.getResultList();
        if (users != null) {
            for (User user : users) {
                CommunityTargetingEngine.assignSystemCommunitiesToUser(user);
            }
        }
        
        return true;
    }
    
    private static Community createTargetingCommunity(String name, String desc, 
            String icon, TargetingType targetingType, String targetingInfo) {
        Community community = null;
        try {
            community = Application.getMBAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, icon);
            community.system = true;
            community.excludeFromNewsfeed = false;
            community.targetingType = targetingType;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(new File(Resource.STORAGE_PATH + "/default/beans_cover.jpg"));
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }
}