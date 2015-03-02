package common.cache;

import java.util.*;

import models.Community;
import models.CommunityCategory;
import models.PreNursery;
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
    // PreNursery community id to PN id
    private static Map<Long,Long> pnCommunityToIds = new HashMap<>();

    static {
        bizCatList = CommunityCategory.loadAllBusinessCategories();
        socialCatList = CommunityCategory.loadAllSocialCategories();
        loadSocialCommCategoryMapVMs();
        loadPreNurseryComms();
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

    public static void loadPreNurseryComms() {
        List<PreNursery> pns = PreNursery.findAll();

        pnCommunityToIds.clear();
        for (PreNursery pn : pns) {
            if (pn.communityId != null) {
                pnCommunityToIds.put(pn.communityId, pn.getId());
            }
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

    public static Long getPNIdFromCommunity(Long communityId) {
        return pnCommunityToIds.get(communityId);
    }
}
