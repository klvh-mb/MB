package targeting.sc;

import common.model.TargetGender;
import common.model.TargetProfile;
import models.Article;
import targeting.Scorable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 1/6/14
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleScorer {

    private static final int DISTRICT_POINTS = 100;
    private static final int PARENT_GENDER_POINTS = 85;
    private static final int CHILDREN_GENDER_POINTS = 70;
    private static final int LIKES_POINTS = 50;
    private static final int PUB_DATE_POINTS = 50;


    public static List<Scorable<Article>> markScores(TargetProfile profile, List<Article> articles) {
        List<Scorable<Article>> results = new ArrayList<>();

        // get max likes count
        int maxLikes = 0;
        for (Article article : articles) {
            if (article.noOfLikes > maxLikes) {
                maxLikes = article.noOfLikes;
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

            long nowMs = System.currentTimeMillis();
            long diffMs = nowMs - article.publishedDate.getTime();
            double percent = (1d - ((double) diffMs / (double) nowMs));
            score += percent * PUB_DATE_POINTS;

            Scorable<Article> scorable = new Scorable<>(score, article);
            results.add(scorable);
        }

        return results;
    }
}
