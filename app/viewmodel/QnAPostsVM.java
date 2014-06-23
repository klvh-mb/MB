package viewmodel;

import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.Post;
import models.User;

import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;

public class QnAPostsVM {
	
	@JsonProperty("lu") public Long loggedUserId;
	@JsonProperty("lun") public String loggedUserName;
	
	@JsonProperty("posts") public List<CommunityPostVM> posts;
	
	public static QnAPostsVM qnaPosts (Community c, User user) {
		QnAPostsVM vm = new QnAPostsVM();
		
		vm.loggedUserId = user.id;
		vm.loggedUserName = user.displayName;
		
		List<CommunityPostVM> posts = new ArrayList<>();
		
		List<Post> postsFromDB = c.getQuestionsOfCommunity(0, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		
		for(Post p: postsFromDB) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p,user);
			posts.add(post);
		}
		vm.posts = posts;
		return vm;
	}
}
