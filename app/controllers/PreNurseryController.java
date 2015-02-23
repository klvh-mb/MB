package controllers;

import common.utils.NanoSecondStopWatch;
import models.PreNursery;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.PreNurseryVM;
import viewmodel.StringVM;

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
	public static Result getPNInfo(Long id) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
		final User localUser = Application.getLocalUser(session());
        PreNursery pn = PreNursery.findById(id);

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][id="+id+"] getPN. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(new PreNurseryVM(pn, localUser)));
    }
    
    @Transactional
	public static Result searchPNsByName(String nameSubStr) {
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
	public static Result getPNOrgsByDistrict(Long districtId) {
        List<String> orgs = PreNursery.getOrganizationsByDistrict(districtId);

        final List<StringVM> vms = new ArrayList<>();
        for (String org : orgs) {
            vms.add(new StringVM(org));
        }
		return ok(Json.toJson(vms));
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
		User localUser = Application.getLocalUser(session());
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
	public static Result getBookmarkedPNs() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        List<PreNursery> pns = PreNursery.getBookmarkedPNs(localUser.getId());

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


    /**
     * @deprecated
     */
    public static Result getPNCommunities() {
        return ok(Json.toJson(new ArrayList<>()));
    }

    /**
     * @deprecated
     */
	public static Result getPNsByCommunity(Long communityId) {
        return ok(Json.toJson(new ArrayList<>()));
	}
}
