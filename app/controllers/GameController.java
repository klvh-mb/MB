package controllers;

import models.User;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class GameController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(GameController.class);
    
    @Transactional
    public static Result signInForToday() {
        final User localUser = Application.getLocalUser(session());
        
        // check if user signed in for today
        
        // sign in
        
        return ok();
    }
    
}
