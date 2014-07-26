package targeting.community;

import common.collection.Pair;
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

    private static final String NEWSFEED_FULLLENGTH_PROP = "newsfeed.fulllength";
    private static final String NEWSFEED_TIMEDISORDER_TOL_PROP = "newsfeed.timedisorder.tolerance";

    // Configurations for news feed
    public static final int NEWSFEED_FULLLENGTH = Play.application().configuration().getInt(NEWSFEED_FULLLENGTH_PROP, 120);
    public static final double NEWSFEED_TIME_TOL = Play.application().configuration().getDouble(NEWSFEED_TIMEDISORDER_TOL_PROP, 0.2d);  // 20%

    /**
     * @param userId
     */
    public static void indexCommNewsfeedForUser(Long userId) {
        logger.underlyingLogger().info("[u="+userId+"] indexCommNewsfeedForUser - start.");
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        // Get distribution
        DistributionResult distributionResult = NewsfeedCommWeightDistributor.process(userId, NEWSFEED_FULLLENGTH);

        DateTime maxTime = null, minTime = null;
        PostDistributionTracker distTracker = new PostDistributionTracker();
        RatioCalculator ratioCalculator = new RatioCalculator();

        for (Long commId : distributionResult.getCommunityIds()) {
            int commCount = distributionResult.getEntriesCount(commId);
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

                distTracker.addCommunity(commId, commPosts);
                ratioCalculator.addInput(commId, commPosts.size());
            }
        }

        long timeToleranceMs = 0;
        if (minTime != null && maxTime != null) {
            timeToleranceMs = (long) ((maxTime.getMillis() - minTime.getMillis()) * NEWSFEED_TIME_TOL);
        }
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+userId+"] Time - min="+minTime+" max="+maxTime+" tol="+(timeToleranceMs/3_600_000)+"hr");
        }

        ratioCalculator.calculate();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+userId+"] Ratio - "+ratioCalculator.getRatioMap());
        }

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
                    logger.underlyingLogger().info("[u="+userId+"] Nothing left in community post pool. NF_size="+nfPostIds.size());
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

    private static class PostDistributionTracker {
        private Map<Long, LinkedList<Tuple>> postDistMap = new HashMap<>();

        public void addCommunity(Long commId, LinkedList<Tuple> posts) {
            postDistMap.put(commId, posts);
        }

        public Pair<Long, Tuple> peekLatest(Set<Long> commIds) {
            Long retCommId = null;
            Tuple retTuple = null;

            for (Long commId : postDistMap.keySet()) {
                if (commIds == null || commIds.contains(commId)) {
                    Tuple tuple = postDistMap.get(commId).peekFirst();
                    if (tuple != null) {
                        if (retCommId == null || retTuple == null) {
                            retCommId = commId;
                            retTuple = tuple;
                        } else {
                            if (tuple.getScore() > retTuple.getScore()) {
                                retCommId = commId;
                                retTuple = tuple;
                            }
                        }
                    }
                }
            }

            if (retCommId != null) {
                return new Pair<>(retCommId, retTuple);
            } else {
                return null;
            }
        }

        public void removeLatest(Long commId) {
            postDistMap.get(commId).pollFirst();
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
            if (minLength == null || length < minLength) {
                minLength = length;
            }
        }

        public void calculate() {
            for (Long commId : ratioMap.keySet()) {
                int normalizedLen = ratioMap.get(commId) / minLength;

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
