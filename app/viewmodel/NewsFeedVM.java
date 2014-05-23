package viewmodel;

import java.util.List;

import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class NewsFeedVM {
	@JsonProperty("lu") public Long loggedUserId;
	@JsonProperty("lun") public String loggedUserName;
	@JsonProperty("posts") public List<CommunityPostVM> posts;
	
	public NewsFeedVM (User u, List<CommunityPostVM> posts) {
		this.loggedUserId = u.id;
		this.loggedUserName = u.name;
		this.posts = posts;
	}
}
