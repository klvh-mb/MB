package tagword;

import common.cache.TagWordCache;
import domain.SocialObjectType;
import models.Article;
import models.ArticleCategory;
import models.TagWord;
import models.TagWordScore;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 18/10/14
 * Time: 7:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class TaggingEngine {
    private static play.api.Logger logger = play.api.Logger.apply(TaggingEngine.class);

    @Transactional
	public static void indexTagWords() {
        final List<TagWord> soonMomTagWords = TagWord.getTagWordsByCategory(
                TagWord.TagCategory.ARTICLE,
                ArticleCategory.ArticleCategoryGroup.SOON_TO_BE_MOMS_ARTICLES.name());

        for (TagWord tagWord : soonMomTagWords) {
            String[] keywords = tagWord.matchingWords.split(",");

            final Map<Long, Integer> articleScores = new HashMap<>();

            for (String keyword : keywords) {
                keyword = keyword.trim();

                List<Article> unscoredArticles =
                        getArticlesWithKeyword(tagWord, keyword,
                                ArticleCategory.ArticleCategoryGroup.SOON_TO_BE_MOMS_ARTICLES);

                for (Article article : unscoredArticles) {
                    int kwScore = ArticleTaggingScorer.computeScore(keyword, article);
                    if (kwScore > 0) {
                        Integer twScore = articleScores.get(article.id);
                        if (twScore != null) {
                            articleScores.put(article.id, twScore + kwScore);
                        } else {
                            articleScores.put(article.id, kwScore);
                        }
                    }
                }
            }

            for (Long articleId : articleScores.keySet()) {
                TagWordScore.createTagWordScore(tagWord.id,
                        SocialObjectType.ARTICLE,
                        articleId,
                        articleScores.get(articleId));
            }
            tagWord.updateSocialObjectCount(SocialObjectType.ARTICLE);

            logger.underlyingLogger().info("Indexed articles for tagword["+tagWord.id+"]. count="+articleScores.size());
        }

        // refresh cache
        TagWordCache.refresh();
	}


    private static List<Article> getArticlesWithKeyword(TagWord tagWord,
                                                        String keyword,
                                                        ArticleCategory.ArticleCategoryGroup categoryGroup) {
     	Query q = JPA.em().createQuery("select a from Article a " +
		        "where a.category.id in (select c.id from ArticleCategory c where c.categoryGroup = ?1) "+
                "and a.deleted = false "+
                "and (a.name like '%"+keyword+"%' or a.description like '%"+keyword+"%') "+
                "and a.id not in (select ts.socialObjectId from TagWordScore ts where ts.tagWordId = ?2 and ts.socialObjectType = ?3)");
		q.setParameter(1, categoryGroup);
        q.setParameter(2, tagWord.id);
        q.setParameter(3, SocialObjectType.ARTICLE);
		return (List<Article>)q.getResultList();
    }
}
