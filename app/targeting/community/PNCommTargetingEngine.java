package targeting.community;

import common.collection.Pair;
import common.system.config.ConfigurationKeys;
import common.utils.NanoSecondStopWatch;
import models.Community;
import models.TargetingSocialObject;
import play.Play;
import processor.FeedProcessor;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 7/4/15
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class PNCommTargetingEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(PNCommTargetingEngine.class);

    // Configurations for news feed
    public static final int NEWSFEED_FULLLENGTH = Play.application().configuration().getInt(ConfigurationKeys.NEWSFEED_FULLLENGTH_PROP, 120);

    /**
     * PN
     */
    public static void indexPNNewsfeed() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        // get list of pn comm ids
        List<Long> pnCommIds = Community.findIdsByTargetingType(TargetingSocialObject.TargetingType.PRE_NURSERY);

        PostDistributionTracker distTracker = new PostDistributionTracker();

        for (Long commId : pnCommIds) {
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
                logger.underlyingLogger().info("PN NF_size="+nfPostIds.size());
                break;
            }
        }

        // Refresh with result
        FeedProcessor.refreshPNCommunityFeed(nfPostIds);

        sw.stop();
        logger.underlyingLogger().info("indexPNNewsfeed - end. Took "+sw.getElapsedMS()+"ms");
	}

    /**
     * KG
     */
    public static void indexKGNewsfeed() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        // get list of kg comm ids
        List<Long> kgCommIds = Community.findIdsByTargetingType(TargetingSocialObject.TargetingType.KINDY);

        PostDistributionTracker distTracker = new PostDistributionTracker();

        for (Long commId : kgCommIds) {
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
                logger.underlyingLogger().info("KG NF_size="+nfPostIds.size());
                break;
            }
        }

        // Refresh with result
        FeedProcessor.refreshKGCommunityFeed(nfPostIds);

        sw.stop();
        logger.underlyingLogger().info("indexKGNewsfeed - end. Took "+sw.getElapsedMS()+"ms");
	}
}

