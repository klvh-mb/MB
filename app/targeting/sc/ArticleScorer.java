package targeting.sc;

import common.model.TargetGender;
import common.model.TargetProfile;
import models.Article;
import targeting.Scorable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/6/14
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleScorer {
    private static final play.api.Logger logger = play.api.Logger.apply(ArticleScorer.class);

    private static final int DISTRICT_POINTS = 100;
    private static final int PARENT_GENDER_POINTS = 80;
    private static final int CHILDREN_GENDER_POINTS = 70;
    private static final int LIKES_POINTS = 50;
    private static final int PUB_DATE_POINTS = 100;
    private static final int RANDOM_POINTS = 40;        // adding in randomness


    /**
     * Mark scores by targetProfile
     * @param profile
     * @param articles
     * @return
     */
    public static List<Scorable<Article>> markScores(TargetProfile profile, List<Article> articles) {
        List<Scorable<Article>> results = new ArrayList<>();

        long nowMs = System.currentTimeMillis();

        // get max count
        int maxLikes = 0;
        long maxTime = 0;
        for (Article article : articles) {
            if (article.noOfLikes > maxLikes) {
                maxLikes = article.noOfLikes;
            }
            long diffMs = nowMs - article.publishedDate.getTime();
            if (diffMs > maxTime) {
                maxTime = diffMs;
            }
        }

        // mark score on each article
        for (Article article : articles) {
            int score = 0;

            if (profile.getLocation() != null && profile.getLocation().equals(article.targetLocation)) {
                score += DISTRICT_POINTS;
            }
            if (TargetGender.valueOfInt(article.targetParentGender) != TargetGender.Both) {
                score += PARENT_GENDER_POINTS;
            }
            if (TargetGender.valueOfInt(article.targetGender) != TargetGender.Both) {
                score += CHILDREN_GENDER_POINTS;
            }

            score += ((double)article.noOfLikes/(double) maxLikes) * LIKES_POINTS;

            long diffMs = nowMs - article.publishedDate.getTime();
            double timePercent = (1d - ((double) diffMs / (double) maxTime));
            score += timePercent * PUB_DATE_POINTS;

            double randomPercent = ThreadLocalRandom.current().nextDouble(0, 1d);
            score += randomPercent * RANDOM_POINTS;

            Scorable<Article> scorable = new Scorable<>(score, article);
            results.add(scorable);
        }

        return results;
    }


    private static final int VIEWS_POINTS = 100;
    private static final int VIEWS_PUB_DATE_POINTS = 80;

    /**
     * @param articles
     * @return
     */
    public static List<Scorable<Article>> markScoresByViewsTime(List<Article> articles) {
        List<Scorable<Article>> results = new ArrayList<>();

        long nowMs = System.currentTimeMillis();

        // get max count
        int maxViews = 0;
        long maxTime = 0;
        for (Article article : articles) {
            if (article.noOfViews > maxViews) {
                maxViews = article.noOfViews;
            }
            long diffMs = nowMs - article.publishedDate.getTime();
            if (diffMs > maxTime) {
                maxTime = diffMs;
            }
        }

        // mark score on each article
        for (Article article : articles) {
            int score = 0;

            score += ((double)article.noOfViews/(double) maxViews) * VIEWS_POINTS;

            long diffMs = nowMs - article.publishedDate.getTime();
            double timePercent = (1d - ((double) diffMs / (double) maxTime));
            score += timePercent * VIEWS_PUB_DATE_POINTS;

            Scorable<Article> scorable = new Scorable<>(score, article);
            results.add(scorable);
        }

        return results;
    }


    private static final int LIKES_PUB_DATE_POINTS = 40;

    /**
     * @param articles
     * @return
     */
    public static List<Scorable<Article>> markScoresByLikesTime(List<Article> articles) {
        List<Scorable<Article>> results = new ArrayList<>();

        long nowMs = System.currentTimeMillis();

        // get max count
        int maxLikes = 0;
        long maxTime = 0;
        for (Article article : articles) {
            if (article.noOfLikes > maxLikes) {
                maxLikes = article.noOfLikes;
            }
            long diffMs = nowMs - article.publishedDate.getTime();
            if (diffMs > maxTime) {
                maxTime = diffMs;
            }
        }

        // mark score on each article
        for (Article article : articles) {
            int score = 0;

            score += ((double)article.noOfLikes/(double) maxLikes) * LIKES_POINTS;

            long diffMs = nowMs - article.publishedDate.getTime();
            double timePercent = (1d - ((double) diffMs / (double) maxTime));
            score += timePercent * LIKES_PUB_DATE_POINTS;

            Scorable<Article> scorable = new Scorable<>(score, article);
            results.add(scorable);
        }

        return results;
    }
}
