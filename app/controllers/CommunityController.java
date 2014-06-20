package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.Comment;
import models.Community;
import models.Community.CommunityType;
import models.Icon;
import models.Post;
import models.Resource;
import models.User;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import viewmodel.CommunitiesParentVM;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.CommunityPostCommentVM;
import viewmodel.CommunityPostVM;
import viewmodel.CommunityVM;
import viewmodel.IconVM;
import viewmodel.MemberWidgetParentVM;
import viewmodel.MembersWidgetChildVM;
import viewmodel.NewsFeedVM;
import viewmodel.QnAPostsVM;
import viewmodel.SocialObjectVM;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotLikableException;
import com.typesafe.plugin.RedisPlugin;

import domain.CommentType;
import domain.PostType;
import domain.SocialObjectType;

public class CommunityController extends Controller{

	private static play.api.Logger logger = play.api.Logger.apply("application");
	private static String prefix = Play.application().configuration().getString("keyprefix", "prod_");
	private static final String USER = prefix + "user_";
	private static final String MOMENT = prefix + "moment_";
	private static final String QNA = prefix + "qna_";
	
	@Transactional
	public static Result getUserUnJoinCommunity() {
		logger.underlyingLogger().debug("getUserUnJoinCommunity");
		final User localUser = Application.getLocalUser(session());
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		int i = 0; 
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			if(i >= 5)
				break;
			CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(
			        community.id, (long) community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType);
			vm.isP = localUser.isJoinRequestPendingFor(community);
			communityList.add(vm);
			i++;
			
		}
		
		CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		if(localUser.getListOfNotJoinedCommunities().size() < 5){
			fwVM.isMore = true;
		}else{
			fwVM.isMore = false;
		}
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result uploadPhotoOfPost() {
		logger.underlyingLogger().debug("uploadPhotoOfPost");
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String postId = form.get("postId");
		
		FilePart picture = request().body().asMultipartFormData().getFile("post-photo0");
		String fileName = picture.getFilename();
	    
		File file = picture.getFile();
	    File fileTo = new File(Play.application().configuration().getString("image.temp")+""+fileName);
	    // TOBE TESTED
	    try {
	    	FileUtils.copyFile(file, fileTo);
	    	Long id = Post.findById(Long.valueOf(postId)).addPostPhoto(fileTo).id;
	    	return ok(id.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			return status(500);
		}
		
	    
	
	}
	
	
	@Transactional
	public static Result getCommunityInfoById(Long id) {
		logger.underlyingLogger().debug("getCommunityInfoById");
		final User localUser = Application.getLocalUser(session());
		final Community community = Community.findById(id);
		if(community.objectType == SocialObjectType.COMMUNITY) {
		//if(localUser.isMemberOf(community) || community.owner.id == localUser.id || community.communityType.toString().equals("OPEN") && localUser.isMemberOf(community) == false || community.communityType.toString().equals("CLOSE") && localUser.isMemberOf(community) == true){
			return ok(Json.toJson(CommunityVM.communityVM(community, localUser)));
		}
		else
		return ok();
	}
	
	@Transactional
	public static Result getEditCommunityInfo(Long id) {
		logger.underlyingLogger().debug("getEditCommunityInfo");
		final User localUser = Application.getLocalUser(session());
		final Community community = Community.findById(id);
		if(community.owner.id == localUser.id) {
			System.out.println("Owner");
			return ok(Json.toJson(CommunityVM.communityVM(community, localUser)));
		}
		System.out.println("Not Owner");
		return status(500);
	}
	
	@Transactional
	public static Result getPostImageById(Long id) {
		logger.underlyingLogger().debug("getPostImageById");
		return ok(Resource.findById(id).getThumbnailFile());
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
		logger.underlyingLogger().debug("getThumbnailCoverCommunityImageById");
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
		logger.underlyingLogger().debug("getFullCoverCommunityImageById");
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
		logger.underlyingLogger().debug("getCommunityImageById");
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
		logger.underlyingLogger().debug("getAllCommunitiesOfUser");
		final User localUser = Application.getLocalUser(session());
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfNotJoinedCommunities()) {
			CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "",  
			        community.iconName, community.communityType);
			vm.isP = localUser.isJoinRequestPendingFor(community);
			communityList.add(vm);
		}
		
		CommunitiesParentVM fwVM = new CommunitiesParentVM(communityList.size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getMyAnyThreeCommunities() {
		logger.underlyingLogger().debug("getMyAnyThreeCommunities");
		final User localUser = Application.getLocalUser(session());
		int count=0;
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfJoinedCommunities()) {
			communityList.add(new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType));
			++count;
			
			if(count == 5) {
				break;
			}
		}
		CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		if(count < 5){
			fwVM.isMore = true;
		}else {
			fwVM.isMore = false;
		}
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getThreeCommunitiesOfUser(Long id) {
		logger.underlyingLogger().debug("getThreeCommunitiesOfUser");
		final User user = User.findById(id);
		final User localUser = Application.getLocalUser(session());
		int count=0;
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : user.getListOfJoinedCommunities()) {
			CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType);
			communityList.add(vm);
			++count;
			vm.isP = localUser.isJoinRequestPendingFor(community);
			if(count == 5) {
				break;
			}
		}
		CommunitiesParentVM fwVM = new CommunitiesParentVM(user.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	
	
	@Transactional
	public static Result getMyAllCommunities() {
		logger.underlyingLogger().debug("getMyAllCommunities");
		final User localUser = Application.getLocalUser(session());
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : localUser.getListOfJoinedCommunities()) {
			CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType);
			vm.isO = (localUser == community.owner) ? true : false;
			communityList.add(vm);
		}
		CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getAllCommunitiesOfUserID(Long id) {
		logger.underlyingLogger().debug("getAllCommunitiesOfUserID");
		final User user = User.findById(id);
		final User localUser = Application.getLocalUser(session());
		List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
		for(Community community : user.getListOfJoinedCommunities()) {
			CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType);
			vm.isP = localUser.isJoinRequestPendingFor(community);
			communityList.add(vm);
		}
		CommunitiesParentVM fwVM = new CommunitiesParentVM(user.getListOfNotJoinedCommunities().size(), communityList);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result getAllComments(Long id) {
		logger.underlyingLogger().debug("getAllComments");
		Post post = Post.findById(id);
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		List<Comment> comments = post.getCommentsOfPost();
		for(Comment comment : comments) {
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentsToShow.add(commentVM);
		}
		return ok(Json.toJson(commentsToShow));
	}
	
	@Transactional
	public static Result getAllAnswers(Long id) {
		logger.underlyingLogger().debug("getAllAnswers");
		Post post = Post.findById(id);
		List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
		List<Comment> comments = post.getCommentsOfPost();
		for(Comment comment : comments) {
			CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment);
			commentsToShow.add(commentVM);
		}
		return ok(Json.toJson(commentsToShow));
	}
	
	@Transactional
	public static Result sendJoinRequest(String id) {
		logger.underlyingLogger().debug("sendJoinRequest");
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
	public static Result getNextPosts(String id,String offset) {
		logger.underlyingLogger().debug("getNextPosts");
		final User localUser = Application.getLocalUser(session());
		Community community = Community.findById(Long.parseLong(id));
		int start = Integer.parseInt(offset) * 5 + 5;
		List<CommunityPostVM> postsVM = new ArrayList<>();
		List<Post> posts =  community.getPostsOfCommunity(start, 5);
		for(Post p: posts) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p, localUser);
			postsVM.add(post);
		}
		return ok(Json.toJson(postsVM));
	}
	
	@Transactional
	public static Result getNextQuests(String id,String offset) {
		logger.underlyingLogger().debug("getNextQuests");
		final User localUser = Application.getLocalUser(session());
		Community community = Community.findById(Long.parseLong(id));
		int start = Integer.parseInt(offset) * 5 + 5;
		List<CommunityPostVM> postsVM = new ArrayList<>();
		List<Post> posts =  community.getQuestionsOfCommunity(start, 5);
		for(Post p: posts) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
			postsVM.add(post);
		}
		return ok(Json.toJson(postsVM));
	}
	
	
	@Transactional
	public static Result uploadCoverPhoto(Long id) {
		logger.underlyingLogger().debug("uploadCoverPhoto");
		Community community = Community.findById(id);
		FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
		String fileName = picture.getFilename();

	    File file = picture.getFile();
	    File fileTo = new File(Play.application().configuration().getString("image.temp")+""+fileName);
	    
			// No cropping is performed
	    	try {
		    	FileUtils.copyFile(file, fileTo);
		    	community.setCoverPhoto(fileTo);
			} catch (IOException e) {
				//e.printStackTrace();
				return status(500);
			}
		
		return ok();
	}
	
	@Transactional
	public static Result createCommunity() {
		logger.underlyingLogger().debug("createCommunity");
		final User localUser = Application.getLocalUser(session());
		Form<Community> form = 
		        DynamicForm.form(Community.class).bindFromRequest(
		                "name","description","iconName","communityType");
		Community community = form.get();
        if (community.communityType == null) {
        	community.communityType = CommunityType.OPEN;
        }
		if (!community.checkCommunityNameExists()) {
			return status(505, "PLEASE CHOOSE OTHER NAME");
		}
		
        FilePart picture = request().body().asMultipartFormData().getFile("cover-photo");
		String fileName = picture.getFilename();
		File file = picture.getFile();
		File fileTo = new File(Play.application().configuration().getString("image.temp")+""+fileName);
		
		try {
    		Community newCommunity = localUser.createCommunity(
    		        community.name, community.description,community.communityType, community.iconName);
    		if (newCommunity == null) {
    			return status(505, "Valid param missing");
    		}
    		FileUtils.copyFile(file, fileTo);
            newCommunity.setCoverPhoto(fileTo);
            
            return ok(Json.toJson(newCommunity.id));
		} catch (SocialObjectNotJoinableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return status(500);
	}
	
	@Transactional
	public static Result updateCommunityProfileData(){
		logger.underlyingLogger().debug("updateCommunityProfileData");
		Form<String> form = DynamicForm.form(String.class).bindFromRequest();
		Map<String, String> dataToUpdate = form.data();
		String communityId = dataToUpdate.get("i");
		Community community = Community.findById(Long.parseLong(communityId));
		community.name = dataToUpdate.get("n");
		community.description = dataToUpdate.get("d");
		community.tagetDistrict = dataToUpdate.get("td");
		if(dataToUpdate.get("typ").equalsIgnoreCase("open")){
			community.communityType = CommunityType.OPEN;
		}else{
			community.communityType = CommunityType.CLOSE;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		
		try {
			if(dataToUpdate.get("dte") != null) {
				java.util.Date date = sdf.parse(dataToUpdate.get("dte"));
				community.createDate =date;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		community.iconName = dataToUpdate.get("iconName");
		community.merge();
		return ok("true");
	}
	
	@Transactional
	public static Result commentOnCommunityPost() {
		logger.underlyingLogger().debug("commentOnCommunityPost");
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = form().bindFromRequest();
		
		
		Long postId = Long.parseLong(form.get("post_id"));
		String commentText = form.get("commentText");
		
		Post p = Post.findById(postId);
		Community c =p.community;
		if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
			Comment comment = null;
			try {
				//NOTE: Currently commentType is hardcoded to SIMPLE
				comment = (Comment) p.onComment(localUser, commentText, CommentType.SIMPLE);
				p.setUpdatedDate(new Date());
				p.merge();
			} catch (SocialObjectNotCommentableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
			Jedis j = jedisPool.getResource();
			
			j.zadd(MOMENT + c.id, new Date().getTime(), p.id.toString());
			jedisPool.returnResource(j);
			
			return ok(Json.toJson(comment.id));
		}
		return ok("Be member of community");
	}
	
	@Transactional
	public static Result postOnCommunity() {
		logger.underlyingLogger().debug("postOnCommunity");
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = form().bindFromRequest();
		
		Long communityId = Long.parseLong(form.get("community_id"));
		Community c =Community.findById(communityId);
		if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
			String postText = form.get("postText");
			String withPhotos = form.get("withPhotos");
			Post p = (Post) c.onPost(localUser, null, postText, PostType.SIMPLE);
			if(Boolean.parseBoolean(withPhotos)) {
				p.ensureAlbumExist();
			}
			
			JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
			Jedis j = jedisPool.getResource();
			
			j.zadd(MOMENT + c.id,  new Date().getTime(), p.id.toString());
			jedisPool.returnResource(j);
			
			p.indexPost(Boolean.parseBoolean(withPhotos));
			
			return ok(Json.toJson(p.id));
		}
		
		return ok("First join the Community");
	}
	
	@Transactional
	public static Result joinToCommunity(Long id) {
		logger.underlyingLogger().debug("joinToCommunity");
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
		logger.underlyingLogger().debug("leaveThisCommunity");
		final User localUser = Application.getLocalUser(session());
		Community community = Community.findById(community_id);
		
		localUser.leaveCommunity(community);
		
		return ok();
	}
	
	@Transactional
	public static Result getCommunityMembers(Long id) {
		logger.underlyingLogger().debug("getCommunityMembers");
		Community community = Community.findById(id);
		List<MembersWidgetChildVM> members = new ArrayList<>();
		for(User member : community.getMembers()) {
			if(community.owner.equals(member)) {
				members.add(new MembersWidgetChildVM(member.id, member.displayName,true));
				continue;
			}
			members.add(new MembersWidgetChildVM(member.id, member.displayName,false));
		}
		MemberWidgetParentVM fwVM = new MemberWidgetParentVM(community.getMembers().size(), members);
		return ok(Json.toJson(fwVM));
	}
	
	@Transactional
	public static Result postQuestionOnCommunity() {
		logger.underlyingLogger().debug("postQuestionOnCommunity");
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = DynamicForm.form().bindFromRequest();
		Long communityId = Long.parseLong(form.get("community_id"));
		String questionTitle = form.get("questionTitle");
		String questionText = form.get("questionText");
		Community c = Community.findById(communityId);
		if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
			String withPhotos = form.get("withPhotos");
			
			Post p = (Post) c.onPost(localUser, questionTitle, questionText, PostType.QUESTION);
			
			if(Boolean.parseBoolean(withPhotos)) {
				p.ensureAlbumExist();
			}
			
			JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
			Jedis j = jedisPool.getResource();
			
			j.zadd(QNA + c.id, new Date().getTime(), p.id.toString());
			jedisPool.returnResource(j);
			
			p.indexPost(Boolean.parseBoolean(withPhotos));
			return ok(Json.toJson(p.id));
		}
		return ok("You are not member of this community");
	}
	
	@Transactional
	public static Result answerToQuestionOnQnACommunity() {
		logger.underlyingLogger().debug("answerToQuestionOnQnACommunity");
		final User localUser = Application.getLocalUser(session());
		DynamicForm form = form().bindFromRequest();
		
		Long postId = Long.parseLong(form.get("post_id"));
		String answerText = form.get("answerText");
		
		Post p = Post.findById(postId);
		Community c =p.community;
		if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
			try {
				p.onComment(localUser, answerText, CommentType.ANSWER);
				
				p.setUpdatedDate(new Date());
				p.merge();
			} catch (SocialObjectNotCommentableException e) {
				e.printStackTrace();
			}
			
			JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
			Jedis j = jedisPool.getResource();
			
			j.zadd(MOMENT + c.id, new Date().getTime(), p.id.toString());
			jedisPool.returnResource(j);
			
			return ok(Json.toJson(p.id));
		}
		return ok("you are not member of community");
	}
	
	@Transactional
	public static Result getAllQuestionsOfCommunity(Long id) {
		logger.underlyingLogger().debug("getAllQuestionsOfCommunity");
		final User localUser = Application.getLocalUser(session());
		final Community community = Community.findById(id);
		return ok(Json.toJson(QnAPostsVM.qnaPosts(community, localUser)));
	}
	
	@Transactional
	public static Result getAllIcons() {
		logger.underlyingLogger().debug("getAllIcons");
		List<Icon> icons = Icon.getAllIcons();
		
		List<IconVM> iconVMs = new ArrayList<>();
		for(Icon icon : icons) {
			IconVM vm = new IconVM(icon);
			iconVMs.add(vm);
		}
		return ok(Json.toJson(iconVMs));
	}
	
	@Transactional
	public static Result getAllUnjoinedUsers(Long comm_id, String query) {
		logger.underlyingLogger().debug("getAllUnjoinedUsers");
		Community community = Community.findById(comm_id);
		List<User> nonMembers = community.getNonMembersOfCommunity(query);
		List<SocialObjectVM> objectVMs = new ArrayList<>();
		
		SocialObjectVM object;
		for(User user : nonMembers) {
			object = new SocialObjectVM(user.id.toString(), user.name, "");
			objectVMs.add(object);
		}
		return ok(Json.toJson(objectVMs));
	}
	@Transactional
	public static Result sendInviteToJoinCommunity(Long community_id, Long user_id) {
		logger.underlyingLogger().debug("sendInviteToJoinCommunity");
		Community community = Community.findById(community_id);
		User invitee = User.findById(user_id);
		
		try {
			community.sendInviteToJoin(invitee);
		} catch (SocialObjectNotJoinableException e) {
			e.printStackTrace();
		}
		return ok();
	}
	
	@Transactional
	public static Result getUnknownCommunities(Integer offset) {
		logger.underlyingLogger().debug("getUnknownCommunities");
		int start = offset * 3 + 3;
		List<CommunitiesWidgetChildVM> communityVM = new ArrayList<>();
		
		final User localUser = Application.getLocalUser(session());
		List<Community> communities = localUser.getListOfNotJoinedCommunities(start, 5);
		
		for (Community community : communities) {
			CommunitiesWidgetChildVM comVM = new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType);
			comVM.isO = (localUser == community.owner) ? true : false;
			communityVM.add(comVM);
		}
		return ok(Json.toJson(communityVM));
	}
	
	@Transactional
	public static Result getMyNextCommunities(Integer offset) {
		logger.underlyingLogger().debug("getMyNextCommunities");
		int start = offset * 3 + 3;
		List<CommunitiesWidgetChildVM> communityVM = new ArrayList<>();
		
		final User localUser = Application.getLocalUser(session());
		List<Community> communities = localUser.getListOfJoinedCommunities(start, 3);
		
		for (Community community : communities) {
			CommunitiesWidgetChildVM comVM = new CommunitiesWidgetChildVM(
			        community.id, (long)community.getMembers().size(), community.name, "", 
			        community.iconName, community.communityType);
			comVM.isO = (localUser == community.owner) ? true : false;
			communityVM.add(comVM);
		}
		return ok(Json.toJson(communityVM));
	}
	
	@Transactional
	public static Result getMyUpdates(Long timestamps){
		logger.underlyingLogger().debug("getMyUpdates");
		final User localUser = Application.getLocalUser(session());
		
		List<CommunityPostVM> posts = new ArrayList<>();
		for(Post p :localUser.getMyUpdates(timestamps)) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
			posts.add(post);
		}
		NewsFeedVM vm = new NewsFeedVM(localUser, posts);
		return ok(Json.toJson(vm));
	}
	
	@Transactional
	public static Result getNewsfeeds(int offset) {
		final User localUser = Application.getLocalUser(session());
		List<CommunityPostVM> posts = new ArrayList<>();
		
		List<Post> newsFeeds = localUser.getNewsfeedsAtHomePage(offset, 5);
		
		if(newsFeeds != null ){
			for(Post p : newsFeeds) {
				CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
				posts.add(post);
			}
		}
		
		NewsFeedVM vm = new NewsFeedVM(localUser, posts);
		return ok(Json.toJson(vm));
	}
	
	@Transactional
	public static Result getMyLiveUpdates(Long timestamps){
		logger.underlyingLogger().debug("getMyLiveUpdates");
		final User localUser = Application.getLocalUser(session());
		
		List<CommunityPostVM> posts = new ArrayList<>();
		for(Post p :localUser.getMyLiveUpdates(timestamps)) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
			posts.add(post);
		}
		
		NewsFeedVM vm = new NewsFeedVM(localUser, posts);
		
		return ok(Json.toJson(vm));
	}
	
	@Transactional
	public static Result getNextNewsFeeds(Long timestamp) {
		logger.underlyingLogger().debug("getNextNewsFeeds");
		final User localUser = Application.getLocalUser(session());
		List<CommunityPostVM> posts = new ArrayList<>();
		for(Post p :localUser.getMyNextNewsFeeds(timestamp)) {
			CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
			posts.add(post);
		}
		
		return ok(Json.toJson(posts));
	}
	
	@Transactional
	public static Result getOriginalPostImageByID(Long id) {
		logger.underlyingLogger().debug("getOriginalPostImageByID");
		Resource resource = Resource.findById(id);
		return ok(resource.getRealFile());
	}
	
	@Transactional
	public static Result likeThePost(Long post_id) {
		logger.underlyingLogger().debug("likeThePost");
		User loggedUser = Application.getLocalUser(session());
		Post post = Post.findById(post_id);
		post.noOfLikes++;
		post.onLikedBy(loggedUser);
		return ok();
	}
	
	@Transactional
	public static Result unlikeThePost(Long post_id) throws SocialObjectNotLikableException {
		logger.underlyingLogger().debug("unlikeThePost");
		User loggedUser = Application.getLocalUser(session());
		Post post = Post.findById(post_id);
		post.noOfLikes--;
		loggedUser.doUnLike(post_id, post.objectType);
		return ok();
	}
	
	@Transactional
	public static Result likeTheComment(Long comment_id) {
		logger.underlyingLogger().debug("likeTheComment");
		User loggedUser = Application.getLocalUser(session());
		Comment comment = Comment.findById(comment_id);
		System.out.println("GOT IT :: "+comment.noOfLikes);
		comment.noOfLikes++;
		System.out.println("GOT IT :: "+comment.noOfLikes);
		comment.onLikedBy(loggedUser);
		return ok();
	}
	
	@Transactional
	public static Result unlikeTheComment(Long comment_id) throws SocialObjectNotLikableException {
		logger.underlyingLogger().debug("unlikeTheComment");
		User loggedUser = Application.getLocalUser(session());
		Comment comment = Comment.findById(comment_id);
		comment.noOfLikes--;
		loggedUser.doUnLike(comment_id, comment.objectType);
		return ok();
	}
	
	@Transactional
	public static Result doBookmark(Long post_id){
		logger.underlyingLogger().debug("doBookmark");
		User loggedUser = Application.getLocalUser(session());
		Post post = Post.findById(post_id);
		post.onBookmarkedBy(loggedUser);
		return ok();
	}
	
	@Transactional
	public static Result doUnBookmark(Long post_id){
		logger.underlyingLogger().debug("doUnBookmark");
		User loggedUser = Application.getLocalUser(session());
		Post post = Post.findById(post_id);
		loggedUser.unBookmarkOn(post_id, post.objectType);
		return ok();
	}
	
	@Transactional
	public static Result getBookmarkPosts(int offset) {
		logger.underlyingLogger().debug("getBookmarkPosts");
		final User localUser = Application.getLocalUser(session());
		List<CommunityPostVM> posts = new ArrayList<>();
		List<Post> bookmarkPost = localUser.getBookamrkPost(offset, 5);
		if(bookmarkPost != null ){
			for(Post p : bookmarkPost) {
				CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
				posts.add(post);
			}
		}
		return ok(Json.toJson(posts));
	}
}

	
	
	
