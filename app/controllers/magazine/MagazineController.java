package controllers.magazine;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class MagazineController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(MagazineController.class);

    @Transactional
    public static Result redirect() {
        return index();
    }
    
	@Transactional
	public static Result index() {
        logger.underlyingLogger().info("STS MagazineController index()");
        return ok(views.html.magazine.home.render());
    }
}
