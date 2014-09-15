package common.cache;

import models.ArticleCategory;
import models.ArticleCategory.ArticleCategoryGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/7/14
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleCategoryCache {
    // Permanent cache loaded up on system startup.

    private static Map<Long, ArticleCategory> categoryMap;
    private static List<ArticleCategory> categoryList;

    static {
        categoryList = ArticleCategory.loadAllCategories();
        categoryMap = new HashMap<>();
        for (ArticleCategory cat : categoryList) {
            categoryMap.put(cat.id, cat);
        }
    }


    public static List<ArticleCategory> getAllCategories() {
		return categoryList;
	}

	public static ArticleCategory getCategoryById(long id) {
		return categoryMap.get(id);
	}

	public static List<ArticleCategory> getCategories(ArticleCategoryGroup categoryGroup) {
	    List<ArticleCategory> list = new ArrayList<ArticleCategory>();
	    for (ArticleCategory category : categoryList) {
	        if (categoryGroup == category.categoryGroup) {
	            list.add(category);
	        }
	    }
        return list;
	}
}
