package tagword;

import models.Article;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * Date: 19/10/14
 * Time: 11:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleTaggingScorer {
    private static final play.api.Logger logger = play.api.Logger.apply(ArticleTaggingScorer.class);

    private static final int TITLE_MATCH = 100;
    private static final int BODY_MATCH = 10;

    public static int computeScore(String keyword, Article article) {
        int score = 0;

        String title = article.name;
        String body = article.description;

        if (title != null && title.contains(keyword)) {
            score += TITLE_MATCH;
        }

        if (body != null) {
            int bodyMatchCount = StringUtils.countMatches(body, keyword);
            score += (bodyMatchCount * BODY_MATCH);
        }

        return score;
    }
}
