package viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Community;
import models.Post;
import models.User;
import models.Community.CommunityType;

import org.codehaus.jackson.annotate.JsonProperty;

import domain.DefaultValues;

public class QnAPostsVM {
	
	@JsonProperty("lu") public Long loggedUserId;
	@JsonProperty("lun") public String loggedUserName;
	
	@JsonProperty("posts") public List<CommunityPostVM> posts = Collections.EMPTY_LIST;
	
	public static QnAPostsVM qnaPosts(Community c, User user) {
		QnAPostsVM vm = new QnAPostsVM();
		
		vm.loggedUserId = user.id;
		vm.loggedUserName = user.displayName;
		
		boolean isMember = user.isMemberOf(c);
        boolean isOwner = (user == c.owner) ? true : false;
        
		List<CommunityPostVM> posts = new ArrayList<>();
		
		List<Post> postsFromDB = c.getQuestionsOfCommunity(0, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		if(isMember == true || isOwner == true || c.communityType == CommunityType.OPEN){
    		for(Post p: postsFromDB) {
    			CommunityPostVM post = CommunityPostVM.communityPostVM(p,user);
    			posts.add(post);
    		}
		}
		vm.posts = posts;
		return vm;
	}
	
	public static QnAPostsVM qnaPosts(Community c, User user, Post post) {
        QnAPostsVM vm = new QnAPostsVM();
        
        vm.loggedUserId = user.id;
        vm.loggedUserName = user.displayName;
        
        boolean isMember = user.isMemberOf(c);
        boolean isOwner = (user == c.owner) ? true : false;
        
        List<CommunityPostVM> posts = new ArrayList<>();
        
        if(isMember == true || isOwner == true || c.communityType == CommunityType.OPEN){
            posts.add(CommunityPostVM.communityPostVM(post,user));
        }
        vm.posts = posts;
        return vm;
    }
}
