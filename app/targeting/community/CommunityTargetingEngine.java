package targeting.community;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.mnt.exception.SocialObjectNotJoinableException;

import common.model.TargetProfile;
import common.model.TargetYear;
import models.Community;
import models.TargetingSocialObject;
import models.User;
import models.UserInfo;

/**
 * 
 */
public class CommunityTargetingEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(CommunityTargetingEngine.class);

    public static void assignSystemCommunitiesToUser(User user) {
        if (logger.underlyingLogger().isDebugEnabled())
            logger.underlyingLogger().debug(String.format("[u=%d] assignSystemCommunitiesToUser", user.id));
        
        TargetProfile targetProfile = TargetProfile.fromUser(user);
        if (targetProfile == null)
            return;
        
        // Default communities
        List<Community> communities = Community.findByTargetingType(TargetingSocialObject.TargetingType.ALL_MOMS_DADS);
        if (communities != null) {
            for (Community community : communities) {
                assign(community, user);
            }
        }
        
        // Soon moms
        if (user.userInfo.parentType == UserInfo.ParentType.SOON_MOM ||
                user.userInfo.parentType == UserInfo.ParentType.SOON_DAD) {
            communities = Community.findByTargetingType(TargetingSocialObject.TargetingType.SOON_MOMS_DADS);
            if (communities != null) {
                for (Community community : communities) {
                    assign(community, user);
                }
            }
            communities = Community.findByTargetingType(TargetingSocialObject.TargetingType.NEW_MOMS_DADS);
            if (communities != null) {
                for (Community community : communities) {
                    assign(community, user);
                }
            }
        }
        
        // New moms
        if (targetProfile.isNewParent()) {
            communities = Community.findByTargetingType(TargetingSocialObject.TargetingType.NEW_MOMS_DADS);
            if (communities != null) {
                for (Community community : communities) {
                    assign(community, user);
                }
            }
        }
        
        // Zodiac community
        for (TargetYear targetYear : targetProfile.getChildYears()) {
            Community community = Community.findByTargetingTypeTargetingInfo(
                    TargetingSocialObject.TargetingType.ZODIAC_YEAR, targetYear.getZodiacInfo());
            assign(community, user);
        }
        
        // Districts
        /*
        if (targetProfile.getLocation() != null) {
            Location district = Location.getParentLocation(targetProfile.getLocation(), Location.LocationType.DISTRICT);
            Community community = Community.findByTargetingTypeTargetingInfo(
                    TargetingSocialObject.TargetingType.LOCATION_DISTRICT, district.id.toString());
            assign(community, user);
        }
        */
        
        // PN community
        if (targetProfile.getChildrenMinAgeMonths() > 6 && targetProfile.getChildrenMaxAgeMonths() <= 18) {
            Community community = Community.findByTargetingTypeTargetingInfo(
            		TargetingSocialObject.TargetingType.PUBLIC, "PRE_NURSERY");
            assign(community, user);
        }
        
        // KG community
        if (targetProfile.getChildrenMinAgeMonths() > 18 && targetProfile.getChildrenMaxAgeMonths() <= 30) {
        	Community community = Community.findByTargetingTypeTargetingInfo(
            		TargetingSocialObject.TargetingType.PUBLIC, "KINDY");
        	assign(community, user);
        }
    }
    
    private static void assign(Community community, User user) {
        if (community != null && !community.excludeFromTargeting && !user.isMemberOf(community)) {
            try {
                community.onJoinRequest(user);
                log(community);
            } catch (SocialObjectNotJoinableException e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            }
        }    
    }
    
    private static void log(Community community) {
        if (logger.underlyingLogger().isDebugEnabled())
            logger.underlyingLogger().debug(
                    String.format("assign [%d|%s|%s|%s]", community.id, community.name, 
                            community.targetingType.name(), community.targetingInfo));
    }
}
