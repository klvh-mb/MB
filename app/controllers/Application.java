package controllers;


import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.home;

public class Application extends Controller {
  
    public static Result index() {
        return ok(home.render());
    }
    
    public static Result getPost(int offset, int limit) {
    	return ok();// TODO: null check need to be added
    }
    
    
  
}
