package controllers.magazine;

import play.Play;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class MagazineController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(MagazineController.class);

    public static final String APPLICATION_BASE_URL = 
            Play.application().configuration().getString("application.baseUrl");
    
	@Transactional
	public static Result index() {
        logger.underlyingLogger().info("STS MagazineController index()");
        return ok(views.html.magazine.home.render());
    }
}
