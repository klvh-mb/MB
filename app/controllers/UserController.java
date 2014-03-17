package controllers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UserController extends Controller {
	
	@Transactional(readOnly=true)
	public static Result getUserInfo() {
		final User localUser = Application.getLocalUser(session());
		
		return ok(Json.toJson(localUser));
	}
	
	@Transactional(readOnly=true)
	public static Result aboutUser() {
		final User localUser = Application.getLocalUser(session());
		
		return ok(Json.toJson(localUser));
	}
	
	@Transactional
	public static Result updateUserDisplayName() {
		Form<String> form = DynamicForm.form(String.class).bindFromRequest();
		String displayName = form.data().get("displayName");
		final User localUser = Application.getLocalUser(session());
		localUser.displayName = displayName;
		localUser.name = displayName;
		localUser.merge();
		return ok("true");
	}
	
	@Transactional
	public static Result uploadProfilePhoto() {
		final User localUser = Application.getLocalUser(session());
		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();
	    String contentType = picture.getContentType(); 
	    File file = picture.getFile();
	    File fileTo = new File(fileName);
	    
	    try {
	    	FileUtils.copyFile(file, fileTo);
			localUser.setPhotoProfile(fileTo);
		} catch (IOException e) {
			e.printStackTrace();
			return status(500);
		}
		return ok();
	}
	
	@Transactional
	public static Result getProfileImage() {
		final User localUser = Application.getLocalUser(session());
		if(localUser.getPhotoProfile() != null) {
			return ok(localUser.getPhotoProfile().getRealFile());
		}
		return ok("No Image");
	}

	@Transactional
	public static Result updateUserProfileData() {
		Form<User> form = DynamicForm.form(User.class).bindFromRequest("firstName","lastName","gender", "aboutMe", "date_of_birth");
		User userForUpdation = form.get();
		final User localUser = Application.getLocalUser(session());
		localUser.firstName = userForUpdation.firstName;
		localUser.lastName = userForUpdation.lastName;
		localUser.gender = userForUpdation.gender;
		localUser.aboutMe = userForUpdation.aboutMe;
		localUser.date_of_birth = userForUpdation.date_of_birth;
		localUser.merge();
		return ok("true");
	}
}
