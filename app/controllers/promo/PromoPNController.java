package controllers.promo;

import java.util.ArrayList;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

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

    /**
     * @deprecated
     */
    public static Result getPNCommunities() {
        return ok(Json.toJson(new ArrayList<>()));
    }
    
    /**
     * @deprecated
     */
    public static Result getPNs(Long id) {
        return ok(Json.toJson(new ArrayList<>()));
    }
    
//    private static int getPNCommunityId(String region) {
//        if ("hk".equalsIgnoreCase(region)) {
//            return MB_PROMO_PN_COMMID_HK;
//        } else if ("kl".equalsIgnoreCase(region)) {
//            return MB_PROMO_PN_COMMID_KL;
//        } else if ("nt".equalsIgnoreCase(region)) {
//            return MB_PROMO_PN_COMMID_NT;
//        } else if ("is".equalsIgnoreCase(region)) {
//            return MB_PROMO_PN_COMMID_IS;
//        }
//        return MB_PROMO_PN_COMMID_HK;
//    }
}
