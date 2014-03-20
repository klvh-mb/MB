package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.FriendsChildVM;
import viewmodel.FriendsParentVM;
import viewmodel.NotJoinedCommunitiesParentVM;
import viewmodel.NotJoinedCommunitiesWidgetChildVM;

public class CommunityController extends Controller{

	@Transactional
	public static Result getUserUnJoinCommunity() {
		final User localUser = Application.getLocalUser(session());
		System.out.println(localUser.getListOfNotJoinedCommunities().size());
		int count=0;
		List<NotJoinedCommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			communityList.add(new NotJoinedCommunitiesWidgetChildVM(community.id,  (long) community.members.size(), community.name));
			++count;
			
			if(count == 3) {
				break;
			}
		}
		
		NotJoinedCommunitiesParentVM fwVM = new NotJoinedCommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	
	@Transactional
	public static Result getCommunityImageById(Long id) {
		final Community community = Community.findById(id);
		if(community.getPhotoProfile() != null) {
			return ok(new File(community.getPhotoProfile().getThumbnail()));
			//return ok();
		}
		return ok("No Image");
	}
	
	@Transactional
	public static Result getAllCommunitiesOfUser() {
		final User localUser = Application.getLocalUser(session());
		System.out.println("List ::: "+localUser.getListOfNotJoinedCommunities().size());
		List<NotJoinedCommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			communityList.add(new NotJoinedCommunitiesWidgetChildVM(community.id,  (long) community.members.size(), community.name));
		}
		
		NotJoinedCommunitiesParentVM fwVM = new NotJoinedCommunitiesParentVM(communityList.size(), communityList);
		return ok(Json.toJson(fwVM));
	}
}
	
	
	
