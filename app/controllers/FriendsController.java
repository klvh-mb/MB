package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mnt.exception.SocialObjectNotJoinableException;

import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;

import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.FriendWidgetChildVM;
import viewmodel.FriendWidgetParentVM;
import viewmodel.FriendsVM;
import viewmodel.FriendsParentVM;

public class FriendsController extends Controller {
	private static play.api.Logger logger = play.api.Logger.apply(FriendsController.class);

	@Transactional
	public static Result getMyFriendsForUtiltiy() {
		final User localUser = Application.getLocalUser(session());
		return getFriendsForUtility(localUser);
	}
	
	@Transactional
    public static Result getUserFriendsForUtiltiy(Long id) {
	    final User user = User.findById(id);
	    return getFriendsForUtility(user);
    }

	private static Result getFriendsForUtility(User user) {
	    List<FriendWidgetChildVM> friends = new ArrayList<>();
        for(User friend : user.getFriends(DefaultValues.FRIENDS_UTILITY_COUNT)) {
            friends.add(new FriendWidgetChildVM(friend.id, friend.displayName, friend.userInfo == null ? null : friend.userInfo.location));
        }
        
        FriendWidgetParentVM friendsVM = new FriendWidgetParentVM(user.getFriendsSize(), friends);
        return ok(Json.toJson(friendsVM));    
	}
	
	@Transactional
    public static Result getAllMyFriends() {
        final User localUser = Application.getLocalUser(session());
        return getAllFriends(localUser);
    }

	@Transactional
    public static Result getAllUserFriends(Long id) {
	    final User user = User.findById(id);
        return getAllFriends(user);
    }
	
	private static Result getAllFriends(User user) {
	    List<FriendsVM> friends = FriendsVM.friends(user);
        FriendsParentVM friendsVM = new FriendsParentVM(friends.size(), friends);
        return ok(Json.toJson(friendsVM));    
	}
	
	@Transactional
	public static Result getSuggestedFriends() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

		final User localUser = Application.getLocalUser(session());
		List<FriendWidgetChildVM> friends = new ArrayList<>();

        List<User> frds = localUser.getSuggestedFriends(DefaultValues.DEFAULT_UTILITY_COUNT);

		for(Object friend1 : frds) {
			User friend = (User) friend1;
			friends.add(new FriendWidgetChildVM(friend.id, friend.displayName, friend.userInfo.location));
		}

		FriendWidgetParentVM friendsVM = new FriendWidgetParentVM((long)friends.size(), friends);

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getSuggestedFriends. Took "+sw.getElapsedMS()+"ms");
        }
		return ok(Json.toJson(friendsVM));
	}
	
	@Transactional
	public static Result getUserImageById(Long id) {
	    response().setHeader("Cache-Control", "max-age=10");
		final User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
		}
		return ok("No Image");
	}
	
	@Transactional
	public static Result sendInvitation(String id) {
		final User localUser = Application.getLocalUser(session());
		User invitee = User.findById(Long.parseLong(id));
		
		try {
			localUser.sendFriendInviteTo(invitee);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
		
		return ok();
	}

	@Transactional
	public static Result doUnFriend(Long id) {
		final User localUser = Application.getLocalUser(session());
		User tobeUnfriend = User.findById(id);
		localUser.doUnFriend(tobeUnfriend);
		return ok();
	}
}
