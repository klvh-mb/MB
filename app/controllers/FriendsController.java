package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mnt.exception.SocialObjectNotJoinableException;

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
	
	@Transactional
	public static Result getUserFriends() {
		final User localUser = Application.getLocalUser(session());
		List<FriendWidgetChildVM> friends = new ArrayList<>();
		for(User friend : localUser.getFriends(DefaultValues.FRINDS_UTILITY_COUNT)) {
			friends.add(new FriendWidgetChildVM(friend.id, friend.displayName, friend.userInfo == null ? null : friend.userInfo.location));
		}
		
		FriendWidgetParentVM fwVM = new FriendWidgetParentVM(localUser.getFriendsSize(), friends);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getFriendsOfUser(Long id) {
		final User user = User.findById(id);
		List<FriendWidgetChildVM> friends = new ArrayList<>();
		for(User friend : user.getFriends(DefaultValues.FRINDS_UTILITY_COUNT)) {
			friends.add(new FriendWidgetChildVM(friend.id, friend.displayName, friend.userInfo.location));
		}
		
		FriendWidgetParentVM fwVM = new FriendWidgetParentVM(user.getFriendsSize(), friends);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getSuggestedFriends() {
		final User localUser = Application.getLocalUser(session());
		List<FriendWidgetChildVM> friends = new ArrayList<>();

        List<User> frds = localUser.getSuggestedFriends(DefaultValues.FRINDS_UTILITY_COUNT);

		for(Object friend1 : frds) {
			User friend = (User) friend1;
			friends.add(new FriendWidgetChildVM(friend.id, friend.displayName, friend.userInfo.location));
		}

		FriendWidgetParentVM fwVM = new FriendWidgetParentVM((long)friends.size(), friends);
		return ok(Json.toJson(fwVM));
	}
	
	
	@Transactional
	public static Result getUserFriendsByID(Long id) {
		final User user = User.findById(id);
		List<FriendsVM> friends = FriendsVM.friends(user);
		return ok(Json.toJson(friends));
	}
	
	@Transactional
	public static Result getUserImageById(Long id) {
		final User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
		}
		return ok("No Image");
	}
	
	@Transactional
	public static Result getAllFriendsOfUser() {
		final User localUser = Application.getLocalUser(session());
		List<FriendsVM> friends = FriendsVM.friends(localUser);
		FriendsParentVM fwVM = new FriendsParentVM(friends.size(), friends);
		return ok(Json.toJson(fwVM));
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
