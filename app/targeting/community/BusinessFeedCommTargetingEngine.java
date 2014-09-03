package targeting.community;

import common.collection.Pair;
import common.system.config.ConfigurationKeys;
import common.utils.NanoSecondStopWatch;
import models.Community;
import play.Play;
import processor.FeedProcessor;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 27/8/14
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class BusinessFeedCommTargetingEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(BusinessFeedCommTargetingEngine.class);

    // Configurations for news feed
    public static final int NEWSFEED_FULLLENGTH = Play.application().configuration().getInt(ConfigurationKeys.NEWSFEED_FULLLENGTH_PROP, 120);


     /**
     * @param userId
     */
    public static void indexBusinessNewsfeedForUser(Long userId, Long commCategoryId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        // get list of business comm ids
        List<Long> bizCommIds;
        if (commCategoryId == null || commCategoryId == 0) {
            bizCommIds = Community.findIdsByCommunityType(Community.CommunityType.BUSINESS);
        } else {
            bizCommIds = Community.findBusinessCommIdsByCategory(commCategoryId);
        }

        PostDistributionTracker distTracker = new PostDistributionTracker();

        for (Long commId : bizCommIds) {
            // targeted count
            int commCount = NEWSFEED_FULLLENGTH;
            // real posts
            LinkedList<Tuple> commPosts = FeedProcessor.getCommunityMostRecentPosts(commId, commCount);

            if (commPosts.size() > 0) {
                distTracker.addCommunity(commId, commPosts);  // pass real posts to distribution tracker
            }
        }

        final List<String> nfPostIds = new ArrayList<>();

        while (nfPostIds.size() < NEWSFEED_FULLLENGTH) {
            Pair<Long, Tuple> postPair = distTracker.peekLatest(null);

            if (postPair != null) {
                nfPostIds.add(postPair.second.getElement());
                distTracker.removeLatest(postPair.first);
            }
            else {
                logger.underlyingLogger().info("[u="+userId+"] Business_NF_size="+nfPostIds.size());
                break;
            }
        }

        // Refresh with result
        FeedProcessor.refreshBusinessCommunityFeed(userId, nfPostIds);

        sw.stop();
        logger.underlyingLogger().info("[u="+userId+"] indexBusinessNewsfeedForUser - end. Took "+sw.getElapsedMS()+"ms");
	}
}
