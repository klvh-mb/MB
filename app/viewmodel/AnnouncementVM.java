package viewmodel;

import models.Announcement;

import org.codehaus.jackson.annotate.JsonProperty;

public class AnnouncementVM {
    @JsonProperty("id") public long id;
    @JsonProperty("t") public String title;
    @JsonProperty("d") public String description;
    @JsonProperty("ic") public String icon;
    @JsonProperty("ty") public String type;

    public AnnouncementVM(Announcement announcement) {
        this.id = announcement.id;
        this.title = announcement.title;
        this.description = announcement.description;
        this.icon = announcement.icon;
        if (announcement.location != null)
            this.type = announcement.location.locationType.name();
    }
    
	public AnnouncementVM(Long id, String title, String description, String icon, String type) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.icon = icon;
		this.type = type;
	}
}
