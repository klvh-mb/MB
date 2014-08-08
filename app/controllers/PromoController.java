package controllers;

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
	    if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("index()");
        }
        return ok(views.html.promo.home.render());
    }
}
