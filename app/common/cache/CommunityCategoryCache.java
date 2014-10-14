package common.cache;

import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.CommunityCategory;
import viewmodel.CommunityCategoryMapVM;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/7/14
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommunityCategoryCache {
    // Permanent cache loaded up on system startup.

    private static List<CommunityCategory> bizCatList;
    private static List<CommunityCategory> socialCatList;

    private static List<CommunityCategoryMapVM> socialCommCategoryMapVMs = new ArrayList<>();

    static {
        bizCatList = CommunityCategory.loadAllBusinessCategories();
        socialCatList = CommunityCategory.loadAllSocialCategories();
        loadSocialCommCategoryMapVMs();
    }

    private static void loadSocialCommCategoryMapVMs() {
        List<CommunityCategory> socialCats = getAllSocialCategories();

        for(CommunityCategory socialCat : socialCats) {
            CommunityCategoryMapVM vm =
                    CommunityCategoryMapVM.communityCategoryMapVM(
                            socialCat, Community.findByCategory(socialCat));
            socialCommCategoryMapVMs.add(vm);
        }
    }


    //////////////// Cache Getters ////////////////
    public static List<CommunityCategory> getAllBusinessCategories() {
		return bizCatList;
	}

    public static List<CommunityCategory> getAllSocialCategories() {
		return socialCatList;
	}

    public static List<CommunityCategoryMapVM> getSocialCommCategoryMapVMs() {
        return socialCommCategoryMapVMs;
    }
}
