package controllers;

import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 16/8/14
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdministrativeController extends Controller {

    public static Result privacy() {
		return badRequest(views.html.privacy.render());
	}

}
