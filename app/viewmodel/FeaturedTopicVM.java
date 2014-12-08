package viewmodel;

import java.util.Date;

import models.FeaturedTopic;

import org.codehaus.jackson.annotate.JsonProperty;

public class FeaturedTopicVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("pd") public Date publishedDate;
	@JsonProperty("img") public String image;
	@JsonProperty("url") public String url;
	@JsonProperty("nc") public int noClicks;
	@JsonProperty("ty") public String featuredType;
	@JsonProperty("ac") public boolean active;

	public FeaturedTopicVM(FeaturedTopic topic) {
		this.id = topic.id;
		this.name = topic.name;
		this.description = topic.description;
		this.publishedDate = topic.publishedDate;
		this.image = topic.image;
		this.url = topic.url;
		this.noClicks = topic.noClicks;
		this.featuredType = topic.featuredType.name();
		this.active = topic.active;
	}
}
