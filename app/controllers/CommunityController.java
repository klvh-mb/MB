package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import models.Community;
import models.Community.CommunityType;
import models.Post;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData.FilePart;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.CommunityVM;
import viewmodel.CommunitiesParentVM;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;

import domain.CommentType;
import domain.PostType;
import static play.data.Form.form;

public class CommunityController extends Controller{

	@Transactional
	public static Result getUserUnJoinCommunity() {
		final User localUser = Application.getLocalUser(session());
		int count=0;
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			communityList.add(new CommunitiesWidgetChildVM(community.id,  (long) community.getMembers().size(), community.name));
			++count;
			
			if(count == 3) {
				break;
			}
		}
		
		CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result uploadPhotoOfPost() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String postId = form.get("postId");
		
		FilePart picture = request().body().asMultipartFormData().getFile("post-photo0");
		String fileName = picture.getFilename();
	    
		File file = picture.getFile();
	    File fileTo = new File(fileName);
	    // TOBE TESTED
	    try {
	    	FileUtils.copyFile(file, fileTo);
	    	Post.findById(Long.valueOf(postId)).addPostPhoto(fileTo);
			
		} catch (IOException e) {
			//e.printStackTrace();
			return status(500);
		}
		
	    
		return ok();
	}
	
	
	@Transactional
	public static Result getCommunityInfoById(Long id) {
		final User localUser = Application.getLocalUser(session());
		final Community community = Community.findById(id);
		return ok(Json.toJson(CommunityVM.communityVM(community, localUser)));
	}
	
	
	@Transactional
	public static Result getMiniCoverCommunityImageById(Long id) {
		Community community = Community.findById(id);
		if(community.getPhotoProfile() != null) {
			return ok(new File(community.getPhotoProfile().getMini()));
		}
		try {
			return ok(community.getDefaultMiniCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	@Transactional
	public static Result getThumbnailCoverCommunityImageById(Long id) {
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
	public static Result getFullCoverCommunityImageById(Long id)  {
		final Community community = Community.findById(id);
		if(community.getPhotoProfile() != null) {
			return ok(community.getPhotoProfile().getRealFile());
		}
		try {
			return ok(community.getDefaultCoverPhoto());
		} catch (FileNotFoundException e) {
			return ok("no image set");
		}
	}
	
	
	@Transactional
	public static Result getCommunityImageById(Long id) {
		final Community community = Community.findById(id);
		if(community.getPhotoProfile() != null) {
			return ok(new File(community.getPhotoProfile().getThumbnail()));
			//return ok();
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
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			communityList.add(new CommunitiesWidgetChildVM(community.id,  (long) community.getMembers().size(), community.name));
		}
		
		CommunitiesParentVM fwVM = new CommunitiesParentVM(communityList.size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getMyAnyThreeCommunities() {
		final User localUser = Application.getLocalUser(session());
		int count=0;
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfJoinedCommunities()) {
			communityList.add(new CommunitiesWidgetChildVM(community.id,  (long) community.getMembers().size(), community.name));
			++count;
			
			if(count == 3) {
				break;
			}
		}
		CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getMyAllCommunities() {
		final User localUser = Application.getLocalUser(session());
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfJoinedCommunities()) {
			communityList.add(new CommunitiesWidgetChildVM(community.id,  (long) community.getMembers().size(), community.name));
		}
		CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	
	@Transactional
	public static Result sendJoinRequest(String id) {
		final User localUser = Application.getLocalUser(session());
		Community community = Community.findById(Long.parseLong(id));
		
		try {
			localUser.requestedToJoin(community);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
		
		return ok();
	}
	

	
	@Transactional
	public static Result uploadCoverPhoto(Long id) {
		Community community = Community.findById(id);
		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();
	    
	    File file = picture.getFile();
	    File fileTo = new File(fileName);
	    
	    try {
	    	FileUtils.copyFile(file, fileTo);
	    	community.setCoverPhoto(fileTo);
		} catch (IOException e) {
			e.printStackTrace();
			return status(500);
		}
		return ok();
	}
	
	@Transactional
	public static Result createGroup(){
		final User localUser = Application.getLocalUser(session());
		Form<Community> form = DynamicForm.form(Community.class).bindFromRequest("name","description","communityType","tagetDistrict");
		Community community = form.get();
       
        FilePart picture = request().body().asMultipartFormData().getFile("cover-photo");
		String fileName = picture.getFilename();
		File file = picture.getFile();
		File fileTo = new File(fileName);
		
		Community newCommunity = localUser.createCommunity(community.name, community.description);
		try {
			newCommunity.ownerAsMember(localUser);
			newCommunity.tagetDistrict = community.tagetDistrict;
			newCommunity.communityType = community.communityType;
			FileUtils.copyFile(file, fileTo);
			newCommunity.setCoverPhoto(fileTo);
		} catch (SocialObjectNotJoinableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return status(500);
		}
		return ok("true");
	}
	
	@Transactional
	public static Result updateGroupProfileData(){
		Form<String> form = DynamicForm.form(String.class).bindFromRequest();
		String groupID = form.data().get("i");
		Community community = Community.findById(Long.parseLong(groupID));
		community.name = form.data().get("n");
		community.description = form.data().get("d");
		community.tagetDistrict = form.data().get("td");
		if(form.data().get("typ") == "OPEN"){
			community.communityType = CommunityType.OPEN;
		}else{
			community.communityType = CommunityType.CLOSE;
		}
		community.merge();
		return ok("true");
	}
	
	
	@Transactional
	public static Result commentOnCommunityPost() {
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = form().bindFromRequest();
		
		Long postId = Long.parseLong(form.get("post_id"));
		String commentText = form.get("commentText");
		
		Post p = Post.findById(postId);
		
		try {
			//NOTE: Currently commentType is hardcoded to SIMPLE
			p.onComment(localUser, commentText, CommentType.SIMPLE);
		} catch (SocialObjectNotCommentableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(Json.toJson(p.id));
	}
	
	@Transactional
	public static Result postOnCommunity() {
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = form().bindFromRequest();
		
		Long communityId = Long.parseLong(form.get("community_id"));
		String postText = form.get("postText");
		
		Community c = Community.findById(communityId);
		
		Post p = (Post) c.onPost(localUser, postText, PostType.SIMPLE);
		
		return ok(Json.toJson(p.id));
	}
	
	@Transactional
	public static Result joinToCommunity(Long id) {
		final User localUser = Application.getLocalUser(session());
		Community community = Community.findById(id);
		try {
			localUser.requestedToJoin(community);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
		return ok();
	}
	
	@Transactional
	public static Result leaveThisCommunity(Long community_id) {
		final User localUser = Application.getLocalUser(session());
		Community community = Community.findById(community_id);
		
		localUser.leaveCommunity(community);
		
		return ok();
	}
}
	
	
	
