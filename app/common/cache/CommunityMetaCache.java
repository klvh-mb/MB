package common.cache;

import java.util.*;

import models.Community;
import models.CommunityCategory;
import models.PlayGroup;
import models.PreNursery;
import models.Kindergarten;
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
    // PlayGroup community id to PG id
    private static Map<Long,Long> pgCommunityToIds = new HashMap<>();
    // PreNursery community id to PN id
    private static Map<Long,Long> pnCommunityToIds = new HashMap<>();
    // Kindergarten community id to KG id
    private static Map<Long,Long> kgCommunityToIds = new HashMap<>();

    static {
        bizCatList = CommunityCategory.loadAllBusinessCategories();
        socialCatList = CommunityCategory.loadAllSocialCategories();
        loadSocialCommCategoryMapVMs();

        loadPlayGroupComms();
        loadPreNurseryComms();
        loadKindergartenComms();
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

    public static void loadPlayGroupComms() {
        List<PlayGroup> pgs = PlayGroup.findAll();
        pgCommunityToIds.clear();
        for (PlayGroup pg : pgs) {
            if (pg.communityId != null) {
                pgCommunityToIds.put(pg.communityId, pg.getId());
            }
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

    public static void loadKindergartenComms() {
        List<Kindergarten> kgs = Kindergarten.findAll();
        kgCommunityToIds.clear();
        for (Kindergarten kg : kgs) {
            if (kg.communityId != null) {
                kgCommunityToIds.put(kg.communityId, kg.getId());
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

    public static Long getPGIdFromCommunity(Long communityId) {
        return pgCommunityToIds.get(communityId);
    }

    public static Long getPNIdFromCommunity(Long communityId) {
        return pnCommunityToIds.get(communityId);
    }

    public static Long getKGIdFromCommunity(Long communityId) {
        return kgCommunityToIds.get(communityId);
    }
}
