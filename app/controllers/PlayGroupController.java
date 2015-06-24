package controllers;

import domain.DefaultValues;
import models.Community;
import models.PlayGroup;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CommunitiesParentVM;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.PlayGroupVM;
import viewmodel.PreNurseryVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 20/6/15
 * Time: 2:46 PM
 */
public class PlayGroupController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PlayGroupController.class);

    @Transactional
	public static Result getPGInfo(Long id) {
		final User localUser = Application.getLocalUser(session());
        PlayGroup pg = PlayGroup.findById(id);
        if (pg == null) {
            return notFound();
        }
        pg.noOfViews++;
        logger.underlyingLogger().info("STS [u="+localUser.id+"][id="+id+"] getPG");
		return ok(Json.toJson(new PlayGroupVM(pg, localUser)));
    }

    @Transactional
	public static Result searchPGsByName(String nameSubStr) {
        final User localUser = Application.getLocalUser(session());
        List<PlayGroup> pgs = PlayGroup.searchByName(nameSubStr);

        final List<PlayGroupVM> vms = new ArrayList<>();
        if (pgs.size() > DefaultValues.MAX_SCHOOLS_SEARCH_COUNT) {
        	for (PlayGroup pg : pgs) {
                vms.add(null);
            }
        } else {
        	for (PlayGroup pg : pgs) {
                vms.add(new PlayGroupVM(pg, localUser));
            }
        }
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getPGsByDistrict(Long districtId) {
        final User localUser = Application.getLocalUser(session());
        List<PlayGroup> pgs = PlayGroup.getPGsByDistrict(districtId);

        final List<PlayGroupVM> vms = new ArrayList<>();
        for (PlayGroup pg : pgs) {
            vms.add(new PlayGroupVM(pg, localUser));
        }
        logger.underlyingLogger().info("STS [u="+localUser.id+"][d="+districtId+"] getPGsByDistrict");
        return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopViewedPGs() {
		final User localUser = Application.getLocalUser(session());
        List<PlayGroup> pgs = PlayGroup.getTopViews(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<PlayGroupVM> vms = new ArrayList<>();
        for (PlayGroup pg : pgs) {
            vms.add(new PlayGroupVM(pg, localUser));
        }
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopViewedPGs");
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopDiscussedPGs() {
		final User localUser = Application.getLocalUser(session());
        List<PlayGroup> pgs = PlayGroup.getTopDiscussed(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<PlayGroupVM> vms = new ArrayList<>();
        for (PlayGroup pg : pgs) {
            vms.add(new PlayGroupVM(pg, localUser));
        }
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopDiscussedPGs");
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getTopBookmarkedPGs() {
		final User localUser = Application.getLocalUser(session());
        List<PlayGroup> pgs = PlayGroup.getTopBookmarked(DefaultValues.TOP_SCHOOLS_RANKING_COUNT);

        final List<PlayGroupVM> vms = new ArrayList<>();
        for (PlayGroup pg : pgs) {
            vms.add(new PlayGroupVM(pg, localUser));
        }
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getTopBookmarkedPGs");
		return ok(Json.toJson(vms));
    }

    @Transactional
    public static Result onBookmark(Long id) {
		User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        PlayGroup pg = PlayGroup.findById(id);
        if (pg != null) {
            pg.onBookmarkedBy(localUser);
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

        PlayGroup pg = PlayGroup.findById(id);
        if (pg != null) {
            pg.onUnBookmarkedBy(localUser);
        }
        return ok();
    }

    @Transactional
	public static Result getBookmarkedPGs() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
        	return ok(Json.toJson(new ArrayList<PreNurseryVM>()));
        }

        List<PlayGroup> pgs = PlayGroup.getBookmarked(localUser.getId());
        final List<PlayGroupVM> vms = new ArrayList<>();
        for (PlayGroup pg : pgs) {
            vms.add(new PlayGroupVM(pg, localUser, true));   // must be Bookmarked
        }
        logger.underlyingLogger().info("STS [u="+localUser.id+"] getBookmarkedPGs");
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getBookmarkedPGCommunities() {
    	final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
        	return ok(Json.toJson(new ArrayList<PreNurseryVM>()));
        }

        List<PlayGroup> pgs = PlayGroup.getBookmarked(localUser.getId());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        for (PlayGroup pg : pgs) {
        	Community community = Community.findById(pg.communityId);
        	communityList.add(new CommunitiesWidgetChildVM(community, localUser));
        }
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(communityList.size(), communityList);

        logger.underlyingLogger().info("STS [u="+localUser.id+"] getBookmarkedPGCommunities");
		return ok(Json.toJson(communitiesVM));
    }
}
