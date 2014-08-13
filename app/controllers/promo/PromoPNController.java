package controllers.promo;

import java.util.ArrayList;
import java.util.List;

import controllers.PreNurseryController;
import models.Community;
import models.TargetingSocialObject.TargetingType;
import play.Play;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.promo.PromoCommunitiesWidgetChildVM;

public class PromoPNController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(PromoPNController.class);

    public static final int MB_PROMO_PN_COMMID_HK = 
            Play.application().configuration().getInt("mb.promo.pn.commid.hk");
    public static final int MB_PROMO_PN_COMMID_KL = 
            Play.application().configuration().getInt("mb.promo.pn.commid.kl");
    public static final int MB_PROMO_PN_COMMID_NT = 
            Play.application().configuration().getInt("mb.promo.pn.commid.nt");
    public static final int MB_PROMO_PN_COMMID_IS = 
            Play.application().configuration().getInt("mb.promo.pn.commid.is");
    
    @Transactional
    public static Result getPNCommunities() {
        List<Community> pnCommunities = Community.findByTargetingType(TargetingType.PRE_NURSERY);
        
        final List<PromoCommunitiesWidgetChildVM> pnCommunityVMs = new ArrayList<>();
        for (Community community : pnCommunities) {
            PromoCommunitiesWidgetChildVM vm = new PromoCommunitiesWidgetChildVM(community);
            pnCommunityVMs.add(vm);
        }

        return ok(Json.toJson(pnCommunityVMs));
    }
    
    @Transactional
    public static Result getPNs(String region) {
        return PreNurseryController.getPNs((long)getPNCommunityId(region));
    }
    
    private static int getPNCommunityId(String region) {
        if ("hk".equalsIgnoreCase(region)) {
            return MB_PROMO_PN_COMMID_HK;
        } else if ("kl".equalsIgnoreCase(region)) {
            return MB_PROMO_PN_COMMID_KL;
        } else if ("nt".equalsIgnoreCase(region)) {
            return MB_PROMO_PN_COMMID_NT;
        } else if ("is".equalsIgnoreCase(region)) {
            return MB_PROMO_PN_COMMID_IS;
        }
        return MB_PROMO_PN_COMMID_HK;
    }
}
