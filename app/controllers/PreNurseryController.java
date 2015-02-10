package controllers;

import common.cache.LocationCache;
import common.utils.NanoSecondStopWatch;
import models.Community;
import models.Location;
import models.PreNursery;
import models.TargetingSocialObject.TargetingType;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.PreNurseryVM;

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
	public static Result getPNsByCommunity(Long communityId) {
        final Community community = Community.findById(communityId);
        if (community == null || community.getTargetingType() != TargetingType.PRE_NURSERY) {
            return ok(Json.toJson(new ArrayList<PreNurseryVM>()));
        }
        
        Location commRegion = null;
        if (community.targetingInfo != null) {
            Long regionId = Long.parseLong(community.targetingInfo);
            commRegion = LocationCache.getRegion(regionId);
        }

        return getPNsByRegion(commRegion.id);
	}

    @Transactional
	public static Result searchByName(String nameSubStr) {
        final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.searchByName(nameSubStr);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }
		return ok(Json.toJson(pnVMs));
    }

    @Transactional
	public static Result getPNsByRegion(Long regionId) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getPNsByRegion(regionId);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][r="+regionId+"] getPNsByRegion. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }

    @Transactional
	public static Result getPNsByDistrict(Long districtId) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getPNsByDistrict(districtId);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][d="+districtId+"] getPNsByDistrict. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getTopViewsPNs(Long num) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getTopViewsPNs(num);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopViewsPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getTopBookmarkedPNs(Long num) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getTopBookmarkedPNs(num);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopBookmarkedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getBookmarkedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getBookmarkedPNs(localUser.getId());

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getBookmarkedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getFormReceivedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getFormReceivedPNs(localUser.getId());

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getFormReceivedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getAppliedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getAppliedPNs(localUser.getId());

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getAppliedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getInterviewedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getInterviewedPNs(localUser.getId());

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getInterviewedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getOfferedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getOfferedPNs(localUser.getId());

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getOfferedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
}
