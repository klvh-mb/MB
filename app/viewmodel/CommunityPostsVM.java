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
		
		List<CommunityPostVM> postsVM = new ArrayList<>();
		if (canSeePostsOfCommunity(user, c)) {
    		for(Post p: posts) {
    			CommunityPostVM post = new CommunityPostVM(p,user);
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

        List<CommunityPostVM> posts = new ArrayList<>();
        if (canSeePostsOfCommunity(user, c)) {
            posts.add(new CommunityPostVM(post,user));
        }

        vm.posts = posts;
        return vm;
    }

    /**
     * Check if the given user can see posts of the community.
     * @param user
     * @param c
     * @return
     */
    private static boolean canSeePostsOfCommunity(User user, Community c) {
        if (c.communityType != CommunityType.CLOSE) {
            return true;
        }
        else {
            boolean isMember = user.isMemberOf(c.getId());
            boolean isOwner = (c.owner != null) && (c.owner.getId() == user.getId());
            return isMember || isOwner;
        }
    }
}
