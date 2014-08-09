package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Community;
import models.Conversation;
import models.Location;
import models.Message;
import models.Notification;
import models.Post;
import models.Resource;
import models.User;
import models.UserCommunityAffinity;

import org.apache.commons.lang.exception.ExceptionUtils;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import viewmodel.BookmarkSummaryVM;
import viewmodel.CommunityPostVM;
import viewmodel.ConversationVM;
import viewmodel.MessageVM;
import viewmodel.NewsFeedVM;
import viewmodel.NotificationVM;
import viewmodel.ProfileVM;
import viewmodel.SocialObjectVM;
import viewmodel.UserVM;

import com.mnt.exception.SocialObjectNotJoinableException;
import common.model.TargetGender;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;

import domain.DefaultValues;
import domain.SocialObjectType;

public class UserController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(UserController.class);
    
    @Transactional(readOnly=true)
    public static Result isNewsfeedEnabledForCommunity(Long communityId) {
        final User localUser = Application.getLocalUser(session());

        UserCommunityAffinity affinity = UserCommunityAffinity.findByUserCommunity(localUser.id, communityId);
        if (affinity == null)
            return status(500);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+", c="+communityId+"] isNewsfeedEnabledForCommunity: "+affinity.isNewsfeedEnabled());
        }

        Map<String, Boolean> map = new HashMap<>();
        map.put("newsfeedEnabled", affinity.isNewsfeedEnabled());
        return ok(Json.toJson(map));
    }
    
    @Transactional
    public static Result toggleNewsfeedEnabledForCommunity(Long communityId) {
        final User localUser = Application.getLocalUser(session());

        UserCommunityAffinity affinity = UserCommunityAffinity.findByUserCommunity(localUser.id, communityId);
        if (affinity == null)
            return status(500);

        boolean target = !affinity.isNewsfeedEnabled();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[c="+communityId+",u="+localUser.id+"] toggleNewsfeedEnabledForCommunity to: "+target);
        }

        affinity.setNewsfeedEnabled(target);

        Map<String, Boolean> map = new HashMap<>();
        map.put("newsfeedEnabled", target);
        return ok(Json.toJson(map));
    }
    
    @Transactional(readOnly=true)
    public static Result getBookmarkSummary() {
        final User localUser = Application.getLocalUser(session());
        BookmarkSummaryVM summary = new BookmarkSummaryVM(
                localUser.getQnABookmarkCount(), localUser.getPostBookmarkCount(), localUser.getArticleBookmarkCount());
        return ok(Json.toJson(summary));
    }
    
	@Transactional(readOnly=true)
	public static Result getUserInfo() {
		final User localUser = Application.getLocalUser(session());
		UserVM userInfo = new UserVM(localUser);
		return ok(Json.toJson(userInfo));
	}
	
	@Transactional(readOnly=true)
	public static Result aboutUser() {
		final User localUser = Application.getLocalUser(session());
		return ok(Json.toJson(localUser));
	}
	
	@Transactional
	public static Result uploadProfilePhoto() {
		final User localUser = Application.getLocalUser(session());
		logger.underlyingLogger().info("[u="+localUser.id+"] uploadProfilePhoto");

		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();

	    File file = picture.getFile();
	    try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
			localUser.setPhotoProfile(fileTo);
		} catch (IOException e) {
		    logger.underlyingLogger().error("Error in uploadProfilePhoto", e);
			return status(500);
		}
		return ok();
	}
	
	@Transactional
	public static Result uploadCoverPhoto() {
		final User localUser = Application.getLocalUser(session());
		logger.underlyingLogger().info("[u="+localUser.id+"] uploadCoverPhoto");

		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();
	    
	    File file = picture.getFile();
	    try {
	    	File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
			localUser.setCoverPhoto(fileTo);
		} catch (IOException e) {
		    logger.underlyingLogger().error("Error in uploadCoverPhoto", e);
			return status(500);
		}
		return ok();
	}
	
	@Transactional
	public static Result getProfileImage() {
	    response().setHeader("Cache-Control", "max-age=1");
	    final User localUser = Application.getLocalUser(session());
		
		if(localUser != null && localUser.getPhotoProfile() != null) {
			return ok(localUser.getPhotoProfile().getRealFile());
		}
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getCoverImage() {
	    response().setHeader("Cache-Control", "max-age=1");
	    final User localUser = Application.getLocalUser(session());
		
		if(localUser != null && localUser.getCoverProfile() != null) {
			return ok(localUser.getCoverProfile().getRealFile());
		}
		
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}

	@Transactional
	public static Result updateUserProfileData() {
		// UserInfo
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        String birthYear = form.get("userInfo.birthYear");
        Location location = null;
        try {
            location = Location.getLocationById(Integer.valueOf(form.get("userInfo.location")));
        } catch (Exception e) { }
        TargetGender gender = null;
        try {
            gender = TargetGender.valueOfInt(Integer.valueOf(form.get("userInfo.gender")));
        } catch (Exception e) { }
        String aboutMe = form.get("userInfo.aboutMe");
        
        final User localUser = Application.getLocalUser(session());
        localUser.firstName = firstName;
        localUser.lastName = lastName;
        localUser.displayName = firstName + " " + lastName;
        localUser.name = localUser.displayName;
        localUser.userInfo.birthYear = birthYear;
        localUser.userInfo.location = location;
        localUser.userInfo.gender = gender;
        localUser.userInfo.aboutMe = aboutMe;
        localUser.merge();
        
		return ok("true");
	}
	
	@Transactional
	public static Result startConeversation(Long id1, Long id2) {
		final User user1 = User.findById(id1);
		final User user2 = User.findById(id2);
		Conversation conversation = user1.findMyConversationsWith(user2);
		conversation.addMessage(user1, ".");
		conversation.addMessage(user2, ".");
		return ok();
	}
	
	@Transactional
	public static Result getUserNewsfeeds(String offset, Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User user = User.findById(id);
		final User localUser = Application.getLocalUser(session());
		List<CommunityPostVM> posts = new ArrayList<>();
		List<Post> newsFeeds;
		
		if(user == localUser){
			newsFeeds = localUser.getUserNewsfeeds(
			        Integer.parseInt(offset), DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		} else {
			newsFeeds = user.getUserNewsfeeds(
			        Integer.parseInt(offset), DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT, localUser);
		}
		
		if(newsFeeds != null ){
			for(Post p : newsFeeds) {
				CommunityPostVM post = CommunityPostVM.communityPostVisitProfile(p,user,localUser);
				posts.add(post);
			}
		}
		
		NewsFeedVM vm = new NewsFeedVM(user, posts);

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+id+"] getUserNewsfeeds(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(vm));
	}
	
	
	@Transactional
	public static Result getUserNewsfeedsComments(String offset, Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User user = User.findById(id);
		final User localUser = Application.getLocalUser(session());
		List<CommunityPostVM> posts = new ArrayList<>();
		List<Post> newsFeeds =  user.getUserNewsfeedsComments(Integer.parseInt(offset), DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		
		if(newsFeeds != null ){
			for(Post p : newsFeeds) {
				CommunityPostVM post = CommunityPostVM.communityPostVisitProfile(p,user,localUser);
				posts.add(post);
			}
		}
		
		NewsFeedVM vm = new NewsFeedVM(user, posts);

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+id+"] getUserNewsfeedsComments(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(vm));
	}
	
	
	@Transactional
	public static Result searchSocialObjects(String query) {
		final User localUser = Application.getLocalUser(session());
		List<User> users = localUser.searchLike(query);
		List<SocialObjectVM> socialVMs = new ArrayList<>();
		for(User user : users) {
		    if (user.isSuperAdmin()) {
		        continue;
		    }
		    
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
    	
    	List<NotificationVM> requests = new ArrayList<>();
    	for(Notification n : friendRequests) {
    		requests.add(new NotificationVM(n));
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
    	notification.status = 1;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result getAllJoinRequests() {
    	final User localUser = Application.getLocalUser(session());
    	List<Notification> joinRequests = localUser.getAllNotification();
    	List<NotificationVM> requests = new ArrayList<>();
    	for(Notification n : joinRequests) {
    		requests.add(new NotificationVM(n,SocialObjectType.ANSWER));
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
    	notification.status = 1;
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
    	notification.status = 1;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result markNotificationAsRead (Long id) {
    	Notification notification=Notification.findById(id);
    	notification.changeStatus(1);
    	return ok();
    }
    
    @Transactional
    public static Result ignoreNotification (Long id) {
    	Notification notification=Notification.findById(id);
    	notification.changeStatus(2);
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
        response().setHeader("Cache-Control", "max-age=1");
        User user = User.findById(id);
    	
		if(user != null && user.getPhotoProfile() != null) {
			return ok(user.getPhotoProfile().getRealFile());
		}
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
    @Transactional
	public static Result getOriginalImageByID(Long id) {
        response().setHeader("Cache-Control", "max-age=1");
        User user = User.findById(id);

        if(user != null && user.getPhotoProfile() != null) {
			return ok(user.getPhotoProfile().getRealFile());
		}
        
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
    
	@Transactional
	public static Result getCoverImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
	    User user = User.findById(id);

	    if(user != null && user.getCoverProfile() != null) {
			return ok(user.getCoverProfile().getRealFile());
		}
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getMiniVersionImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(user != null && user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getMini()));
		} 
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getMiniCommentVersionImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(user != null && user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getMiniComment()));
		} 
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getThumbnailVersionImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(user != null && user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
		}
		
		try {
			return ok(User.getDefaultUserPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getThumbnailCoverImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=1");
		final User user = User.findById(id);
		
		if(user != null && user.getCoverProfile() != null) {
			return ok(new File(user.getCoverProfile().getThumbnail()));
		}
		
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getAllConversation() {
		final User localUser = Application.getLocalUser(session());
		List<ConversationVM> vms = new ArrayList<>();
		List<Conversation> conversations =  localUser.findMyAllConversations();
		if(conversations != null ){
			for(Conversation conversation: conversations) {
				User user;
				if(conversation.user1 == localUser){
					user = conversation.user2;
				} else { 
					user = conversation.user1;
				}
				ConversationVM vm = new ConversationVM(conversation, user);
				vms.add(vm);
			}
		}
		
		return ok(Json.toJson(vms));
	}
	
	@Transactional
	public static Result getMessages(String id, String offset) {
		final User localUser = Application.getLocalUser(session());
		List<MessageVM> vms = new ArrayList<>();
		Conversation conversation = Conversation.findById(Long.parseLong(id)); 
		List<Message> messages =  (List<Message>) localUser.getMessageForConversation(conversation, Long.parseLong(offset));
		if(messages != null ){
			for(Message message : messages) {
				MessageVM vm = new MessageVM(message);
				vms.add(vm);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("message", vms);
		map.put("counter", localUser.getUnreadMsgCount());
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result sendMessage() {
        final User localUser = Application.getLocalUser(session());
        DynamicForm form = form().bindFromRequest();
        
        Long receiverUserID = Long.parseLong(form.get("receiver_id"));
        User receiverUser = User.findById(receiverUserID);
        String msgText = form.get("msgText");
        Conversation.sendMessage(localUser, receiverUser, msgText);
        Conversation conversation = Conversation.findBetween(localUser, receiverUser);
        return getMessages(conversation.id+"", 0+"");
    }
	
	@Transactional
    public static Result deleteConversation(Long id) {
		final User localUser = Application.getLocalUser(session());
        Conversation.archiveConversation(id, localUser);
        return getAllConversation();
    }
	
	@Transactional
    public static Result startConversation(Long id) {
        final User localUser = Application.getLocalUser(session());
        User user = User.findById(id);
        Conversation conversation = Conversation.startConversation(localUser, user);
        conversation.setUpdatedDate(new Date());
        return getAllConversation();
    }
	
	@Transactional
	public static Result searchUserFriends(String query) {
		final User localUser = Application.getLocalUser(session());
		List<User> users = localUser.searchUserFriends(query);
		List<SocialObjectVM> socialVMs = new ArrayList<>();
		for(User user : users) {
		    if (user.isSuperAdmin()) {
		        continue;
		    }
		    
			socialVMs.add(new SocialObjectVM(user.id.toString(), user.displayName, user.objectType.name()));
		}
		return ok(Json.toJson(socialVMs));
	}
	
	@Transactional
	public static Result sendPhotoInMessage() {
		final User localUser = Application.getLocalUser(session());
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String messageId = form.get("messageId");
        
        FilePart picture = request().body().asMultipartFormData().getFile("send-photo0");
        String fileName = picture.getFilename();
        
        File file = picture.getFile();
        try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
            Long id = Message.findById(Long.valueOf(messageId)).addPrivatePhoto(fileTo,localUser).id;
            System.out.println("id :: "+id);
            return ok(id.toString());
        } catch (IOException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            return status(500);
        }
    }
	
	@Transactional
	public static Result getUnreadMsgCount() {
		final User localUser = Application.getLocalUser(session());
		Map<String, Long> vm = new HashMap<>();
		vm.put("count", localUser.getUnreadMsgCount());
		return ok(Json.toJson(vm));
	}
	
	@Transactional
    public static Result getHeaderBarMetadata() {
		
		final User localUser = Application.getLocalUser(session());
		List<Notification> allNotify = localUser.getAllNotification();
		int unread_notify_count = 0;
    	List<NotificationVM> notif = new ArrayList<>();
    	for(Notification n : allNotify) {
    		if(n.status == 0){
    			unread_notify_count++;
    		}
    		notif.add(new NotificationVM(n));
    	}
		
    	
    	List<Notification> friendRequests = localUser.getAllFriendRequestNotification();
    	List<NotificationVM> requests = new ArrayList<>();
    	for(Notification n : friendRequests) {
    		requests.add(new NotificationVM(n));
    	}
    	
    	
		Map<String, Object> vm = new HashMap<>();
		
		vm.put("messageCount", localUser.getUnreadMsgCount());
		vm.put("requestNotif", requests);
		vm.put("allNotif", notif);
		vm.put("name", localUser.firstName);
		vm.put("notifyCount", unread_notify_count);
    	return ok(Json.toJson(vm));
    }
	
	@Transactional
	public static Result getMessageImageByID(Long id) {
	    response().setHeader("Cache-Control", "max-age=604800");
		return ok(Resource.findById(id).getThumbnailFile());
	}

    @Transactional
    public static Result getOriginalPrivateImageByID(Long id) {
        response().setHeader("Cache-Control", "max-age=604800");
        return ok(Resource.findById(id).getRealFile());
    }
}
