package targeting.community;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.mnt.exception.SocialObjectNotJoinableException;

import common.model.TargetProfile;
import common.model.TargetYear;
import models.Community;
import models.Location;
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
        
        if (targetProfile.getLocation() != null) {
            Location district = Location.getParentLocation(targetProfile.getLocation(), Location.LocationType.DISTRICT);
            Community community = Community.findByTargetingTypeTargetingInfo(
                    TargetingSocialObject.TargetingType.LOCATION_DISTRICT, district.id.toString());
            assign(community, user);
        }

        // PreNursery communities
        if (targetProfile.isPreNurseryApplicable()) {
            Location region = Location.getParentLocation(targetProfile.getLocation(), Location.LocationType.REGION);
            if (region != null) {
                Community community = Community.findByTargetingTypeTargetingInfo(
                    TargetingSocialObject.TargetingType.PRE_NURSERY, region.id.toString());
                if (community != null) {
                    assign(community, user);
                }
            }
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
