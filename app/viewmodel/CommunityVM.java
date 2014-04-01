package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.Post;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityVM {
	@JsonProperty("lu") public Long loggedUserId;
	@JsonProperty("lun") public String loggedUserName;
	
	@JsonProperty("n") public String name;
	@JsonProperty("d") public String description;
	@JsonProperty("i") public long id;
	@JsonProperty("isM") public boolean isMember;
	@JsonProperty("isP") public boolean isRequested;
	@JsonProperty("isO") public boolean isOwner;
	@JsonProperty("posts") public List<CommunityPostVM> posts;
	
	public static CommunityVM communityVM (Community c, User user) {
		CommunityVM vm = new CommunityVM();
		vm.loggedUserId = user.id;
		vm.loggedUserName = user.displayName;
		
		vm.name = c.name;
		vm.description = c.description;
		vm.id = c.id;
		
		//TODO Logic required
		vm.isMember = user.isMemberOf(c);
		
		vm.isRequested = user.isJoinRequestPendingFor(c);
		
		vm.isOwner = (user == c.owner) ? true : false;
		
		List<CommunityPostVM> posts = new ArrayList<>();
		
		for(Post p: c.posts) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p);
			posts.add(post);
		}
		
		vm.posts = posts;
		return vm;
	}
}
