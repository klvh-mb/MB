package common.cache;

import models.ArticleCategory;
import models.TagWord;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 24/10/14
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagWordCache {
    // Permanent cache loaded up on system startup.

    private static List<TagWord> soonMomsTagWords = Collections.EMPTY_LIST;
    private static List<TagWord> hotArticlesTagWords = Collections.EMPTY_LIST;

    static {
        refresh();
    }

    /**
     * Cache refresh.
     */
    public static void refresh() {
        soonMomsTagWords = TagWord.getTagWordsByCategoryByCount(TagWord.TagCategory.ARTICLE,
                    ArticleCategory.ArticleCategoryGroup.SOON_TO_BE_MOMS_ARTICLES.name());
        hotArticlesTagWords = TagWord.getTagWordsByCategoryByCount(TagWord.TagCategory.ARTICLE,
                    ArticleCategory.ArticleCategoryGroup.HOT_ARTICLES.name());
    }

    /**
     * Getter for soon to be moms.
     * @return
     */
    public static List<TagWord> getSoonToBeMomTagWords() {
        return soonMomsTagWords;
    }

    /**
     * Getter for hot articles.
     * @return
     */
    public static List<TagWord> getHotArticlesTagWords() {
        return hotArticlesTagWords;
    }

}
