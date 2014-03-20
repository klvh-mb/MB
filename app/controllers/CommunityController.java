package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import models.Community;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
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
		}
		try {
			return ok(community.getDefaultThumbnailCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getAllCommunitiesOfUser() {
		final User localUser = Application.getLocalUser(session());
		List<NotJoinedCommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			communityList.add(new NotJoinedCommunitiesWidgetChildVM(community.id,  (long) community.members.size(), community.name));
		}
		
		NotJoinedCommunitiesParentVM fwVM = new NotJoinedCommunitiesParentVM(communityList.size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getMyAllCommunities() {
		final User localUser = Application.getLocalUser(session());
		System.out.println("LIST ::::: "+localUser.getListOfJoinedCommunities().size());
		int count=0;
		List<NotJoinedCommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfJoinedCommunities()) {
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
	public static Result sendJoinRequest(String id) {
		final User localUser = Application.getLocalUser(session());
		System.out.println("COmmunity :: "+id);
		Community community = Community.findById(Long.parseLong(id));
		
		try {
			localUser.requestedToJoin(community);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
		
		return ok();
	}
	
	
}
	
	
	
