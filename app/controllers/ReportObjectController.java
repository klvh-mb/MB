package controllers;

import common.model.TargetGender;
import domain.SocialObjectType;

import models.ReportedObject;
import models.ReportedObject.categoryType;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class ReportObjectController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(ReportObjectController.class);

    @Transactional
    public static Result createReport() {
    	System.out.println("createReport");
        final User localUser = Application.getLocalUser(session());
        DynamicForm form = DynamicForm.form().bindFromRequest();
        ReportedObject reportedObject1 = new ReportedObject(form,localUser.id);
        
        return ok();
    }
    
}
