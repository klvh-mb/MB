package targeting.sc;

import common.model.TargetGender;
import common.model.TargetProfile;
import models.Article;
import models.Location;
import models.User;

import play.db.jpa.JPA;
import targeting.Scorable;
import targeting.ScoreSortedList;

import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/5/14
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleTargetingEngine {
    private static final play.api.Logger logger = play.api.Logger.apply(ArticleTargetingEngine.class);

    /**
     * Children Age targeting
     * - Article.max >= child.min && Article.min <= child.max
     * 1) 2x OK:    A.min  < 12m   < A.max     < 36m
     * 2) 2x OK:    12m    < A.min < 36m       < A.max
     * 3) 1x OK:    A.min  < 12m   < A.max
     * 4) 2x OK:    A.min  < 12m   < 36m       < A.max
     * 5) 2x OK:    12m    < A.min < A.max     < 36m   (not perfect, but good for majority)
     * 6) Filter:   3m     < 6m    < A.min     < A.max
     */

    public static List<Article> getTargetedArticles(User user, int k) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }

        TargetProfile profile = TargetProfile.fromUser(user);
        logger.underlyingLogger().info("[u="+user.getId()+"] getTargetedArticles. "+profile);

        List<Article> unRankedRes = query(profile, false);
        if (unRankedRes.size() < k) {
            unRankedRes = query(profile, true);
        }

        return rankAndFunnel(profile, unRankedRes, k);
    }

    static List<Article> rankAndFunnel(TargetProfile profile, List<Article> unRankedRes, int k) {
        List<Article> results = new ArrayList<>();

        List<Scorable<Article>> scoredRes = ArticleScorer.markScores(profile, unRankedRes);
        ScoreSortedList<Article> scoreSortedList = new ScoreSortedList<>(scoredRes);

        for (Scorable<Article> scorable : scoreSortedList.greatestOf(k)) {
            results.add(scorable.getObject());
        }

        logger.underlyingLogger().info("[rankAndFunnel] results["+results.size()+"]");
        return results;
    }

    static List<Article> query(TargetProfile profile, boolean skipChildrenAge) {
        StringBuilder sb = new StringBuilder();
        sb.append("Select a from Article a ");

        String whereDelim = "where ", andDelim = "";
        int paramCount = 1;
        List<Object> paramValues = new ArrayList<Object>();

        // parent
        if (profile.getParentGender() != null && profile.getParentGender() != TargetGender.Both) {
            TargetGender oppositeGender = profile.getParentGender().getOppositeGender();

            sb.append(whereDelim).append(andDelim).append("targetParentGender <> ?").append(paramCount).append(" ");
            paramValues.add(oppositeGender.getCode());
            whereDelim = "";
            andDelim = "and ";
            paramCount++;
        }

        // location - should get all articles targeted to parent location as well
        // e.g. user in [DISTRICT|南區], include articles targeted for [REGION|香港島], [CITY|香港]...
        if (profile.getLocation() != null) {
            // user location
            sb.append(whereDelim).append(andDelim).append("(targetLocation_id = ?").append(paramCount).append(" ");
            paramValues.add(profile.getLocation().id);
            paramCount++;
            for (Location parentLocation = profile.getLocation().parent; 
                    parentLocation != null; 
                    parentLocation = parentLocation.parent) {
                // parent location of user location
                sb.append("or targetLocation_id = ?").append(paramCount).append(" ");
                paramValues.add(parentLocation.id);
                paramCount++;
            }
            sb.append("or targetLocation_id is null) ");
            whereDelim = "";
            andDelim = "and ";
        }

        // children
        if (profile.getNumChildren() > 0) {
            if (profile.getChildrenGender() != null && profile.getChildrenGender() != TargetGender.Both) {
                TargetGender oppositeGender = profile.getChildrenGender().getOppositeGender();

                sb.append(whereDelim).append(andDelim).append("targetGender <> ?").append(paramCount).append(" ");
                paramValues.add(oppositeGender.getCode());
                whereDelim = "";
                andDelim = "and ";
                paramCount++;
            }

            if (!skipChildrenAge) {
                sb.append(whereDelim).append(andDelim).append("targetAgeMinMonth <= ?").append(paramCount).append(" ");
                paramValues.add(profile.getChildrenMaxAgeMonths());
                whereDelim = "";
                andDelim = "and ";
                paramCount++;

                sb.append(whereDelim).append(andDelim).append("targetAgeMaxMonth >= ?").append(paramCount).append(" ");
                paramValues.add(profile.getChildrenMinAgeMonths());
                whereDelim = "";
                andDelim = "and ";
                paramCount++;
            }
        }

        sb.append(whereDelim).append(andDelim).append("excludeFromTargeting = 0 ");

        // exec query
        Query q = JPA.em().createQuery(sb.toString());
        for (int i = 1; i < paramCount; i++) {
            q.setParameter(i, paramValues.get(i-1));
        }

        List<Article> results = (List<Article>)q.getResultList();
		return (List<Article>)q.getResultList();
    }

}
