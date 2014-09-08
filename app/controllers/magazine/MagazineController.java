package controllers.magazine;

import common.utils.UserAgentUtil;

import controllers.Application;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class MagazineController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(MagazineController.class);

    @Transactional
    public static Result mobile() {
        Application.setMobileUser();    // manually set mobile to true
        logger.underlyingLogger().info("STS MagazineController mobile()");
        return ok(views.html.mobile.magazine.home.render());
    }
    
	@Transactional
	public static Result index() {
        logger.underlyingLogger().info("STS MagazineController index()");

        UserAgentUtil userAgentUtil = new UserAgentUtil(request());
        boolean isMobile = userAgentUtil.isMobileUserAgent();
        
        Application.setMobileUser(isMobile? "true":"false");
        
        if (isMobile) {
            return ok(views.html.mobile.magazine.home.render());
        }
        return ok(views.html.magazine.home.render());
    }
}
