package targeting.community;

import models.UserCommunityAffinity;
import org.elasticsearch.common.joda.time.DateTime;
import targeting.Scorable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/6/14
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewsfeedCommAffinityScorer {
    private static final play.api.Logger logger = play.api.Logger.apply(NewsfeedCommAffinityScorer.class);

    private static final int LAST_JOINED_3D_POINTS = 170;   // important, but exponentially less
    private static final int LAST_JOINED_1W_POINTS = LAST_JOINED_3D_POINTS - 40;
    private static final int LAST_JOINED_2W_POINTS = LAST_JOINED_1W_POINTS - 45;
    private static final int LAST_JOINED_3W_POINTS = LAST_JOINED_2W_POINTS - 55;

    private static final int ACTIVITY_COUNT_POINTS = 100;    // most important
    private static final int VIEW_COUNT_POINTS = 70;

    public static List<Scorable<UserCommunityAffinity>> markScores(List<UserCommunityAffinity> affinities) {
        List<Scorable<UserCommunityAffinity>> results = new ArrayList<>();

        Long userId = null;

        // get max count
        int maxActivities = 0, maxViews = 0;
        for (UserCommunityAffinity affinity : affinities) {
            if (affinity.isNewsfeedEnabled()) {
                userId = affinity.getUserId();

                if (maxActivities > maxActivities) {
                    maxActivities = affinity.getActivityCount();
                }
                if (affinity.getViewCount() > maxViews) {
                    maxViews = affinity.getViewCount();
                }
            }
        }

        // mark score on each community
        for (UserCommunityAffinity affinity : affinities) {
            int score = 0;

            if (affinity.isNewsfeedEnabled()) {
                if (maxActivities > 0) {
                    double percent = ((double) affinity.getActivityCount() / (double) maxActivities);
                    score += percent * ACTIVITY_COUNT_POINTS;
                }
                if (maxViews > 0) {
                    double percent = ((double) affinity.getViewCount() / (double) maxViews);
                    score += percent * VIEW_COUNT_POINTS;
                }

                DateTime now = DateTime.now();
                DateTime lastJoined = new DateTime(affinity.getLastJoined().getTime());
                if (now.minusDays(3).isBefore(lastJoined)) {
                    score += LAST_JOINED_3D_POINTS;
                }
                else if (now.minusWeeks(1).isBefore(lastJoined)) {
                    score += LAST_JOINED_1W_POINTS;
                }
                else if (now.minusWeeks(2).isBefore(lastJoined)) {
                    score += LAST_JOINED_2W_POINTS;
                }
                else if (now.minusWeeks(3).isBefore(lastJoined)) {
                    score += LAST_JOINED_3W_POINTS;
                }
            }

            Scorable<UserCommunityAffinity> scorable = new Scorable<>(score, affinity);
            results.add(scorable);
        }

        logger.underlyingLogger().info("[u="+userId+"] NewsfeedCommAffinityScore: "+results);
        return results;
    }
}
