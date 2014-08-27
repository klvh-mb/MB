package viewmodel;

import models.Announcement;

import org.codehaus.jackson.annotate.JsonProperty;

public class AnnouncementVM {
    @JsonProperty("id") public long id;
    @JsonProperty("t") public String title;
    @JsonProperty("d") public String description;
    @JsonProperty("ic") public String icon;
    @JsonProperty("url") public String url;
    @JsonProperty("ty") public String type;

    public AnnouncementVM(Announcement announcement) {
        this.id = announcement.id;
        this.title = announcement.title;
        this.description = announcement.description;
        this.icon = announcement.icon;
        this.url = announcement.url;
        this.type = announcement.announcementType.name();
    }
}
