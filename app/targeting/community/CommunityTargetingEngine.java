package targeting.community;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static Map<TargetingSocialObject.TargetingType, List<Community>> targetingTypeCommunitiesMap = new HashMap<>();
    
    private static Map<String, Community> targetingTypeTargetingInfoCommunitiesMap = new HashMap<>();
    
    public static void assignSystemCommunitiesToUsers(Long fromUserId, Long toUserId) {
    	// get all valid users
    	for (Long i = fromUserId; i <= toUserId; i++) {
    		User user = User.findById(i);
    		if (user == null || user.system || user.deleted || !user.emailValidated || user.userInfo == null || user.displayName == null)
    			continue;
    		
    		assignSystemCommunitiesToUser(user);
    	}
    }
    
    public static void assignSystemCommunitiesToUser(User user) {
        if (logger.underlyingLogger().isDebugEnabled())
            logger.underlyingLogger().debug(String.format("[u=%d] assignSystemCommunitiesToUser", user.id));
        
        TargetProfile targetProfile = TargetProfile.fromUser(user);
        if (targetProfile == null)
            return;
        
        // Default communities
        List<Community> communities = getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.ALL_MOMS_DADS);
        if (communities != null) {
            for (Community community : communities) {
                assign(community, user);
            }
        }
        
        // Soon moms
        if (user.userInfo.parentType == UserInfo.ParentType.SOON_MOM ||
                user.userInfo.parentType == UserInfo.ParentType.SOON_DAD) {
            communities = getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.SOON_MOMS_DADS);
            if (communities != null) {
                for (Community community : communities) {
                    assign(community, user);
                }
            }
            communities = getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.NEW_MOMS_DADS);
            if (communities != null) {
                for (Community community : communities) {
                    assign(community, user);
                }
            }
        }
        
        // New moms
        if (targetProfile.isNewParent()) {
            communities = getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.NEW_MOMS_DADS);
            if (communities != null) {
                for (Community community : communities) {
                    assign(community, user);
                }
            }
        }
        
        // Zodiac community
        for (TargetYear targetYear : targetProfile.getChildYears()) {
            Community community = getCommunitiesByTargetingTypeTargetingInfo(
                    TargetingSocialObject.TargetingType.ZODIAC_YEAR, targetYear.getZodiacInfo());
            assign(community, user);
        }
        
        // Districts
        /*
        if (targetProfile.getLocation() != null) {
            Location district = Location.getParentLocation(targetProfile.getLocation(), Location.LocationType.DISTRICT);
            Community community = getCommunitiesByTargetingTypeTargetingInfo(
                    TargetingSocialObject.TargetingType.LOCATION_DISTRICT, district.id.toString());
            assign(community, user);
        }
        */
        
        // PN KG community
        for (Integer childMonth : targetProfile.getChildMonths()) {
        	if (childMonth > 6 && childMonth <= 18) {
                Community community = getCommunitiesByTargetingTypeTargetingInfo(
                		TargetingSocialObject.TargetingType.PUBLIC, "PRE_NURSERY");
                assign(community, user);
            } else if (childMonth > 18 && childMonth <= 30) {
            	Community community = getCommunitiesByTargetingTypeTargetingInfo(
                		TargetingSocialObject.TargetingType.PUBLIC, "KINDY");
            	assign(community, user);
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
    
    private static List<Community> getCommunitiesByTargetingType(
    		TargetingSocialObject.TargetingType targetingType) {
    	if (!targetingTypeCommunitiesMap.containsKey(targetingType)) {
    		List<Community> communities = Community.findByTargetingType(targetingType);
    		targetingTypeCommunitiesMap.put(targetingType, communities);
    		
    		if (communities != null) {
    			logger.underlyingLogger().debug(
    					String.format("[targetingType=%s] getCommunitiesByTargetingType caches %d communities",
    							targetingType.name(),communities.size()));
    		} else {
    			logger.underlyingLogger().debug(
    					String.format("[targetingType=%s] getCommunitiesByTargetingType caches NULL communities",
    							targetingType.name()));
    		}
    	}
    	return targetingTypeCommunitiesMap.get(targetingType);
    }
    
    private static Community getCommunitiesByTargetingTypeTargetingInfo(
    		TargetingSocialObject.TargetingType targetingType, String targetingInfo) {
    	String key = targetingType.name()+"_"+targetingInfo;
    	if (!targetingTypeTargetingInfoCommunitiesMap.containsKey(key)) {
    		Community community = Community.findByTargetingTypeTargetingInfo(targetingType, targetingInfo);
    		targetingTypeTargetingInfoCommunitiesMap.put(key, community);
    		
    		if (community != null) {
	    		logger.underlyingLogger().debug(
	    				String.format("[targetingType=%s][targetingInfo=%s] getCommunitiesByTargetingTypeTargetingInfo caches community %s",
	    						targetingType.name(),targetingInfo,community.name));
    		} else {
    			logger.underlyingLogger().debug(
	    				String.format("[targetingType=%s][targetingInfo=%s] getCommunitiesByTargetingTypeTargetingInfo caches NULL community",
	    						targetingType.name(),targetingInfo));
    		}
    	}
    	return targetingTypeTargetingInfoCommunitiesMap.get(key);
    }
    
    private static void log(Community community) {
        if (logger.underlyingLogger().isDebugEnabled())
            logger.underlyingLogger().debug(
                    String.format("assign [%d|%s|%s|%s]", community.id, community.name, 
                            community.targetingType.name(), community.targetingInfo));
    }
}
