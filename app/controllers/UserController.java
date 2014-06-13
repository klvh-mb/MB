package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.Notification;
import models.Post;
import models.User;

import org.apache.commons.io.FileUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import processor.FeedProcessor;
import viewmodel.CommunityPostVM;
import viewmodel.FriendWidgetChildVM;
import viewmodel.NewsFeedVM;
import viewmodel.NotificationVM;
import viewmodel.ProfileVM;
import viewmodel.SocialObjectVM;
import viewmodel.UserVM;

import com.mnt.exception.SocialObjectNotJoinableException;

public class UserController extends Controller {
	
	@Transactional(readOnly=true)
	public static Result getUserInfo() {
		final User localUser = Application.getLocalUser(session());
		UserVM userInfo = new UserVM(localUser);
		System.out.println(FeedProcessor.getUserFeedIds(localUser));
		return ok(Json.toJson(userInfo));
	}
	
	@Transactional(readOnly=true)
	public static Result aboutUser() {
		final User localUser = Application.getLocalUser(session());
		return ok(Json.toJson(localUser));
	}
	
	@Transactional
	public static Result updateUserDisplayName() {
		Form<String> form = DynamicForm.form(String.class).bindFromRequest();
		String displayName = form.data().get("displayName");
		final User localUser = Application.getLocalUser(session());
		localUser.displayName = displayName;
		localUser.name = displayName;
		localUser.merge();
		return ok("true");
	}
	
	@Transactional
	public static Result uploadProfilePhoto() {
		final User localUser = Application.getLocalUser(session());
		
		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();

	    File file = picture.getFile();
	    File fileTo = new File(fileName);

	    try {
	    	FileUtils.copyFile(file, fileTo);
			localUser.setPhotoProfile(fileTo);
		} catch (IOException e) {
			//e.printStackTrace();
			return status(500);
		}
		return ok();
	}
	
	@Transactional
	public static Result uploadCoverPhoto() {
		final User localUser = Application.getLocalUser(session());
		
		
		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();
	    
	    File file = picture.getFile();
	    File fileTo = new File(fileName);

	    try {
	    	FileUtils.copyFile(file, fileTo);
			localUser.setCoverPhoto(fileTo);
		} catch (IOException e) {
			//e.printStackTrace();
			return status(500);
		}
		return ok();
	}
	
	@Transactional
	public static Result getProfileImage() {
		final User localUser = Application.getLocalUser(session());
		if(localUser.getPhotoProfile() != null) {
			return ok(localUser.getPhotoProfile().getRealFile());
		}
		try {
			return ok(localUser.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
		
	}
	
	@Transactional
	public static Result getCoverImage() {
		final User localUser = Application.getLocalUser(session());
		if(localUser.getCoverProfile() != null) {
			return ok(localUser.getCoverProfile().getRealFile());
		}
		try {
			return ok(localUser.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
		
	}

	@Transactional
	public static Result updateUserProfileData() {
		Form<User> form = DynamicForm.form(User.class).bindFromRequest(
		        "firstName","lastName","gender","aboutMe","birth_year","location");
		User userForUpdation = form.get();
		final User localUser = Application.getLocalUser(session());
		localUser.firstName = userForUpdation.firstName;
		localUser.lastName = userForUpdation.lastName;
		localUser.aboutMe = userForUpdation.aboutMe;
		localUser.location = userForUpdation.location;
		localUser.userInfo.parent_birth_year = userForUpdation.userInfo.parent_birth_year;
		localUser.userInfo.parent_gender = userForUpdation.userInfo.parent_gender;
		localUser.merge();
		return ok("true");
	}
	
	@Transactional
	public static Result getUserNewsfeeds(String offset, Long id) {
		final User user = User.findById(id);
		final User localUser = Application.getLocalUser(session());
		List<CommunityPostVM> posts = new ArrayList<>();
		List<Post> newsFeeds =  user.getUserNewsfeeds(Integer.parseInt(offset), 5);
		
		if(newsFeeds != null ){
			for(Post p : newsFeeds) {
				CommunityPostVM post = CommunityPostVM.communityPostVisitProfile(p,user,localUser);
				posts.add(post);
			}
		}
		
		NewsFeedVM vm = new NewsFeedVM(user, posts);
		return ok(Json.toJson(vm));
	}
	
	@Transactional
	public static Result searchSocialObjects(String query) {
		final User localUser = Application.getLocalUser(session());
		List<User> users = localUser.searchLike(query);
		List<SocialObjectVM> socialVMs = new ArrayList<>();
		for(User user : users) {
			if(localUser != user) {
				socialVMs.add(new SocialObjectVM(user.id.toString(), user.displayName, user.objectType.name()));
			}
		}
		
		List<Community> communities = Community.search(query);
		for(Community community : communities) {
			socialVMs.add(new SocialObjectVM(community.id.toString(), community.name, community.objectType.name()));
		}
		return ok(Json.toJson(socialVMs));
	}
	

    @Transactional
    public static Result getAllFriendRequests() {
    	final User localUser = Application.getLocalUser(session());
    	List<Notification> friendRequests = localUser.getAllFriendRequestNotification();
    	
    	List<FriendWidgetChildVM> requests = new ArrayList<>();
    	for(Notification n : friendRequests) {
    		requests.add(new FriendWidgetChildVM(n.socialAction.actor, n.socialAction.getActorObject().name,n.id));
    	}
    	return ok(Json.toJson(requests));
    }
    
    @Transactional
    public static Result acceptFriendRequest(Long id, Long notify_id) {
    	final User localUser = Application.getLocalUser(session());
    	User invitee = User.findById(id);
    	
    	try {
			localUser.onFriendRequestAccepted(invitee);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
    	Notification notification = Notification.findById(notify_id);
    	notification.readed = true;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result getAllJoinRequests() {
    	final User localUser = Application.getLocalUser(session());
    	List<Notification> joinRequests = localUser.getAllJoinRequestNotification();
    	List<NotificationVM> requests = new ArrayList<>();
    	for(Notification n : joinRequests) {
    		requests.add(new NotificationVM(n));
    	}
    	return ok(Json.toJson(requests));
    }
    
    @Transactional
    public static Result acceptJoinRequest(Long member_id,Long group_id,Long notify_id) {
    	final User localUser = Application.getLocalUser(session());
    	
    	User invitee = User.findById(member_id);
    	Community community = Community.findById(group_id);
    	
    	try {
			localUser.joinRequestAccepted(community, invitee);
			
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
    	Notification notification = Notification.findById(notify_id);
    	notification.readed = true;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result acceptInviteRequest(Long member_id, Long group_id, Long notify_id) {
    	final User localUser = Application.getLocalUser(session());
    	
    	User invitee = User.findById(member_id);
    	Community community = Community.findById(group_id);
    	
    	try {
			localUser.inviteRequestAccepted(community, invitee);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
    	
    	Notification notification = Notification.findById(notify_id);
    	notification.readed = true;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result markNotificationAsRead (Long id) {
    	Notification notification=Notification.findById(id);
    	notification.markNotificationRead();
    	return ok();
    }
    
    @Transactional
    public static Result getProfile(Long id) {
    	User user = User.findById(id);
    	final User localUser = Application.getLocalUser(session());
    	
    	return ok(Json.toJson(ProfileVM.profile(user,localUser)));
    }
    
    @Transactional
	public static Result getProfileImageByID(Long id) {
    	User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
		}
		try {
			return ok(user.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
    @Transactional
	public static Result getOriginalImageByID(Long id) {
    	User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(user.getPhotoProfile().getRealFile());
		}
		try {
			return ok(user.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
		
	}
    
	@Transactional
	public static Result getCoverImageByID(Long id) {
		User user = User.findById(id);
		if(user.getCoverProfile() != null) {
			return ok(user.getCoverProfile().getRealFile());
		}
		try {
			return ok(user.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
		
	}
	
	@Transactional
	public static Result getMiniVersionImageByID(Long id) {
		final User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getMini()));
		} 
		
		try {
			// TODO:
			return ok(user.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
		
	}
	
	@Transactional
	public static Result getMiniCommentVersionImageByID(Long id) {
		final User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getMiniComment()));
		} 
		
		try {
			// TODO:
			return ok(user.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
		
	}
	
	@Transactional
	public static Result getThumbnailVersionImageByID(Long id) {
		final User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
		}
		try {
			// TODO:
			return ok(user.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getThumbnailCoverImageByID(Long id) {
		final User user = User.findById(id);
		if(user.getCoverProfile() != null) {
			return ok(new File(user.getCoverProfile().getThumbnail()));
		}
		try {
			// TODO:
			return ok(user.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
}
