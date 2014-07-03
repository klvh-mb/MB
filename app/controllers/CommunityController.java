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
import models.UserCommunityAffinity;

import org.apache.commons.io.FileUtils;

import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import targeting.community.NewsfeedCommTargetingEngine;
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

import domain.CommentType;
import domain.DefaultValues;
import domain.PostType;
import domain.SocialObjectType;

public class CommunityController extends Controller{
    private static play.api.Logger logger = play.api.Logger.apply(CommunityController.class);

    @Transactional
    public static Result getUserUnJoinCommunity() {
        logger.underlyingLogger().debug("getUserUnJoinCommunity");
        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        int i = 0; 
        List<Community> unjoinedCommunities = localUser.getListOfNotJoinedCommunities();
        for(Community community : unjoinedCommunities) {
            if(i >= DefaultValues.DEFAULT_UTILITY_COUNT) {
                break;
            }
            CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(community, localUser);
            communityList.add(vm);
            i++;
        }
        
        CommunitiesParentVM fwVM = new CommunitiesParentVM(unjoinedCommunities.size(), communityList);
        fwVM.isMore = false;
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

        //if(localUser.isMemberOf(community) || community.owner.id == localUser.id || community.communityType.toString().equals("OPEN") && localUser.isMemberOf(community) == false || community.communityType.toString().equals("CLOSE") && localUser.isMemberOf(community) == true){
        if(community.objectType == SocialObjectType.COMMUNITY) {
            UserCommunityAffinity.onCommunityView(localUser.getId(), community.getId());
            return ok(Json.toJson(CommunityVM.communityVM(community, localUser)));
        } else {
            return ok();
        }
    }
    
    @Transactional
    public static Result getEditCommunityInfo(Long id) {
        final User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"] getEditCommunityInfo(c="+id+")");
        }

        final Community community = Community.findById(id);
        if(community.owner.id == localUser.id) {
            return ok(Json.toJson(CommunityVM.communityVM(community, localUser)));
        }
        return status(500);
    }
    
    @Transactional
    public static Result getPostImageById(Long id) {
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
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getThumbnailCoverCommunityImageById(c="+id+")");
        }

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
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getFullCoverCommunityImageById(c="+id+")");
        }

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
        }
        try {
            return ok(community.getDefaultThumbnailCoverPhoto());
        } catch (FileNotFoundException e) {
            return ok("no image set");
        }
    }
    
    @Transactional
    public static Result getMyCommunities() {
        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        List<Community> communities = localUser.getListOfJoinedCommunities();

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"] getMyCommunities. count="+communities.size());
        }
        for(Community community : communities) {
            communityList.add(new CommunitiesWidgetChildVM(community, localUser));
        }
        CommunitiesParentVM fwVM = new CommunitiesParentVM(communityList.size(), communityList);
        return ok(Json.toJson(fwVM));
    }
    
    @Transactional
    public static Result getCommunitiesOfUser(Long id) {
        logger.underlyingLogger().debug("getCommunitiesOfUser");
        final User user = User.findById(id);
        final User localUser = Application.getLocalUser(session());
        int count=0;
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        List<Community> joinedCommunities = user.getListOfJoinedCommunities();
        for(Community community : joinedCommunities) {
            CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(community, localUser);
            communityList.add(vm);
            ++count;
            if(count == DefaultValues.DEFAULT_UTILITY_COUNT) {
                break;
            }
        }
        CommunitiesParentVM fwVM = new CommunitiesParentVM(joinedCommunities.size(), communityList);
        return ok(Json.toJson(fwVM));
    }
    
    @Transactional
    public static Result getMyAllCommunities() {
        logger.underlyingLogger().debug("getMyAllCommunities");
        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        for(Community community : localUser.getListOfJoinedCommunities()) {
            CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(community, localUser);
            communityList.add(vm);
        }
        CommunitiesParentVM fwVM = new CommunitiesParentVM(localUser.getListOfNotJoinedCommunities().size(), communityList);
        return ok(Json.toJson(fwVM));
    }
    
    @Transactional
    public static Result getAllCommunitiesOfUser(Long id) {
        logger.underlyingLogger().debug("getAllCommunitiesOfUser");
        final User user = User.findById(id);
        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        for(Community community : user.getListOfJoinedCommunities()) {
            CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(community, localUser); 
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
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getNextPosts(c="+id+" offset="+offset+")");
        }

        final User localUser = Application.getLocalUser(session());
        Community community = Community.findById(Long.parseLong(id));
        int start = (Integer.parseInt(offset) * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) + DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT;
        List<CommunityPostVM> postsVM = new ArrayList<>();
        List<Post> posts = community.getPostsOfCommunity(start, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        for(Post p: posts) {
            CommunityPostVM post = CommunityPostVM.communityPostVM(p, localUser);
            postsVM.add(post);
        }
        return ok(Json.toJson(postsVM));
    }
    
    @Transactional
    public static Result getNextQuests(String id,String offset) {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getNextQuests(c="+id+" offset="+offset+")");
        }

        final User localUser = Application.getLocalUser(session());
        Community community = Community.findById(Long.parseLong(id));
        int start = (Integer.parseInt(offset) * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) + DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT;
        List<CommunityPostVM> postsVM = new ArrayList<>();
        List<Post> posts =  community.getQuestionsOfCommunity(start, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        for(Post p: posts) {
            CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
            postsVM.add(post);
        }
        return ok(Json.toJson(postsVM));
    }
    
    
    @Transactional
    public static Result uploadCoverPhoto(Long id) {
        logger.underlyingLogger().info("uploadCoverPhoto(c="+id+")");

        Community community = Community.findById(id);
        FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
        String fileName = picture.getFilename();

        File file = picture.getFile();
        File fileTo = new File(Play.application().configuration().getString("image.temp")+""+fileName);
        
        try {
            FileUtils.copyFile(file, fileTo);
            community.setCoverPhoto(fileTo);
        } catch (IOException e) {
            logger.underlyingLogger().error("Error in uploadCoverPhoto", e);
            return status(500);
        }
        return ok();
    }
    
    @Transactional
    public static Result createCommunity() {
        final User localUser = Application.getLocalUser(session());
        Form<Community> form =
                DynamicForm.form(Community.class).bindFromRequest(
                        "name","description","icon","communityType");
        Community community = form.get();
        if (community.communityType == null) {
            community.communityType = CommunityType.OPEN;
        }

        logger.underlyingLogger().info("[u="+localUser.id+"] createCommunity. name="+community.getName()+", type="+community.communityType);

        if (!community.checkCommunityNameExists()) {
            return status(505, "PLEASE CHOOSE OTHER NAME");
        }
        
        FilePart picture = request().body().asMultipartFormData().getFile("cover-photo");
        String fileName = picture.getFilename();
        File file = picture.getFile();
        File fileTo = new File(Play.application().configuration().getString("image.temp")+""+fileName);
        
        try {
            Community newCommunity = localUser.createCommunity(
                    community.name, community.description,community.communityType, community.icon);
            if (newCommunity == null) {
                return status(505, "Valid param missing");
            }
            FileUtils.copyFile(file, fileTo);
            newCommunity.setCoverPhoto(fileTo);

            // save community affinity for admin
            UserCommunityAffinity.onJoinedCommunity(localUser.id, newCommunity.id);

            return ok(Json.toJson(newCommunity.id));
        } catch (SocialObjectNotJoinableException e) {
            logger.underlyingLogger().error("Error in createCommunity", e);
        } catch (IOException e) {
            logger.underlyingLogger().error("Error in createCommunity", e);
        }
        return status(500);
    }
    
    @Transactional
    public static Result updateCommunityProfileData(){
        logger.underlyingLogger().debug("updateCommunityProfileData");
        Form<String> form = DynamicForm.form(String.class).bindFromRequest();
        Map<String, String> dataToUpdate = form.data();
        String communityId = dataToUpdate.get("id");
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
        
        community.icon = dataToUpdate.get("icon");
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
                String withPhotos = form.get("withPhotos");
                if(Boolean.parseBoolean(withPhotos)) {
                	comment.ensureAlbumExist();
                }
                p.setUpdatedDate(new Date());
                p.merge();
            } catch (SocialObjectNotCommentableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        final User localUser = Application.getLocalUser(session());
        Community community = Community.findById(community_id);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] leaveThisCommunity. community_id="+community_id);
        }

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
        Comment comment =null;
        
        if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
            try {
                comment = (Comment) p.onComment(localUser, answerText, CommentType.ANSWER);

                String withPhotos = form.get("withPhotos");
                if(Boolean.parseBoolean(withPhotos)) {
                	comment.ensureAlbumExist();
                }
                
                p.setUpdatedDate(new Date());
                p.merge();
            } catch (SocialObjectNotCommentableException e) {
                e.printStackTrace();
            }
            
          
            
            return ok(Json.toJson(comment.id));
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
        List<Icon> icons = Icon.getCommunityIcons();
        
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

        // reloading newsfeed
        if(offset == 0) {
    		List<Long> communities = localUser.getListOfJoinedCommunityIds();

            if (logger.underlyingLogger().isDebugEnabled()) {
         	   logger.underlyingLogger().debug("[u="+localUser.getId()+"] indexCommNewsfeed. numJoinedComm="+communities.size());
        	}

            // Re-index user's community feed
            NewsfeedCommTargetingEngine.indexCommNewsfeedForUser(localUser.getId());
    	}
       
        List<CommunityPostVM> posts = new ArrayList<>();
        
        List<Post> newsFeeds = localUser.getNewsfeedsAtHomePage(offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        
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
        User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("likeThePost - u=" + localUser.id + " p=" + post_id);
        }

        Post post = Post.findById(post_id);
        post.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result unlikeThePost(Long post_id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("unlikeThePost - u=" + localUser.id + " p=" + post_id);
        }

        Post post = Post.findById(post_id);
        post.onUnlikedBy(localUser);
        localUser.doUnLike(post_id, post.objectType);
        return ok();
    }
    
    @Transactional
    public static Result likeTheComment(Long comment_id) {
        User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("likeTheComment - u=" + localUser.id + " c=" + comment_id);
        }

        Comment comment = Comment.findById(comment_id);
        comment.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result unlikeTheComment(Long comment_id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("unlikeTheComment - u=" + localUser.id + " c=" + comment_id);
        }

        Comment comment = Comment.findById(comment_id);
        comment.onUnlikedBy(localUser);
        localUser.doUnLike(comment_id, comment.objectType);
        return ok();
    }
    
    @Transactional
    public static Result doBookmark(Long post_id){
        logger.underlyingLogger().debug("doBookmark");
        User localUser = Application.getLocalUser(session());
        Post post = Post.findById(post_id);
        post.onBookmarkedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result doUnBookmark(Long post_id){
        logger.underlyingLogger().debug("doUnBookmark");
        User localUser = Application.getLocalUser(session());
        Post post = Post.findById(post_id);
        localUser.unBookmarkOn(post_id, post.objectType);
        return ok();
    }
    
    @Transactional
    public static Result getBookmarkedPosts(int offset) {
        logger.underlyingLogger().debug("getBookmarkedPosts");
        final User localUser = Application.getLocalUser(session());
        List<CommunityPostVM> posts = new ArrayList<>();
        List<Post> bookmarkedPosts = localUser.getBookmarkedPosts(offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        if(bookmarkedPosts != null ){
            for(Post p : bookmarkedPosts) {
                CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
                posts.add(post);
            }
        }
        return ok(Json.toJson(posts));
    }
	
    @Transactional
	public static Result uploadCommentPhoto() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String commentId = form.get("commentId");
		System.out.println(commentId);
		
		FilePart picture = request().body().asMultipartFormData().getFile("comment-photo0");
		String fileName = picture.getFilename();
		File file = picture.getFile();
	    File fileTo = new File(fileName);
	    // TOBE TESTED
	    try {
	    	FileUtils.copyFile(file, fileTo);
	    	Long id = Comment.findById(Long.valueOf(commentId)).addCommentPhoto(fileTo).id;
	    	return ok(id.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			return status(500);
		}
	}
	
	@Transactional
	public static Result getCommentImageById(Long id) {
		return ok(Resource.findById(id).getThumbnailFile());
	}
	
	@Transactional
	public static Result uploadQnACommentPhoto() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String commentId = form.get("commentId");
		System.out.println(commentId);
		
		FilePart picture = request().body().asMultipartFormData().getFile("comment-photo0");
		String fileName = picture.getFilename();
		File file = picture.getFile();
	    File fileTo = new File(fileName);
	    // TOBE TESTED
	    try {
	    	FileUtils.copyFile(file, fileTo);
	    	Long id = Comment.findById(Long.valueOf(commentId)).addCommentPhoto(fileTo).id;
	    	return ok(id.toString());
		} catch (IOException e) {
			//e.printStackTrace();
			return status(500);
		}
	}

    @Transactional
    public static Result postLanding(Long id, Long communityId) {
        logger.underlyingLogger().debug("postLanding");
        final User localUser = Application.getLocalUser(session());
        final Post post = Post.findById(id);
        if (post == null) {
            return ok("NO_RESULT"); 
        }
        return ok(Json.toJson(CommunityVM.communityVM(post.community, localUser, post)));
    }
    
    @Transactional
    public static Result qnaLanding(Long id, Long communityId) {
        logger.underlyingLogger().debug("qnaLanding");
        final User localUser = Application.getLocalUser(session());
        final Post post = Post.findById(id);
        if (post == null) {
            return ok("NO_RESULT"); 
        }
        return ok(Json.toJson(QnAPostsVM.qnaPosts(post.community, localUser, post)));
    }
}