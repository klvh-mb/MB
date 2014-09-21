package targeting.community;

import common.collection.Pair;
import common.system.config.ConfigurationKeys;
import common.utils.NanoSecondStopWatch;
import org.elasticsearch.common.joda.time.DateTime;
import play.Play;
import processor.FeedProcessor;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static targeting.community.NewsfeedCommWeightDistributor.DistributionResult;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/6/14
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewsfeedCommTargetingEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(NewsfeedCommTargetingEngine.class);

    // Configurations for news feed
    public static final int NEWSFEED_FULLLENGTH = Play.application().configuration().getInt(ConfigurationKeys.NEWSFEED_FULLLENGTH_PROP, 120);
    public static final double NEWSFEED_TIME_TOL = Play.application().configuration().getDouble(ConfigurationKeys.NEWSFEED_TIMEDISORDER_TOL_PROP, 0.15d);  // 15%

    private static final long MAX_TIME_TOLERANCE = 3 * 24 * 60 * 60 * 1000;

    /**
     * @param userId
     */
    public static void indexCommNewsfeedForUser(Long userId) {
        // purely by last updated time (for the bootstrap phase)
        indexCommNewsfeedForUserByTime(userId);
    }

    /**
     * @param userId
     */
    private static void indexCommNewsfeedForUserByTime(Long userId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        // Get targeted distribution
        DistributionResult distributionResult = NewsfeedCommWeightDistributor.process(userId, NEWSFEED_FULLLENGTH);

        PostDistributionTracker distTracker = new PostDistributionTracker();
        RatioCalculator ratioCalculator = new RatioCalculator();

        for (Long commId : distributionResult.getCommunityIds()) {
            // targeted count
            int commCount = NEWSFEED_FULLLENGTH / 2;
            // real posts
            LinkedList<Tuple> commPosts = FeedProcessor.getCommunityMostRecentPosts(commId, commCount);

            if (commPosts.size() > 0) {
                distTracker.addCommunity(commId, commPosts);  // pass real posts to distribution tracker
                ratioCalculator.addInput(commId, commCount);  // use target count to compute ratio
            }
        }

        ratioCalculator.calculate();

        logger.underlyingLogger().info("[u="+userId+"] Normalized ratio: "+ratioCalculator.getRatioMap());


        final List<String> nfPostIds = new ArrayList<>();

        while (nfPostIds.size() < NEWSFEED_FULLLENGTH) {
            Pair<Long, Tuple> postPair = distTracker.peekLatest(null);

            if (postPair != null) {
                nfPostIds.add(postPair.second.getElement());
                distTracker.removeLatest(postPair.first);
            }
            else {
                logger.underlyingLogger().info("[u="+userId+"] Social NF_size="+nfPostIds.size());
                break;
            }
        }

        // Refresh with result
        FeedProcessor.refreshUserCommunityFeed(userId, nfPostIds);

        sw.stop();
        logger.underlyingLogger().info("[u="+userId+"] indexCommNewsfeedForUser - end. Took "+sw.getElapsedMS()+"ms");
    }

    /**
     * @param userId
     */
    private static void indexCommNewsfeedForUserByAffinity(Long userId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        // Get targeted distribution
        DistributionResult distributionResult = NewsfeedCommWeightDistributor.process(userId, NEWSFEED_FULLLENGTH);

        DateTime maxTime = null, minTime = null;
        PostDistributionTracker distTracker = new PostDistributionTracker();
        RatioCalculator ratioCalculator = new RatioCalculator();

        for (Long commId : distributionResult.getCommunityIds()) {
            // targeted count
            int commCount = distributionResult.getEntriesCount(commId);
            // real posts
            LinkedList<Tuple> commPosts = FeedProcessor.getCommunityMostRecentPosts(commId, commCount);

            if (commPosts.size() > 0) {
                DateTime curMaxTime = new DateTime((long)commPosts.peekFirst().getScore());
                DateTime curMinTime = new DateTime((long)commPosts.peekLast().getScore());

                if (maxTime == null || curMaxTime.isAfter(maxTime)) {
                    maxTime = curMaxTime;
                }
                if (minTime == null || curMinTime.isBefore(minTime)) {
                    minTime = curMinTime;
                }

                distTracker.addCommunity(commId, commPosts);  // pass real posts to distribution tracker
                ratioCalculator.addInput(commId, commCount);  // use target count to compute ratio
            }
        }

        long timeToleranceMs = 0;
        if (minTime != null && maxTime != null) {
            timeToleranceMs = Math.min(
                    MAX_TIME_TOLERANCE,
                    (long) ((maxTime.getMillis() - minTime.getMillis()) * NEWSFEED_TIME_TOL));
        }

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+userId+"] Time - min="+minTime+" max="+maxTime+" tol="+(timeToleranceMs/3_600_000)+"hr");
        }

        ratioCalculator.calculate();

        logger.underlyingLogger().info("[u="+userId+"] Normalized ratio: "+ratioCalculator.getRatioMap());

        RatioTracker ratioTracker = new RatioTracker(ratioCalculator.getRatioMap(), ratioCalculator.getTotalShares());

        final List<String> nfPostIds = new ArrayList<>();

        Set<Long> tmpCommIds = new HashSet<>();
        Tuple lastPost = null;

        while (nfPostIds.size() < NEWSFEED_FULLLENGTH) {
            // 1) try to maintain ratio
            tmpCommIds = ratioTracker.getCommIdsToFill(tmpCommIds);
            Pair<Long, Tuple> postPair = distTracker.peekLatest(tmpCommIds);

            boolean isPostInserted = false;
            if (postPair != null) {
                Tuple curPost = postPair.second;
                boolean isInTimeTolerance = (lastPost == null) || (Math.abs(curPost.getScore()-lastPost.getScore()) < timeToleranceMs);

                if (isInTimeTolerance) {
                    nfPostIds.add(curPost.getElement());
                    distTracker.removeLatest(postPair.first);
                    ratioTracker.onPostPopulated(postPair.first);

                    lastPost = curPost;
                    isPostInserted = true;
                }
            }

            // 2) go beyond ratio
            if (!isPostInserted) {
                tmpCommIds = ratioTracker.getCommIdsNoFill(tmpCommIds);
                postPair = distTracker.peekLatest(tmpCommIds);

                if (postPair != null) {
                    nfPostIds.add(postPair.second.getElement());
                    distTracker.removeLatest(postPair.first);
                    ratioTracker.onPostPopulated(postPair.first);

                    lastPost = postPair.second;
                    isPostInserted = true;
                }
            }

            // 3) simply pick the latest from what's left
            if (!isPostInserted) {
                postPair = distTracker.peekLatest(null);

                if (postPair != null) {
                    nfPostIds.add(postPair.second.getElement());
                    distTracker.removeLatest(postPair.first);
                    ratioTracker.onPostPopulated(postPair.first);

                    lastPost = postPair.second;
                }
                else {
                    logger.underlyingLogger().info("[u="+userId+"] Social NF_size="+nfPostIds.size());
                    break;
                }
            }
        }

        // Refresh with result
        FeedProcessor.refreshUserCommunityFeed(userId, nfPostIds);

        sw.stop();
        logger.underlyingLogger().info("[u="+userId+"] indexCommNewsfeedForUser - end. Took "+sw.getElapsedMS()+"ms");
	}

    private static class RatioTracker {
        private final Map<Long, AtomicInteger> ratioStatus = new HashMap<>();
        private final int totalShares;
        private final LinkedList<Long> lastCommWindow = new LinkedList<>();

        public RatioTracker(Map<Long, Integer> ratioMap, int totalShares) {
            for (Long key : ratioMap.keySet()) {
                AtomicInteger countState = new AtomicInteger(ratioMap.get(key));
                ratioStatus.put(key, countState);
            }
            this.totalShares = totalShares;
        }

        public void onPostPopulated(Long commId) {
            lastCommWindow.add(commId);
            int newCount = ratioStatus.get(commId).decrementAndGet();
            if (newCount < 0) {
                ratioStatus.get(commId).set(0);
            }

            if (lastCommWindow.size() == Math.max(1, totalShares-1)) {
                Long commIdRemoved = lastCommWindow.pollFirst();
                ratioStatus.get(commIdRemoved).incrementAndGet();
            }
        }

        public Set<Long> getCommIdsToFill(Set<Long> ret) {
            ret.clear();
            for (Long commId : ratioStatus.keySet()) {
                if (ratioStatus.get(commId).intValue() > 0) {
                    ret.add(commId);
                }
            }
            return ret;
        }

        public Set<Long> getCommIdsNoFill(Set<Long> ret) {
            ret.clear();
            for (Long commId : ratioStatus.keySet()) {
                if (ratioStatus.get(commId).intValue() <= 0) {
                    ret.add(commId);
                }
            }
            return ret;
        }
    }

    /**
     * Normalize entries count to ratio.
     */
    private static class RatioCalculator {
        private Map<Long, Integer> ratioMap = new HashMap<>();
        private int totalShares;
        private Integer minLength = null;

        public void addInput(Long commId, Integer length) {
            ratioMap.put(commId, length);
            if (length != 0 && (minLength == null || length < minLength)) { 
                minLength = length;
            }
        }

        public void calculate() {
            for (Long commId : ratioMap.keySet()) {
                int divisor = (minLength == null || minLength == 0) ? 1 : minLength; 
                int normalizedLen = ratioMap.get(commId) / divisor; 

                ratioMap.put(commId, normalizedLen);
                totalShares += normalizedLen;
            }
        }

        public int getTotalShares() {
            return totalShares;
        }

        public Map<Long, Integer> getRatioMap() {
            return ratioMap;
        }
    }
}
