package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Announcement;
import models.Announcement.AnnouncementType;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.AnnouncementVM;

public class AnnouncementController extends Controller {
	
	@Transactional
	public static Result getGeneralAnnouncements() {
		return ok(Json.toJson(getAnnouncements(AnnouncementType.GENERAL)));
	}
	
	@Transactional
    public static Result getTopAnnouncements() {
	    List<AnnouncementVM> announcementVMs = getAnnouncements(AnnouncementType.TOP_INFO);
	    announcementVMs.addAll(getAnnouncements(AnnouncementType.TOP_ALERT));
	    return ok(Json.toJson(announcementVMs));
    }
	
	@Transactional
    public static List<AnnouncementVM> getAnnouncements(AnnouncementType announcementType) {
        List<AnnouncementVM> announcementVMs = new ArrayList<>();
        for (Announcement announcement : Announcement.getAnnouncements(announcementType)) {
            announcementVMs.add(new AnnouncementVM(announcement));
        }
        return announcementVMs;
    }
}
