package common.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Community;
import models.CommunityCategory;
import models.TargetingSocialObject;
import viewmodel.CommunityCategoryMapVM;

/**
 * Created by IntelliJ IDEA.
 * Date: 13/7/14
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommunityMetaCache {
    // Permanent cache loaded up on system startup.

    private static List<CommunityCategory> bizCatList;
    private static List<CommunityCategory> socialCatList;

    private static List<CommunityCategoryMapVM> socialCommCategoryMapVMs = new ArrayList<>();
    // PreNursery community ids
    private static Set<Long> pnCommunityIds = new HashSet<>();
    // Kindy community ids
    private static Set<Long> kindyCommunityIds = new HashSet<>();

    static {
        bizCatList = CommunityCategory.loadAllBusinessCategories();
        socialCatList = CommunityCategory.loadAllSocialCategories();
        loadSocialCommCategoryMapVMs();
        loadPreNurseryCommIds();
        loadKindyCommIds();
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

    private static void loadPreNurseryCommIds() {
        List<Community> comms =
                Community.findByTargetingType(TargetingSocialObject.TargetingType.PRE_NURSERY);
        for (Community comm : comms) {
            pnCommunityIds.add(comm.getId());
        }
    }

    private static void loadKindyCommIds() {
        List<Community> comms =
                Community.findByTargetingType(TargetingSocialObject.TargetingType.KINDY);
        for (Community comm : comms) {
            kindyCommunityIds.add(comm.getId());
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

    public static boolean isPreNurseryCommunity(Long id) {
        return pnCommunityIds.contains(id);
    }

    public static boolean isKindyCommunity(Long id) {
        return kindyCommunityIds.contains(id);
    }
}
