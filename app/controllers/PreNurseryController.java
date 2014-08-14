package controllers;

import common.cache.LocationCache;
import models.Community;
import models.Location;
import models.PreNursery;
import models.TargetingSocialObject.TargetingType;
import models.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.PreNurseryVM;

import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 26/7/14
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreNurseryController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PreNurseryController.class);

    private static final String SCHOOL_YEAR = "2015";

    @Transactional
    public static Result getPNCommunities() {
        final User localUser = Application.getLocalUser(session());
        List<Community> pnCommunities = Community.findByTargetingType(TargetingType.PRE_NURSERY);
        
        final List<CommunitiesWidgetChildVM> pnCommunityVMs = new ArrayList<>();
        for (Community community : pnCommunities) {
            if (!localUser.isMemberOf(community)) {
                CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(community, localUser);
                pnCommunityVMs.add(vm);
            }
        }

        return ok(Json.toJson(pnCommunityVMs));
    }
    
    @Transactional
	public static Result getPNs(Long id) {
		final User localUser = Application.getLocalUser(session());
        final Community community = Community.findById(id);
        if (community == null || community.getTargetingType() != TargetingType.PRE_NURSERY) {
            return ok(Json.toJson(new ArrayList<PreNurseryVM>()));
        }
        
        Location commRegion = null;
        if (community.targetingInfo != null) {
            Long regionId = Long.parseLong(community.targetingInfo);
            commRegion = LocationCache.getRegion(regionId);
        }

        logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+id+"] getPNs");

        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.regionId = ?1 and pn.schoolYear = ?2 order by pn.districtId, pn.name");
        q.setParameter(1, commRegion.id);
        q.setParameter(2, SCHOOL_YEAR);
        List<PreNursery> pns = (List<PreNursery>)q.getResultList();

        Long userDistrictId = null;
        if (User.isLoggedIn(localUser) && localUser.userInfo != null && localUser.userInfo.location != null) {
            userDistrictId = localUser.userInfo.location.id;
        }

        final List<PreNurseryVM> pnVMs = new ArrayList<>();

        for (PreNursery pn : pns) {
            boolean isMyDistrict = userDistrictId != null && userDistrictId.equals(pn.districtId);

            String districtName = LocationCache.getDistrict(pn.districtId).getDisplayName();
            PreNurseryVM vm = new PreNurseryVM(pn, isMyDistrict, districtName);
            pnVMs.add(vm);
        }
		return ok(Json.toJson(pnVMs));
	}
}
