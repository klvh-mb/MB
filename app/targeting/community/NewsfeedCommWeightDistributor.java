package targeting.community;

import models.Community;
import models.UserCommunityAffinity;
import org.joda.time.LocalDate;
import targeting.Scorable;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/6/14
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewsfeedCommWeightDistributor {
    private static final play.api.Logger logger = play.api.Logger.apply(NewsfeedCommWeightDistributor.class);

    /**
     * @param userId
     * @param newsfeedFullLen
     * @return
     */
    public static DistributionResult process(Long userId, int newsfeedFullLen) {
        final DistributionResult result = new DistributionResult();

        // get list of communities with affinity info for user (only active and comm not deleted)
        List<UserCommunityAffinity> affinities = UserCommunityAffinity.findSocialFeedCommunitiesByUser(userId);

        // mark scores for each community
        List<Scorable<UserCommunityAffinity>> scores = NewsfeedCommAffinityScorer.markScores(affinities);

        int totalScore = 0;
        for (Scorable<UserCommunityAffinity> scorable : scores) {
            totalScore += scorable.getScore();
        }

        final int totalToFetch = (int) (newsfeedFullLen * 1.2d);    // fetch 20% more
        for (Scorable<UserCommunityAffinity> scorable : scores) {
            UserCommunityAffinity affinity = scorable.getObject();

            if (affinity.isNewsfeedEnabled()) {
                // each community fetch count based on ratio of scores
                int entriesCount = (totalScore == 0) ? 0 : totalToFetch * scorable.getScore() / totalScore;
                result.setEntriesCount(affinity.getCommunityId(), entriesCount);
            }
        }

        logger.underlyingLogger().info("[u="+userId+"] Target fetch count: "+result);
        return result;
    }


    public static class DistributionResult {
        // (community id, entries count)
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

    /**
     * @param userId
     * @return
     */
    public static List<Long> getNfCommunities(Long userId) {
        final List<Long> result = new ArrayList<>();

        // get list of communities with affinity info for user (only active and comm not deleted)
        List<UserCommunityAffinity> affinities = UserCommunityAffinity.findSocialFeedCommunitiesByUser(userId);

        for (UserCommunityAffinity affinity : affinities) {
            // only if user has subscribed
            if (affinity.isNewsfeedEnabled()) {
                result.add(affinity.getCommunityId());
            }
        }

        logger.underlyingLogger().info("[u="+userId+"] Target fetch communities: "+result);
        return result;
    }

    /**
     * @return
     */
    public static List<Long> getNfCommunitiesNoLogin() {
        LocalDate updatedSince = (new LocalDate()).minusDays(21);   // updated in the last 3 weeks

        List<Long> result = Community.findSocialOpenCommIdsForNf(updatedSince);

        logger.underlyingLogger().info("[u=-1] NoLogin Target fetch communities: "+result);
        return result;
    }
}
