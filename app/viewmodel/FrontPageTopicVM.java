package viewmodel;

import java.util.Date;

import models.FrontPageTopic;
import models.PKViewMeta;
import models.Post;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import common.collection.Pair;

public class FrontPageTopicVM {
	@JsonProperty("id") public long id;
	@JsonProperty("nm") public String name;
	@JsonProperty("ds") public String description;
	@JsonProperty("pd") public Date publishedDate;
	@JsonProperty("img") public String image;
	@JsonProperty("url") public String url;
	@JsonProperty("attr") public String attribute;
	@JsonProperty("obj") public Object obj;
	@JsonProperty("seq") public int seq;
	@JsonProperty("nc") public int noClicks;
	@JsonProperty("ty") public String topicType;
	@JsonProperty("sty") public String topicSubType;
	@JsonProperty("ac") public boolean active;
	@JsonProperty("m") public boolean mobile;

	public FrontPageTopicVM(FrontPageTopic topic, User user) {
		this.id = topic.id;
		this.name = topic.name;
		this.description = topic.description;
		this.publishedDate = topic.publishedDate;
		this.image = topic.image;
		this.url = topic.url;
		this.attribute = topic.attribute;
		this.seq = topic.seq;
		this.noClicks = topic.noClicks;
		this.topicType = topic.topicType.name();
        this.topicSubType = topic.topicSubType.name();
		this.active = (topic.active != null && topic.active);
		this.mobile = (topic.mobile != null && topic.mobile);
		
		if (FrontPageTopic.TopicSubType.PK_VIEW.equals(topic.topicSubType)) {
		    Pair<PKViewMeta, Post> pair = PKViewMeta.getPKViewById(Long.parseLong(topic.attribute));
		    if (pair != null) {
		        this.obj = new PKViewVM(pair.first, pair.second, user, true);
		    }
		}
	}
}
