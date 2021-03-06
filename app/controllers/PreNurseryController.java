package controllers;

import common.utils.NanoSecondStopWatch;
import models.Community;
import models.PreNursery;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CommunitiesParentVM;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.PreNurseryVM;
import viewmodel.StringVM;

import java.util.ArrayList;
import java.util.List;

import domain.DefaultValues;

/**
 * Created by IntelliJ IDEA.
 * Date: 26/7/14
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreNurseryController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PreNurseryController.class);

    @Transactional
	public static Result getPNInfo(Long id) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
		final User localUser = Application.getLocalUser(session());
        PreNursery pn = PreNursery.findById(id);
        if (pn == null) {
            return notFound();
        }
        pn.noOfViews++;

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][id="+id+"] getPN. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(new PreNurseryVM(pn, localUser)));
    }
    
    @Transactional
	public static Result searchPNsByName(String nameSubStr) {
        final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.searchByName(nameSubStr);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        if (pns.size() > DefaultValues.MAX_SCHOOLS_SEARCH_COUNT) {
        	for (PreNursery pn : pns) {
                pnVMs.add(null);
            }
        } else {
        	for (PreNursery pn : pns) {
                pnVMs.add(new PreNurseryVM(pn, localUser));
            }	
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
	public static Result getPNOrgsByDistrict(Long districtId) {
        List<String> orgs = PreNursery.getOrganizationsByDistrict(districtId);

        final List<StringVM> vms = new ArrayList<>();
        for (String org : orgs) {
            vms.add(new StringVM(org));
        }
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopViewedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getTopViews(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopViewedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getTopDiscussedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getTopDiscussed(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopDiscussedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getTopBookmarkedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getTopBookmarked(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopBookmarkedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }

    @Transactional
    public static Result onBookmark(Long id) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        PreNursery pn = PreNursery.findById(id);
        if (pn != null) {
            pn.onBookmarkedBy(localUser);
        }
        return ok();
    }

    @Transactional
    public static Result onUnBookmark(Long id) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        PreNursery pn = PreNursery.findById(id);
        if (pn != null) {
            pn.onUnBookmarkedBy(localUser);
        }
        return ok();
    }

    @Transactional
	public static Result getBookmarkedPNCommunities() {
    	final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
        	return ok(Json.toJson(new ArrayList<PreNurseryVM>()));
        }
    	
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
        List<PreNursery> pns = PreNursery.getBookmarked(localUser.getId());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        for (PreNursery pn : pns) {
        	Community community = Community.findById(pn.communityId);
        	communityList.add(new CommunitiesWidgetChildVM(community, localUser));
        }
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(communityList.size(), communityList);
        
        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getBookmarkedPNCommunities. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
	public static Result getBookmarkedPNs() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
        	return ok(Json.toJson(new ArrayList<PreNurseryVM>()));
        }

        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
        List<PreNursery> pns = PreNursery.getBookmarked(localUser.getId());
        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser, true));   // must be Bookmarked
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getBookmarkedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
    
    @Transactional
	public static Result getFormReceivedPNs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getFormReceived(localUser.getId());

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
        List<PreNursery> pns = PreNursery.getApplied(localUser.getId());

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
        List<PreNursery> pns = PreNursery.getInterviewed(localUser.getId());

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
        List<PreNursery> pns = PreNursery.getOffered(localUser.getId());

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getOfferedPNs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }

    @Transactional
    public static Result getAppDates() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getWithApplicationDates();

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }
        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] PN getApplicationDates. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }

    @Transactional
	public static Result getAppDatesByDistrict(Long districtId) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getWithApplicationDatesDistrict(districtId);

        final List<PreNurseryVM> pnVMs = new ArrayList<>();
        for (PreNursery pn : pns) {
            pnVMs.add(new PreNurseryVM(pn, localUser));
        }
        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] PN getApplicationDatesByDistrict. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(pnVMs));
    }
}
