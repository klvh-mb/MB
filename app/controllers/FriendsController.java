package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mnt.exception.SocialObjectNotJoinableException;

import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.FriendWidgetChildVM;
import viewmodel.FriendWidgetParentVM;
import viewmodel.FriendsChildVM;
import viewmodel.FriendsParentVM;

public class FriendsController extends Controller {
	
	@Transactional
	public static Result getUserFriends() {
		final User localUser = Application.getLocalUser(session());
		System.out.println(localUser.getFriends().size());
		int count=0;
		List<FriendWidgetChildVM> friends = new ArrayList<>();
		for(User friend : localUser.getFriends()) {
			friends.add(new FriendWidgetChildVM(friend.id, friend.displayName));
			++count;
			
			if(count == 12) {
				break;
			}
		}
		
		FriendWidgetParentVM fwVM = new FriendWidgetParentVM(localUser.getFriends().size(), friends);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getUserImageById(Long id) {
		final User user = User.findById(id);
		if(user.getPhotoProfile() != null) {
			return ok(new File(user.getPhotoProfile().getThumbnail()));
			//return ok();
		}
		return ok("No Image");
	}
	
	@Transactional
	public static Result getAllFriendsOfUser() {
		final User localUser = Application.getLocalUser(session());
	
		int count=0;
		List<FriendsChildVM> friends = new ArrayList<>();
		for(User friend : localUser.getFriends()) {
			friends.add(new FriendsChildVM(friend.id, friend.firstName + " " + friend.lastName,friend.displayName, friend.location));
		}
		
		FriendsParentVM fwVM = new FriendsParentVM(friends.size(), friends);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result sendInvitation(String id) {
		final User localUser = Application.getLocalUser(session());
		User friend = User.findById(Long.parseLong(id));
		
		try {
			localUser.onFriendRequest(friend);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
		
		return ok();
	}
}
