package targeting.community;

import models.UserCommunityAffinity;
import play.Play;
import targeting.Scorable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/6/14
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewsfeedCommWeightDistributor {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

    private static final String NEWSFEED_FULLLENGTH_PROP = "newsfeed.fulllength";
    private static final int NEWSFEED_FULLLENGTH = Play.application().configuration().getInt(NEWSFEED_FULLLENGTH_PROP, 160);

    /**
     * @param userId
     * @return
     */
    public static DistributionResult process(Long userId) {
        final DistributionResult result = new DistributionResult();

        List<UserCommunityAffinity> affinities = UserCommunityAffinity.findByUser(userId);
        List<Scorable<UserCommunityAffinity>> scores = NewsfeedCommAffinityScorer.markScores(affinities);

        int totalScore = 0;
        for (Scorable<UserCommunityAffinity> scorable : scores) {
            totalScore += scorable.getScore();
        }

        final int totalToFetch = (int) (NEWSFEED_FULLLENGTH * 1.2d);
        for (Scorable<UserCommunityAffinity> scorable : scores) {
            UserCommunityAffinity affinity = scorable.getObject();

            if (affinity.isNewsfeedEnabled()) {
                int entriesCount = totalToFetch * scorable.getScore() / totalScore;
                result.setEntriesCount(affinity.getCommunityId(), entriesCount);
            }
        }

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+userId+"] NewsfeedCommWeightDistributor - process. "+result);
        }

        return result;
    }

    public static class DistributionResult {
        private Map<Long, Integer> nfCountMap = new HashMap<>();

        public int getEntriesCount(Long commId) {
            if (nfCountMap.containsKey(commId)) {
                return nfCountMap.get(commId);
            } else {
                return 0;
            }
        }

        public void setEntriesCount(Long commId, int count) {
            nfCountMap.put(commId, count);
        }

        public Set<Long> getCommunityIds() {
            return nfCountMap.keySet();
        }

        @Override
        public String toString() {
            return "DistributionResult{"+nfCountMap+"}";
        }
    }
}
