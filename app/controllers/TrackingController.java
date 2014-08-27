package controllers;

import models.User;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class TrackingController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(TrackingController.class);
    
    @Transactional(readOnly=true)
    public static Result track() {
        final User localUser = Application.getLocalUser(session());
        String page = request().getQueryString("page");
        String fr = request().getQueryString("fr");
        logger.underlyingLogger().info(String.format("STS [u=%d][page=%s][fr=%s] track", localUser.id, page, fr));
        return ok();
    }
}
