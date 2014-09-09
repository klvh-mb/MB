package controllers;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.PrivacyVM;
import models.Privacy;
import models.User;

public class PrivacySettingsController extends Controller {

	@Transactional
    public static Result saveSettings() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        	System.out.println("called......................................"+form.get("activity")+form.get("joinedCommunity")+form.get("friendList")+form.get("detail"));
           
        	final User localUser = Application.getLocalUser(session());
        	
        	try {
        			Privacy privacy = Privacy.findByUserId(localUser.id);
	        		privacy.showActivitiesTo = Integer.parseInt(form.get("activity"));
	            	privacy.showJoinedcommunitiesTo = Integer.parseInt(form.get("joinedCommunity"));
	            	privacy.showFriendListTo = Integer.parseInt(form.get("friendList"));
	            	privacy.showDetailsTo = Integer.parseInt(form.get("detail"));
	            	privacy.merge();
        	} catch(NullPointerException e) {
        			Privacy privacy = new Privacy();
	        		privacy.user = localUser;
	        		privacy.showActivitiesTo = Integer.parseInt(form.get("activity"));
	            	privacy.showJoinedcommunitiesTo = Integer.parseInt(form.get("joinedCommunity"));
	            	privacy.showFriendListTo = Integer.parseInt(form.get("friendList"));
	            	privacy.showDetailsTo = Integer.parseInt(form.get("detail"));
	            	privacy.save();
        	}
        	
        return ok();
	} 
	
	@Transactional
    public static Result getSettings() {
		final User localUser = Application.getLocalUser(session());
		Privacy privacy = Privacy.findByUserId(localUser.id);
		PrivacyVM vm = new PrivacyVM(privacy);
		return ok(Json.toJson(vm));
	}
	
}