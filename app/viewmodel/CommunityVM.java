package viewmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Community;
import models.Post;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityVM {
	@JsonProperty("n") public String name;
	@JsonProperty("i") public long id;
	@JsonProperty("isM") public boolean isMyCommunity;
	@JsonProperty("posts") public List<CommunityPostVM> posts;
	
	public static CommunityVM communityVM (Community c) {
		CommunityVM vm = new CommunityVM();
		vm.name = c.name;
		vm.id = c.id;
		
		//TODO Logic required
		vm.isMyCommunity = true;
		
		List<CommunityPostVM> posts = new ArrayList<>();
		
		for(Post p: c.posts) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p);
			posts.add(post);
		}
		
		vm.posts = posts;
		return vm;
	}
}
