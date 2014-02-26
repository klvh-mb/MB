package controllers;


import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
  
    public static Result index() {
        return ok(index.render());
    }
    
    public static Result getPost(int offset, int limit) {
    	return ok();// TODO: null check need to be added
    }
  
}
