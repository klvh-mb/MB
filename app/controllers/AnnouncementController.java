package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Announcement;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.AnnouncementVM;

public class AnnouncementController extends Controller {
	
	@Transactional
	public static Result getAnnouncements() {
		final User localUser = Application.getLocalUser(session());
		int count=0;
		List<AnnouncementVM> announcementVMs = new ArrayList<>();
		for (Announcement announcement : Announcement.getAnnouncements()) {
		    announcementVMs.add(new AnnouncementVM(announcement));
			if (count == 10) {
				break;
			}
		}
		return ok(Json.toJson(announcementVMs));
	}
}
