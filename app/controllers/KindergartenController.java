package controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

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
        return ok();
    }

    @Transactional
	public static Result searchKGsByName(String nameSubStr) {
        return ok();
    }

    @Transactional
	public static Result getKGsByRegion(Long regionId) {
        return ok();
    }

    @Transactional
	public static Result getKGsByDistrict(Long districtId) {
        return ok();
    }

    @Transactional
	public static Result getKGOrgsByDistrict(Long districtId) {
        return ok();
    }

    @Transactional
    public static Result onBookmark(Long id) {
        return ok();
    }

    @Transactional
    public static Result onUnBookmark(Long id) {
        return ok();
    }

    @Transactional
	public static Result getBookmarkedKGs() {
        return ok();
    }
}
