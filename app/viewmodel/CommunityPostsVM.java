package viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Community;
import models.Post;
import models.User;
import models.Community.CommunityType;

import org.codehaus.jackson.annotate.JsonProperty;

public class CommunityPostsVM {
	
	@JsonProperty("lu") public Long loggedUserId;
	@JsonProperty("lun") public String loggedUserName;
	
	@JsonProperty("posts") public List<CommunityPostVM> posts = Collections.EMPTY_LIST;
	
	public static CommunityPostsVM posts(Community c, User user, List<Post> posts) {
	    CommunityPostsVM vm = new CommunityPostsVM();
		
		vm.loggedUserId = user.id;
		vm.loggedUserName = user.displayName;
		
		boolean isMember = user.isMemberOf(c.getId());
        boolean isOwner = (user == c.owner) ? true : false;
        
		List<CommunityPostVM> postsVM = new ArrayList<>();
		
		if(isMember == true || isOwner == true || c.communityType == CommunityType.OPEN){
    		for(Post p: posts) {
    			CommunityPostVM post = CommunityPostVM.communityPostVM(p,user);
    			postsVM.add(post);
    		}
		}
		vm.posts = postsVM;
		return vm;
	}
	
	public static CommunityPostsVM posts(Community c, User user, Post post) {
	    CommunityPostsVM vm = new CommunityPostsVM();
        
        vm.loggedUserId = user.id;
        vm.loggedUserName = user.displayName;
        
        boolean isMember = user.isMemberOf(c.getId());
        boolean isOwner = (user == c.owner) ? true : false;
        
        List<CommunityPostVM> posts = new ArrayList<>();
        
        if(isMember == true || isOwner == true || c.communityType == CommunityType.OPEN){
            posts.add(CommunityPostVM.communityPostVM(post,user));
        }
        vm.posts = posts;
        return vm;
    }
}
