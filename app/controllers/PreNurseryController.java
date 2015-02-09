package controllers;

import common.cache.LocationCache;
import common.utils.NanoSecondStopWatch;
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
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
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

        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.regionId = ?1 order by pn.districtId, pn.name");
        q.setParameter(1, commRegion.id);
        List<PreNursery> pns = (List<PreNursery>)q.getResultList();

        Long userDistrictId = null;
        if (User.isLoggedIn(localUser) && localUser.userInfo != null && localUser.userInfo.location != null) {
            userDistrictId = localUser.userInfo.location.id;
        }

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            boolean isMyDistrict = userDistrictId != null && userDistrictId.equals(pn.districtId);

            String districtName = LocationCache.getDistrict(pn.districtId).getDisplayName();
            PreNurseryVM vm = new PreNurseryVM(pn, localUser, isMyDistrict, districtName);
            pnVMs.add(vm);
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+id+"] getPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
	}
    
    @Transactional
	public static Result getPNsByDistrict(Long districtId) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
		final User localUser = Application.getLocalUser(session());
        
        Query q = JPA.em().createQuery("SELECT pn FROM PreNursery pn where pn.districtId = ?1 order by pn.name");
        q.setParameter(1, districtId);
        List<PreNursery> pns = (List<PreNursery>)q.getResultList();

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            boolean isMyDistrict = false;	// TODO
            String districtName = LocationCache.getDistrict(pn.districtId).getDisplayName();
            PreNurseryVM vm = new PreNurseryVM(pn, localUser, isMyDistrict, districtName);
            pnVMs.add(vm);
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][d="+districtId+"] getPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getTopViewsPNs(Long num) {
    	
    	return ok(Json.toJson(null));
    }
    
    @Transactional
	public static Result getTopBookmarkedPNs(Long num) {
    	
    	return ok(Json.toJson(null));
    }
    
    @Transactional
	public static Result getBookmarkedPNs() {
    	
    	return ok(Json.toJson(null));
    }
    
    @Transactional
	public static Result getFormReceivedPNs() {
    	
    	return ok(Json.toJson(null));
    }
    
    @Transactional
	public static Result getAppliedPNs() {
    	
    	return ok(Json.toJson(null));
    }
    
    @Transactional
	public static Result getInterviewedPNs() {
    	
    	return ok(Json.toJson(null));
    }
    
    @Transactional
	public static Result getOfferedPNs() {
    	
    	return ok(Json.toJson(null));
    }
}
