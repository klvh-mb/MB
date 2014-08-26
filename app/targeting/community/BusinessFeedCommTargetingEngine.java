package targeting.community;

import common.collection.Pair;
import common.system.config.ConfigurationKeys;
import common.utils.NanoSecondStopWatch;
import org.elasticsearch.common.joda.time.DateTime;
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
    public static void indexBusinessNewsfeedForUser(Long userId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();


        final List<String> nfPostIds = new ArrayList<>();

        Set<Long> tmpCommIds = new HashSet<>();
        Tuple lastPost = null;

        while (nfPostIds.size() < NEWSFEED_FULLLENGTH) {
           break;
        }

        // Refresh with result
        FeedProcessor.refreshBusinessCommunityFeed(userId, nfPostIds);

        sw.stop();
        logger.underlyingLogger().info("[u="+userId+"] indexBusinessNewsfeedForUser - end. Took "+sw.getElapsedMS()+"ms");
	}
}
