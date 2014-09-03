package common.cache;

import java.util.List;

import models.CommunityCategory;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/7/14
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommunityCategoryCache {
    // Permanent cache loaded up on system startup.

    private static List<CommunityCategory> categoryList;

    static {
        categoryList = CommunityCategory.loadAllCategories();
    }

    public static List<CommunityCategory> getAllCategories() {
		return categoryList;
	}
}
