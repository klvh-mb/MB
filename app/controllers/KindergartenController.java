package controllers;

import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;
import models.Kindergarten;
import models.PreNursery;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.KindergartenVM;
import viewmodel.PreNurseryVM;
import viewmodel.StringVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 28/3/15
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class KindergartenController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(KindergartenController.class);

    @Transactional
	public static Result getKGInfo(Long id) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        Kindergarten kg = Kindergarten.findById(id);
        if (kg == null) {
            return notFound();
        }
        kg.noOfViews++;

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][id="+id+"] getKG. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(new KindergartenVM(kg, localUser)));
    }

    @Transactional
	public static Result searchKGsByName(String nameSubStr) {
        final User localUser = Application.getLocalUser(session());
        List<Kindergarten> kgs = Kindergarten.searchByName(nameSubStr);

        final List<KindergartenVM> vms = new ArrayList<>();
        if (kgs.size() > DefaultValues.MAX_SCHOOLS_SEARCH_COUNT) {
        	for (Kindergarten kg : kgs) {
                vms.add(null);
            }
        } else {
        	for (Kindergarten kg : kgs) {
                vms.add(new KindergartenVM(kg, localUser));
            }
        }
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getKGsByRegion(Long regionId) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<Kindergarten> kgs = Kindergarten.getKGsByRegion(regionId);

        final List<KindergartenVM> vms = new ArrayList<>();
        for (Kindergarten kg : kgs) {
            vms.add(new KindergartenVM(kg, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][r="+regionId+"] getKGsByRegion. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getKGsByDistrict(Long districtId) {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<Kindergarten> kgs = Kindergarten.getKGsByDistrict(districtId);

        final List<KindergartenVM> kgVMs = new ArrayList<>();
        for (Kindergarten kg : kgs) {
        	kgVMs.add(new KindergartenVM(kg, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"][d="+districtId+"] getKGsByDistrict. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(kgVMs));
    }

    @Transactional
	public static Result getKGOrgsByDistrict(Long districtId) {
        List<String> orgs = Kindergarten.getOrganizationsByDistrict(districtId);

        final List<StringVM> vms = new ArrayList<>();
        for (String org : orgs) {
            vms.add(new StringVM(org));
        }
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopViewedKGs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<Kindergarten> kgs = Kindergarten.getTopViews(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<KindergartenVM> vms = new ArrayList<>();
        for (Kindergarten kg : kgs) {
            vms.add(new KindergartenVM(kg, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopViewedKGs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopDiscussedKGs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<Kindergarten> kgs = Kindergarten.getTopDiscussed(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<KindergartenVM> vms = new ArrayList<>();
        for (Kindergarten kg : kgs) {
            vms.add(new KindergartenVM(kg, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopDiscussedKGs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopBookmarkedKGs() {
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
        List<Kindergarten> kgs = Kindergarten.getTopBookmarked(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<KindergartenVM> vms = new ArrayList<>();
        for (Kindergarten kg : kgs) {
            vms.add(new KindergartenVM(kg, localUser));
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopBookmarkedKGs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(vms));
    }

    @Transactional
    public static Result onBookmark(Long id) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        Kindergarten kg = Kindergarten.findById(id);
        if (kg != null) {
            kg.onBookmarkedBy(localUser);
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

        Kindergarten kg = Kindergarten.findById(id);
        if (kg != null) {
            kg.onUnBookmarkedBy(localUser);
        }
        return ok();
    }

    @Transactional
	public static Result getBookmarkedKGs() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
        	return ok(Json.toJson(new ArrayList<KindergartenVM>()));
        }

        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<Kindergarten> kgs = Kindergarten.getBookmarked(localUser.getId());
        final List<KindergartenVM> vms = new ArrayList<>();
        for (Kindergarten kg : kgs) {
            vms.add(new KindergartenVM(kg, localUser, true));   // must be Bookmarked
        }

        sw.stop();
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getBookmarkedKGs. Took "+sw.getElapsedMS()+"ms");
		return ok(Json.toJson(vms));
    }
}
