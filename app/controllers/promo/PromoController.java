package controllers.promo;

import play.Play;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class PromoController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(PromoController.class);

    public static final String APPLICATION_BASE_URL = 
            Play.application().configuration().getString("application.baseUrl");
    
	@Transactional
	public static Result index() {
        logger.underlyingLogger().info("STS PromoController index()");
        //return ok(views.html.promo.home.render());
        return redirect("/frontpage");
    }
}
