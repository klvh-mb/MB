package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;

import models.Community;
import models.Conversation;
import models.Emoticon;
import models.GameAccount;
import models.Location;
import models.Message;
import models.Notification;
import models.Post;
import models.Resource;
import models.SiteTour;
import models.User;
import models.UserCommunityAffinity;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import viewmodel.BookmarkSummaryVM;
import viewmodel.CommunityPostVM;
import viewmodel.ConversationVM;
import viewmodel.EmoticonVM;
import viewmodel.MessageVM;
import viewmodel.NewsFeedVM;
import viewmodel.NotificationVM;
import viewmodel.ProfileVM;
import viewmodel.SocialObjectVM;
import viewmodel.UserVM;

import com.mnt.exception.SocialObjectNotJoinableException;

import common.utils.HtmlUtil;
import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;

public class UserController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(UserController.class);
    
    public static String getMobileUserKey(final play.mvc.Http.Request r, final Object key) {
		final String[] m = r.queryString().get(key);
		if(m != null && m.length > 0) {
			try {
				return URLDecoder.decode(m[0], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
    
    @Transactional
    public static Result completeHomeTour() {
        final User localUser = Application.getLocalUser(session());
        SiteTour tour = SiteTour.getSiteTour(localUser.id, SiteTour.TourType.HOME);
        if (tour == null) {
            tour = new SiteTour(localUser.id, SiteTour.TourType.HOME);
            tour.complete();
            tour.save();
            logger.underlyingLogger().debug(String.format("[u=%d] User completed home tour", localUser.id));
        }
        return ok();
    }
    
    @Transactional(readOnly=true)
    public static Result isNewsfeedEnabledForCommunity(Long communityId) {
        final User localUser = Application.getLocalUser(session());
        
        Map<String, Boolean> map = new HashMap<>();
        UserCommunityAffinity affinity = UserCommunityAffinity.findByUserCommunity(localUser.id, communityId);
        if (affinity == null) {
            map.put("newsfeedEnabled", false);
            return ok(Json.toJson(map));
        }

        map.put("newsfeedEnabled", affinity.isNewsfeedEnabled());
        return ok(Json.toJson(map));
    }
    
    @Transactional
    public static Result toggleNewsfeedEnabledForCommunity(Long communityId) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        Map<String, Boolean> map = new HashMap<>();
        UserCommunityAffinity affinity = UserCommunityAffinity.findByUserCommunity(localUser.id, communityId);
        if (affinity == null) {
            // if no affinity previously, treat as disabled and toggle it on now
            affinity = UserCommunityAffinity.onJoinedCommunity(localUser.id, communityId);
            if (logger.underlyingLogger().isDebugEnabled()) {
                logger.underlyingLogger().debug("[c="+communityId+",u="+localUser.id+"] toggleNewsfeedEnabledForCommunity created affinity");
            }
        }

        boolean target = !affinity.isNewsfeedEnabled();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[c="+communityId+",u="+localUser.id+"] toggleNewsfeedEnabledForCommunity to: "+target);
        }

        affinity.setNewsfeedEnabled(target);

        map.put("newsfeedEnabled", target);
        return ok(Json.toJson(map));
    }
    
    @Transactional(readOnly=true)
    public static Result getBookmarkSummary() {
        final User localUser = Application.getLocalUser(session());
        
        BookmarkSummaryVM summary = new BookmarkSummaryVM(
                localUser.getQnABookmarkCount(), 
                localUser.getPostBookmarkCount(), 
                localUser.getArticleBookmarkCount(),
                localUser.getPKViewBookmarkCount()
                );
        logger.underlyingLogger().debug("[u="+localUser.id+"] getBookmarkSummary - "+
                summary.qnaBookmarkCount+"|"+summary.postBookmarkCount+"|"+
        		summary.articleBookmarkCount+"|"+summary.pkViewBookmarkCount);
        return ok(Json.toJson(summary));
    }
    
	@Transactional
	public static Result getUserInfo() {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
	    
		final User localUser = Application.getLocalUser(session());
		if (localUser == null) {
			return status(500);
		}
		
		UserVM userInfo = new UserVM(localUser);
		
		sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"] getUserInfo(). Took "+sw.getElapsedMS()+"ms");
        }
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
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		logger.underlyingLogger().info("STS [u="+localUser.id+"] uploadProfilePhoto");

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
	    completeHomeTour();
		return ok();
	}
	
	@Transactional
	public static Result uploadProfilePhotoMobile() {
		final User localUser = Application.getLocalUser(session());
		FilePart picture = request().body().asMultipartFormData().getFile("club_image");
		String fileName = picture.getFilename();
		logger.underlyingLogger().info("STS [u="+localUser.id+"] uploadProfilePhotoMobile - "+fileName);
		request().body().asMultipartFormData().getFile("club_image");
	    File file = picture.getFile();
	    completeHomeTour();
		return ok();
	}
	
	@Transactional
	public static Result uploadCoverPhoto() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		logger.underlyingLogger().info("STS [u="+localUser.id+"] uploadCoverPhoto");

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
	    completeHomeTour();
		return ok();
	}
	
	@Transactional
	public static Result getProfileImage() {
	    response().setHeader("Cache-Control", "max-age=1");
	    final User localUser = Application.getLocalUser(session());
		
		if(User.isLoggedIn(localUser) && localUser.getPhotoProfile() != null) {
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
		
		if(User.isLoggedIn(localUser) && localUser.getCoverProfile() != null) {
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
	    final User localUser = Application.getLocalUser(session());
	    
	    logger.underlyingLogger().info(String.format("[u=%d] updateUserProfileData", localUser.id));
	    
	    // Basic info
	    DynamicForm form = DynamicForm.form().bindFromRequest();
	    String parentDisplayName = form.get("parent_displayname");
	    String parentFirstName = form.get("parent_firstname");
	    String parentLastName = form.get("parent_lastname");
	    String parentAboutMe = form.get("parent_aboutme");
	    if (parentDisplayName == null || parentFirstName == null || parentLastName == null) {
	        logger.underlyingLogger().error(String.format(
	                "[u=%d][displayname=%s][firstname=%s][lastname=%s] displayname, firstname or lastname missing", 
	                localUser.id, parentDisplayName, parentFirstName, parentLastName));
            return status(500, "請填寫您的顯示名稱與姓名");
        }
	    
	    parentDisplayName = parentDisplayName.trim();
	    parentFirstName = parentFirstName.trim();
	    parentLastName = parentLastName.trim();
	    if (parentAboutMe != null) {
	        parentAboutMe = parentAboutMe.trim();
	    }
	    
	    if (!localUser.displayName.equals(parentDisplayName)) {  
	        if (!User.isDisplayNameValid(parentDisplayName)) {
                logger.underlyingLogger().error(String.format(
                        "[u=%d][displayname=%s] displayname contains whitespace", localUser.id, parentDisplayName));
                return status(500, "\""+parentDisplayName+"\" 不可有空格");
	        }
	        if (User.isDisplayNameExists(parentDisplayName)) {
                logger.underlyingLogger().error(String.format(
                        "[u=%d][displayname=%s] displayname already exists", localUser.id, parentDisplayName));
                return status(500, "\""+parentDisplayName+"\" 已被選用。請選擇另一個顯示名稱重試");
            }
        }
        
		// UserInfo
        String parentBirthYear = form.get("parent_birth_year");
        Location parentLocation = Location.getLocationById(Integer.valueOf(form.get("parent_location")));
        
        if (parentBirthYear == null || parentLocation == null) {
            logger.underlyingLogger().error(String.format(
                    "[u=%d][birthYear=%s][location=%s] birthYear or location missing", localUser.id, parentBirthYear, parentLocation.displayName));
            return status(500, "請填寫您的生日，地區");
        }
        
        localUser.displayName = parentDisplayName;
        localUser.name = parentDisplayName;
        localUser.firstName = parentFirstName;
        localUser.lastName = parentLastName;
        
        localUser.userInfo.birthYear = parentBirthYear;
        localUser.userInfo.location = parentLocation;
        localUser.userInfo.aboutMe = parentAboutMe;
        localUser.userInfo.save();
        localUser.save();
        
        /*
        ParentType parentType = ParentType.valueOf(form.get("parent_type"));
        int numChildren = Integer.valueOf(form.get("num_children"));
        if (ParentType.NA.equals(parentType)) {
            numChildren = 0;
        }
        
        if (parentBirthYear == null || parentLocation == null || parentType == null) {
            return status(500, "請填寫您的生日，地區，媽媽身份");
        }
        
        localUser.displayName = parentDisplayName;
        localUser.name = parentDisplayName;
        localUser.firstName = parentFirstName;
        localUser.lastName = parentLastName;
        
        UserInfo userInfo = new UserInfo();
        userInfo.birthYear = parentBirthYear;
        userInfo.location = parentLocation;
        userInfo.parentType = parentType;
        userInfo.aboutMe = parentAboutMe;
        
        if (ParentType.MOM.equals(parentType) || ParentType.SOON_MOM.equals(parentType)) {
            userInfo.gender = TargetGender.Female;
        } else if (ParentType.DAD.equals(parentType) || ParentType.SOON_DAD.equals(parentType)) {
            userInfo.gender = TargetGender.Male;
        } else {
            userInfo.gender = TargetGender.Female;   // default
        }
        userInfo.numChildren = numChildren;
        
        localUser.userInfo = userInfo;
        localUser.userInfo.save();
        
        // UseChild
        int maxChildren = (numChildren > 5)? 5 : numChildren;
        for (int i = 1; i <= maxChildren; i++) {
            String genderStr = form.get("bb_gender" + i);
            if (genderStr == null) {
                return status(500, "請選擇寶寶性別");
            }
            
            TargetGender bbGender = TargetGender.valueOf(form.get("bb_gender" + i));
            String bbBirthYear = form.get("bb_birth_year" + i);
            String bbBirthMonth = form.get("bb_birth_month" + i);
            String bbBirthDay = form.get("bb_birth_day" + i);
            
            if (bbBirthDay == null) {
                bbBirthDay = "";
            }
            
            if (!DateTimeUtil.isDateOfBirthValid(bbBirthYear, bbBirthMonth, bbBirthDay)) {
                return status(500, "寶寶生日日期格式不正確。請重試");
            }
            
            UserChild userChild = new UserChild();
            userChild.gender = bbGender;
            userChild.birthYear = bbBirthYear;
            userChild.birthMonth = bbBirthMonth;
            userChild.birthDay = bbBirthDay;
            
            userChild.save();
            localUser.children.add(userChild);
        }
        */
        
        return ok();
	}
	
	@Transactional
	public static Result getUserNewsfeeds(String offset, Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
		if (id == -1L) {
            id = localUser.id;
        }
		final User user = User.findById(id);
		
		List<CommunityPostVM> posts = new ArrayList<>();

        List<Post> newsFeeds = user.getUserNewsfeeds(Integer.parseInt(offset), DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		if(newsFeeds != null ){
			for(Post p : newsFeeds) {
				CommunityPostVM post = new CommunityPostVM(p, localUser);
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

        final User localUser = Application.getLocalUser(session());
        if (id == -1L) {
            id = localUser.id;
        }
        final User user = User.findById(id);
		
		List<CommunityPostVM> posts = new ArrayList<>();
		List<Post> newsFeeds =  user.getUserNewsfeedsComments(Integer.parseInt(offset), DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		
		if(newsFeeds != null ){
			for(Post p : newsFeeds) {
				CommunityPostVM post = new CommunityPostVM(p, localUser);
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
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		List<User> users = localUser.searchLike(query);
		List<SocialObjectVM> socialVMs = new ArrayList<>();
		for(User user : users) {
		    if (user.system) {
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
    public static Result acceptFriendRequest(Long id, Long notify_id) {
    	final User localUser = Application.getLocalUser(session());
    	if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
    	
    	User invitee = User.findById(id);
    	
    	try {
			localUser.onFriendRequestAccepted(invitee);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
    	Notification notification = Notification.findById(notify_id);
    	notification.status = 3;
    	notification.merge();
    	return ok();
    }

    @Transactional
    public static Result acceptJoinRequest(Long member_id,Long group_id,Long notify_id) {
    	final User localUser = Application.getLocalUser(session());
    	if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
    	
    	User invitee = User.findById(member_id);
    	Community community = Community.findById(group_id);
    	
    	try {
			localUser.joinRequestAccepted(community, invitee);
			
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
    	Notification notification = Notification.findById(notify_id);
    	notification.status = 3;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result acceptInviteRequest(Long member_id, Long group_id, Long notify_id) {
    	final User localUser = Application.getLocalUser(session());
    	if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
    	
    	User invitee = User.findById(member_id);
    	Community community = Community.findById(group_id);
    	
    	try {
			localUser.inviteRequestAccepted(community, invitee);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
    	
    	Notification notification = Notification.findById(notify_id);
    	notification.status = 3;
    	notification.merge();
    	return ok();
    }
    
    @Transactional
    public static Result markNotificationAsRead (String ids) {
    	Notification.markAsRead(ids);
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
    	NanoSecondStopWatch sw = new NanoSecondStopWatch();
	    
    	User user = User.findById(id);
    	final User localUser = Application.getLocalUser(session());
		
		sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+user.getId()+"] getProfile(). Took "+sw.getElapsedMS()+"ms");
        }

    	return ok(Json.toJson(ProfileVM.profile(user,localUser)));
    }
    
    @Transactional
	public static Result getProfileImageByID(Long id) {
        response().setHeader("Cache-Control", "max-age=1");
        User user = User.findById(id);
    	
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
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

        if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
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

	    if(User.isLoggedIn(user) && user.getCoverProfile() != null) {
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
		
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
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
		
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
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
		
		if(User.isLoggedIn(user) && user.getPhotoProfile() != null) {
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
		
		if(User.isLoggedIn(user) && user.getCoverProfile() != null) {
			return ok(new File(user.getCoverProfile().getThumbnail()));
		}
		
		try {
			return ok(User.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
    public static Result getEmoticons() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        List<Emoticon> emoticons = Emoticon.getEmoticons();
        
        List<EmoticonVM> emoticonVMs = new ArrayList<>();
        for(Emoticon emoticon : emoticons) {
            EmoticonVM vm = new EmoticonVM(emoticon);
            emoticonVMs.add(vm);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getEmoticons. Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(emoticonVMs));
    }
	   
	@Transactional
	public static Result getMessages(Long id, Long offset) {
		final User localUser = Application.getLocalUser(session());
		List<MessageVM> vms = new ArrayList<>();
		Conversation conversation = Conversation.findById(id); 
		List<Message> messages =  conversation.getMessages(localUser, offset);
		if(messages != null ){
			for(Message message : messages) {
				MessageVM vm = new MessageVM(message);
				vms.add(vm);
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("message", vms);
		map.put("counter", localUser.getUnreadConversationCount());
		return ok(Json.toJson(map));
	}
	
	@Transactional
    public static Result sendMessage() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        DynamicForm form = form().bindFromRequest();
        
        Long receiverUserID = Long.parseLong(form.get("receiver_id"));
        User receiverUser = User.findById(receiverUserID);
        String msgText = HtmlUtil.convertTextToHtml(form.get("msgText"));
        Conversation.sendMessage(localUser, receiverUser, msgText);
        Conversation conversation = Conversation.findByUsers(localUser, receiverUser);
        return getMessages(conversation.id, 0L);
    }
	
	@Transactional
    public static Result sendGreetingMessageToNewUser() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        User superAdmin = Application.getMBAdmin();
        String msgText = HtmlUtil.convertTextToHtml("歡迎來到「小萌豆 miniBean」~  立即發掘您喜愛的媽媽社群與話題。 請開心分享！");
        Conversation.sendMessage(superAdmin, localUser, msgText);
        return ok();
    }
	
	@Transactional
    public static Result deleteConversation(Long id) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
        Conversation.archiveConversation(id, localUser);
        return getAllConversations();
    }
	
	private static List<ConversationVM> getAllConversations(User localUser, ConversationVM newConversationVM) {
		List<ConversationVM> vms = new ArrayList<>();
		List<Conversation> conversations = localUser.findMyConversations();
		if (conversations != null) {
			User otherUser;
			for (Conversation conversation : conversations) {
				// archived, dont show
				if (conversation.isArchivedBy(localUser)) {
					continue;
				}

				// add new conversation to top of list
				if (newConversationVM != null && conversation.id == newConversationVM.id) {
					continue;
				}

				if (conversation.user1 == localUser) {
					otherUser = conversation.user2;
				} else { 
					otherUser = conversation.user1;
				}
				
				ConversationVM vm = new ConversationVM(conversation, localUser, otherUser);
				vms.add(vm);
			}
		}
		
		// always add new conversation to top of list
		if (newConversationVM != null) {
			vms.add(0,newConversationVM);
		}
		
		return vms;	
	}

	@Transactional
	public static Result getAllConversations() {
		final User localUser = Application.getLocalUser(session());
		List<ConversationVM> vms = getAllConversations(localUser, null);
		return ok(Json.toJson(vms));
	}
	
	@Transactional
    public static Result startConversation(Long id) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        if (localUser.id == id) {
            logger.underlyingLogger().error(String.format("[u1=%d] [u2=%d] Same user. Will not start conversation", localUser.id, id));
            return status(500);
        }
        
        User otherUser = User.findById(id);
        Conversation newConversation = Conversation.startConversation(localUser, otherUser);
        ConversationVM newConversationVM = null;
        if (newConversation != null) {
        	newConversationVM = new ConversationVM(newConversation, localUser, otherUser);
        }
        List<ConversationVM> vms = getAllConversations(localUser, newConversationVM);

        return ok(Json.toJson(vms));
    }
	
	@Transactional
    public static Result openConversation(Long cid, Long id) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        if (localUser.id == id) {
            logger.underlyingLogger().error(String.format("[u1=%d] [u2=%d] Same user. Will not open conversation", localUser.id, id));
            return status(500);
        }
        
        Conversation conv = Conversation.findById(cid);
        
        List<ConversationVM> vms = new ArrayList<>();
        User otherUser = User.findById(id);
        ConversationVM vm = new ConversationVM(conv, localUser, otherUser);
		vms.add(vm);
        
		return ok(Json.toJson(vms));
    }
	
	@Transactional
	public static Result searchUserFriends(String query) {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
		List<User> users = localUser.searchUserFriends(query);
		List<SocialObjectVM> socialVMs = new ArrayList<>();
		for(User user : users) {
		    if (user.system) {
		        continue;
		    }
		    
			socialVMs.add(new SocialObjectVM(user.id.toString(), user.displayName, user.objectType.name()));
		}
		return ok(Json.toJson(socialVMs));
	}
	
	@Transactional
	public static Result sendPhotoInMessage() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
		
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String messageId = form.get("messageId");
        
        FilePart picture = request().body().asMultipartFormData().getFile("send-photo0");
        String fileName = picture.getFilename();
        
        File file = picture.getFile();
        try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
            Long id = Message.findById(Long.valueOf(messageId)).addPrivatePhoto(fileTo,localUser).id;
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
		vm.put("count", localUser.getUnreadConversationCount());
		return ok(Json.toJson(vm));
	}
	
	@Transactional
    public static Result getHeaderBarMetadata() {
		NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
		List<Notification> batchupNotif = Notification.getAllNotification(localUser.id);
		int unread_allNotif_count = 0;
		int unread_reqNotif_count = 0;
    	List<NotificationVM> notif = new ArrayList<>();
    	for(Notification n : batchupNotif) {
    		if(n.status == 0){
    			unread_allNotif_count++;
    		}
    		notif.add(new NotificationVM(n));
    	}
    	
    	List<Notification> requestNotif = Notification.getAllRequestNotification(localUser.id);
    	List<NotificationVM> requests = new ArrayList<>();
    	for(Notification n : requestNotif) {
    		if(n.status == 0){
    			unread_reqNotif_count++;
    		}
    		requests.add(new NotificationVM(n));
    	}
    	
		Map<String, Object> vm = new HashMap<>();
		
		vm.put("messageCount", localUser.getUnreadConversationCount());
		vm.put("requestNotif", requests);
		vm.put("allNotif", notif);
		vm.put("name", localUser.displayName);
		vm.put("notifyCount", unread_allNotif_count);
		vm.put("requestCount", unread_reqNotif_count);

        sw.stop();
        if (sw.getElapsedMS() > 50) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getHeaderBarMetadata. Took "+sw.getElapsedMS()+"ms");
        }
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

    @Transactional
    public static Result inviteByEmail(String email) {
		final User localUser = Application.getLocalUser(session());

        if (localUser.isLoggedIn()) {
            GameAccount gameAccount = GameAccount.findByUserId(localUser.id);
            gameAccount.sendInvitation(email);
        } else {
            logger.underlyingLogger().info("Not signed in. Skipped signup invitation to: "+email);
        }
		return ok();
	}
}
