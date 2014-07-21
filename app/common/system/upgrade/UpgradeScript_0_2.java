package common.system.upgrade;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import play.db.jpa.JPA;
import providers.MyUsernamePasswordAuthUser;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import controllers.Application;
import models.Community;
import models.Icon;
import models.LinkedAccount;
import models.SystemVersion;
import models.TargetingSocialObject;
import models.TargetingSocialObject.TargetingType;
import models.User;
import models.Community.CommunityType;
import models.Icon.IconType;

/**
 * 1) Insert bean icons for community. 
 * 2) Create feedback community.
 * 3) Set LinkedAccount for super admin.
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_0_2 extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_0_2.class);
    
    public UpgradeScript_0_2() {
    }
    
    @Override
    public String getVersion() {
        return "0.2";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                this.getClass().getName(), 
                "1) Insert bean icons for community. 2) Create feedback community. 3) Set LinkedAccount for super admin.");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("Insert bean icons for community...");
        Icon icon = null;
        icon = new Icon("bean_orange", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_orange.png");
        icon.save();
        icon = new Icon("bean_blue", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_blue.png");
        icon.save();
        icon = new Icon("bean_green", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_green.png");
        icon.save();
        icon = new Icon("bean_red", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_red.png");
        icon.save();
        icon = new Icon("bean_yellow", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_yellow.png");
        icon.save();
        
        logger.underlyingLogger().info("Create feedback community...");
        Community feedbackCommunity = 
                Community.findByTargetingTypeTargetingInfo(TargetingType.ALL_MOMS_DADS, "FEEDBACK");
        if (feedbackCommunity == null) {
            String name = "miniBean小萌豆意見區";
            String desc = "miniBean小萌豆意見區";
            feedbackCommunity = createFeedbackCommunity(name, desc);
            
            logger.underlyingLogger().info("Assign feedback community to all users...");
            Query q = JPA.em().createQuery("SELECT u FROM User u where system = ?1 and deleted = false");
            q.setParameter(1, false);
            List<User> users = (List<User>)q.getResultList();
            if (users != null) {
                for (User user : users) {
                    feedbackCommunity.onJoinRequest(user);
                }
            }
        } else {
            logger.underlyingLogger().info("Feedback community already exists");
        }
        
        logger.underlyingLogger().info("Set LinkedAccount for super admin...");
        MySignup signup = new MySignup();
        signup.email = "minibean.hk@gmail.com";
        signup.fname = "miniBean";
        signup.lname = "HK";
        signup.password = "m1n1Bean";
        signup.repeatPassword = "m1n1Bean";
        
        MyUsernamePasswordAuthUser authUser = new MyUsernamePasswordAuthUser(signup);
        User superAdmin = Application.getSuperAdmin();
        if (CollectionUtils.isEmpty(superAdmin.linkedAccounts)) {
            superAdmin.linkedAccounts = Collections.singletonList(
                    LinkedAccount.create(authUser).addUser(superAdmin));
            superAdmin.save();
        } else {
            logger.underlyingLogger().info("LinkedAccount for SuperAdmin already exists");
        }
        
        return true;
    }
    
    private static Community createFeedbackCommunity(String name, String desc) {
        Community community = null;
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/community/beans.png");
            community.system = true;
            community.excludeFromNewsfeed = true;
            community.targetingType = TargetingSocialObject.TargetingType.ALL_MOMS_DADS;
            community.targetingInfo = "FEEDBACK";
            //community.setCoverPhoto(new File(Resource.STORAGE_PATH + "/default/beans.jpg"));
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }
}