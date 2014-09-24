package controllers;

import static play.data.Form.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import common.utils.ImageFileUtil;
import common.utils.NanoSecondStopWatch;
import models.Comment;
import models.Community;
import models.Community.CommunityType;
import models.TargetingSocialObject.TargetingType;
import models.CommunityCategory;
import models.Emoticon;
import models.Icon;
import models.Post;
import models.Resource;
import models.TargetingSocialObject;
import models.User;
import models.UserCommunityAffinity;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import security.CommunityPermission;
import targeting.community.BusinessFeedCommTargetingEngine;
import targeting.community.NewsfeedCommTargetingEngine;
import viewmodel.CommunitiesParentVM;
import viewmodel.CommunitiesWidgetChildVM;
import viewmodel.CommunityCategoryVM;
import viewmodel.CommunityPostCommentVM;
import viewmodel.CommunityPostVM;
import viewmodel.CommunityVM;
import viewmodel.IconVM;
import viewmodel.MemberWidgetParentVM;
import viewmodel.MembersWidgetChildVM;
import viewmodel.NewsFeedVM;
import viewmodel.CommunityPostsVM;
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
    public static Result getZodiacYearCommunities() {
        List<CommunitiesWidgetChildVM> vms = 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.ZODIAC_YEAR);
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(vms.size(), vms);
        return ok(Json.toJson(communitiesVM));
    }

    @Transactional
    public static Result getZodiacYearMonthCommunities() {
        List<CommunitiesWidgetChildVM> vms = 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.ZODIAC_YEAR_MONTH);
        
        List<CommunitiesWidgetChildVM> result = new ArrayList<CommunitiesWidgetChildVM>();
        DateTime maxYearMonth = new DateTime().plusMonths(10);
        for (CommunitiesWidgetChildVM vm : vms) {
            try {
                int year = Integer.parseInt(vm.targetingInfo.substring(0, 4));  // e.g. 2013_08
                int month = Integer.parseInt(vm.targetingInfo.substring(5));
                if (new DateTime(year, month, 1, 0, 0, 0).isBefore(maxYearMonth)) {
                    result.add(vm);
                }
            } catch (NumberFormatException e) {
                logger.underlyingLogger().error(String.format("[c=%d] targetingInfo not integer year", vm.id));
            }
        }
        
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(result.size(), result);
        return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
    public static Result getDistrictCommunities() {
        List<CommunitiesWidgetChildVM> vms = 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.LOCATION_DISTRICT);
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(vms.size(), vms);
        return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
    public static Result getOtherCommunities() {
        List<CommunitiesWidgetChildVM> vms = 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.SOON_MOMS_DADS);
        vms.addAll( 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.NEW_MOMS_DADS));
        vms.addAll( 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.ALL_MOMS_DADS));
        vms.addAll(
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.PUBLIC));
        vms.addAll( 
                getCommunitiesByTargetingType(TargetingSocialObject.TargetingType.PRE_NURSERY));
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(vms.size(), vms);
        return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
    public static List<CommunitiesWidgetChildVM> getCommunitiesByTargetingType(TargetingType targetingType) {
    
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();

        List<Community> communities = Community.findByTargetingType(targetingType);
        for(Community community : communities) {
            communityList.add(new CommunitiesWidgetChildVM(community, localUser));
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug(String.format("[u=%d][targetingType=%s] getCommunitiesByTargetingType. Took"+sw.getElapsedMS()+"ms", localUser.id, targetingType.name()));
        }
        return communityList;
    }
    
    /**
     * Invoked by suggested communities widget
     * @return
     */
    @Transactional
    public static Result getUserUnJoinedCommunities() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

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
        
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(unjoinedCommunities.size(), communityList);
        communitiesVM.isMore = false;

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getUserUnJoinedCommunities. Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
    public static Result uploadPhotoOfPost() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String postId = form.get("postId");
        if (logger.isDebugEnabled()) {
            logger.underlyingLogger().debug("uploadPhotoOfPost(p="+postId+")");
        }

        FilePart picture = request().body().asMultipartFormData().getFile("post-photo0");
        if (picture == null) {
            return status(500);
        }
        
        String fileName = picture.getFilename();
        File file = picture.getFile();
        try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
            Post post = Post.findById(Long.valueOf(postId));
            Long id = post.addPostPhoto(fileTo).id;
            return ok(id.toString());
        } catch (IOException e) {
            logger.underlyingLogger().error("Error in uploadPhotoOfPost", e);
            return status(500);
        }
    }
    
    @Transactional
    public static Result getCommunityInfoById(Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        final Community community = Community.findById(id);
        if (community == null) {
            logger.underlyingLogger().warn(String.format("[c=%d] Community not exist", id));
            return status(500);
        }

        //if(localUser.isMemberOf(community) || community.owner.id == localUser.id || community.communityType.toString().equals("OPEN") && localUser.isMemberOf(community) == false || community.communityType.toString().equals("CLOSE") && localUser.isMemberOf(community) == true){
        if(community.objectType == SocialObjectType.COMMUNITY) {
            if (User.isLoggedIn(localUser)) {
                UserCommunityAffinity.onCommunityView(localUser.getId(), community.getId());
            }

            CommunityVM communityVM = CommunityVM.communityVM(community, localUser);
    
            sw.stop();
            logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+id+"] getCommunityInfoById. Took "+sw.getElapsedMS()+"ms");
            return ok(Json.toJson(communityVM));
        }
        return ok();
    }
    
    @Transactional
    public static Result getEditCommunityInfo(Long id) {
        final User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][c="+id+"] getEditCommunityInfo");
        }

        final Community community = Community.findById(id);
        if(community.owner.id == localUser.id) {
            return ok(Json.toJson(CommunityVM.communityVM(community, localUser)));
        }
        return status(500);
    }
    
    @Transactional
    public static Result getPostImageById(Long id) {
    	response().setHeader("Cache-Control", "max-age=604800");
        return ok(Resource.findById(id).getThumbnailFile());
    }
    
    @Transactional
    public static Result getMiniCoverCommunityImageById(Long id) {
        response().setHeader("Cache-Control", "max-age=1");
        Community community = Community.findById(id);
        if(community != null && community.getPhotoProfile() != null) {
            return ok(new File(community.getPhotoProfile().getMini()));
        }
        try {
            return ok(Community.getDefaultMiniCoverPhoto());
        } catch (FileNotFoundException e) {
            return ok("no image set");
        }
    }
    
    @Transactional
    public static Result getThumbnailCoverCommunityImageById(Long id) {
        response().setHeader("Cache-Control", "max-age=1");
        final Community community = Community.findById(id);
        if(community != null && community.getPhotoProfile() != null) {
            return ok(new File(community.getPhotoProfile().getThumbnail()));
        }
        try {
            return ok(Community.getDefaultThumbnailCoverPhoto());
        } catch (FileNotFoundException e) {
            return ok("no image set");
        }
    }
    
    @Transactional
    public static Result getFullCoverCommunityImageById(Long id)  {
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[c="+id+"] getFullCoverCommunityImageById");
        }

        final Community community = Community.findById(id);
        if(community.getPhotoProfile() != null) {
            return ok(community.getPhotoProfile().getRealFile());
        }
        try {
            return ok(Community.getDefaultCoverPhoto());
        } catch (FileNotFoundException e) {
            return ok("no image set");
        }
    }
    
    @Transactional
    public static Result getCommunityImageById(Long id) {
        response().setHeader("Cache-Control", "max-age=10");
        final Community community = Community.findById(id);
        if(community != null && community.getPhotoProfile() != null) {
            return ok(new File(community.getPhotoProfile().getThumbnail()));
        }
        try {
            return ok(Community.getDefaultThumbnailCoverPhoto());
        } catch (FileNotFoundException e) {
            return ok("no image set");
        }
    }
    
    @Transactional
    public static Result getMyCommunities() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();

        List<Community> communities = localUser.getListOfJoinedCommunities();
        for(Community community : communities) {
            communityList.add(new CommunitiesWidgetChildVM(community, localUser));
        }
        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(communityList.size(), communityList);

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getMyCommunities. Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
    public static Result getUserCommunities(Long id) {
        logger.underlyingLogger().debug("getUserCommunities");
        final User user = User.findById(id);
        final User localUser = Application.getLocalUser(session());
        List<CommunitiesWidgetChildVM> communityList = new ArrayList<>();
        for(Community community : user.getListOfJoinedCommunities()) {
            CommunitiesWidgetChildVM vm = new CommunitiesWidgetChildVM(community, localUser); 
            communityList.add(vm);
        }

        CommunitiesParentVM communitiesVM = new CommunitiesParentVM(communityList.size(), communityList);
        return ok(Json.toJson(communitiesVM));
    }

    @Transactional
    public static Result getAllComments(Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        Post post = Post.findById(id);
        List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
        List<Comment> comments = post.getCommentsOfPost();
        for(Comment comment : comments) {
            CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment, localUser);
            commentsToShow.add(commentVM);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][p="+id+"] getAllComments count="+comments.size()+". Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(commentsToShow));
    }
    
    @Transactional
    public static Result getAllAnswers(Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        Post post = Post.findById(id);
        List<CommunityPostCommentVM> commentsToShow = new ArrayList<>();
        List<Comment> comments = post.getCommentsOfPost();
        for(Comment comment : comments) {
            CommunityPostCommentVM commentVM = CommunityPostCommentVM.communityPostCommentVM(comment, localUser);
            commentsToShow.add(commentVM);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][p="+id+"] getAllAnswers count="+comments.size()+". Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(commentsToShow));
    }
    
    @Transactional
    public static Result sendJoinRequest(String id) {
        final User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][c="+id+"] sendJoinRequest Community");
        }

        Community community = Community.findById(Long.parseLong(id));
        try {
            localUser.requestedToJoin(community);
        } catch (SocialObjectNotJoinableException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        
        return ok();
    }
    
    @Transactional
    public static Result getNextPosts(String id,String offset,String time) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        Community community = Community.findById(Long.parseLong(id));
        int start = (Integer.parseInt(offset) * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) + DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT;
        List<CommunityPostVM> postsVM = new ArrayList<>();
        List<Post> posts = community.getPostsOfCommunityByTime(start, Long.parseLong(time));
        for(Post p: posts) {
            CommunityPostVM post = CommunityPostVM.communityPostVM(p, localUser);
            postsVM.add(post);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][c="+id+"] getNextPosts(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(postsVM));
    }
    
    @Transactional
    public static Result getNextQnAs(String id,String offset,String time) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        Community community = Community.findById(Long.parseLong(id));
        int start = (Integer.parseInt(offset) * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT) + DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT;
        List<CommunityPostVM> postsVM = new ArrayList<>();
        List<Post> posts =  community.getQuestionsOfCommunityByTime(Long.parseLong(time));
        for(Post p: posts) {
            CommunityPostVM post = CommunityPostVM.communityPostVM(p, localUser);
            postsVM.add(post);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][c="+id+"] getNextQnAs(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(postsVM));
    }
    
    @Transactional
    public static Result uploadCoverPhoto(Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        Community community = Community.findById(id);
        FilePart picture = request().body().asMultipartFormData().getFile("profile-photo");
        String fileName = picture.getFilename();

        File file = picture.getFile();
        try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
            community.setCoverPhoto(fileTo);
        } catch (IOException e) {
            logger.underlyingLogger().error("Error in uploadCoverPhoto", e);
            return status(500);
        }

        sw.stop();
        logger.underlyingLogger().info("[c="+id+"] uploadCoverPhoto Community. Took "+sw.getElapsedMS()+"ms");
        return ok();
    }
    
    @Transactional
    public static Result createCommunity() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Form<Community> form =
                DynamicForm.form(Community.class).bindFromRequest(
                        "name","description","icon","communityType");
        Community community = form.get();
        community.description = Emoticon.replace(community.description);
        
        if (community.communityType == null) {
            community.communityType = CommunityType.OPEN;
        }

        logger.underlyingLogger().info("[u="+localUser.id+"] createCommunity. name="+community.getName()+", type="+community.communityType);

        if (!community.checkCommunityNameExists()) {
            return status(505, String.format("Community name '%s' already exists", community.getName()));
        }
        
        FilePart picture = request().body().asMultipartFormData().getFile("cover-photo");
        String fileName = picture.getFilename();
        File file = picture.getFile();
        try {
            Community newCommunity = localUser.createCommunity(
                    community.name, community.description, community.communityType, community.icon);
            if (newCommunity == null) {
                return status(505, "Failed to create community. Invalid parameters.");
            }
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
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
        Form<String> form = DynamicForm.form(String.class).bindFromRequest();
        Map<String, String> dataToUpdate = form.data();
        String communityId = dataToUpdate.get("id");

        Community community = Community.findById(Long.parseLong(communityId));
        community.name = dataToUpdate.get("n");
        community.description = dataToUpdate.get("d");
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
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        
        community.icon = dataToUpdate.get("icon");
        community.merge();

        logger.underlyingLogger().debug("[c="+community.id+"] updateCommunityProfileData");
        return ok("true");
    }
    
    @Transactional
    public static Result commentOnCommunityPost() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        DynamicForm form = form().bindFromRequest();
        Long postId = Long.parseLong(form.get("post_id"));
        String commentText = Emoticon.replace(form.get("commentText"));
        
        Post p = Post.findById(postId);
        Community c = p.community;
        if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
            try {
                //NOTE: Currently commentType is hardcoded to SIMPLE
                Comment comment = (Comment) p.onComment(localUser, commentText, CommentType.SIMPLE);

                String withPhotos = form.get("withPhotos");
                if(Boolean.parseBoolean(withPhotos)) {
                	comment.ensureAlbumExist();
                }
                p.setUpdatedDate(new Date());
                p.merge();

                sw.stop();
                logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+c.id+"][p="+postId+"] commentOnCommunityPost - photo="+withPhotos+". Took "+sw.getElapsedMS()+"ms");

                return ok(Json.toJson(comment.id));
            } catch (SocialObjectNotCommentableException e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            }
            return ok("Error in creating comment");
        }
        return ok("You are not a member of the community");
    }
    
    @Transactional
    public static Result postOnCommunity() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        DynamicForm form = form().bindFromRequest();
        Long communityId = Long.parseLong(form.get("community_id"));

        Community c = Community.findById(communityId);
        if (CommunityPermission.canPostOnCommunity(localUser, c)) {
            String postText = Emoticon.replace(form.get("postText"));
            boolean withPhotos = Boolean.parseBoolean(form.get("withPhotos"));

            Post p = (Post) c.onPost(localUser, null, postText, PostType.SIMPLE);
            if(withPhotos) {
                p.ensureAlbumExist();
            }

            p.indexPost(withPhotos);

            sw.stop();
            logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+c.id+"] postOnCommunity - photo="+withPhotos+". Took "+sw.getElapsedMS()+"ms");
            return ok(Json.toJson(p.id));
        }
        
        return ok("First join the Community");
    }
    
    @Transactional
    public static Result deletePost(Long postId) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(postId);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug(String.format("[u=%d][c=%d][p=%d] deletePost", localUser.id, post.community.id, postId));
        }

        if (localUser.equals(post.owner) || 
                localUser.isSuperAdmin()) {
            post.delete(localUser);
            User.unBookmarkAllUsersOn(post.id, post.objectType);    // unbookmark for all users
            return ok();
        }
        return status(500, "Failed to delete post. [u=" + localUser.id + "] not owner of post [id=" + postId + "].");
    }
    
    @Transactional
    public static Result deleteComment(Long commentId) {
        final User localUser = Application.getLocalUser(session());
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug(String.format("[u=%d][cmt=%d] deleteComment", localUser.id, commentId));
        }

        Comment comment = Comment.findById(commentId);
        if (localUser.equals(comment.owner) ||
                localUser.isSuperAdmin()) {
            comment.delete(localUser);
            return ok();
        }
        return status(500, "Failed to delete comment. [u="+localUser.id+"] not owner of comment [id=" + commentId + "].");
    }
    
    @Transactional
    public static Result joinToCommunity(Long id) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        logger.underlyingLogger().debug(String.format("[u=%d][c=%d] joinToCommunity", localUser.id, id));
        
        Community community = Community.findById(id);
        try {
            localUser.requestedToJoin(community);
        } catch (SocialObjectNotJoinableException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return ok();
    }
    
    @Transactional
    public static Result leaveThisCommunity(Long community_id) {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Community community = Community.findById(community_id);

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug(String.format("[u=%d] [c=%d] leaveThisCommunity", localUser.id, community.id));
        }

        localUser.leaveCommunity(community);
        
        return ok();
    }
    
    @Transactional
    public static Result getCommunityMembers(Long id) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        Community community = Community.findById(id);
        List<MembersWidgetChildVM> members = new ArrayList<>();
        for(User member : community.getMembers()) {
            if(community.owner.equals(member)) {
                members.add(new MembersWidgetChildVM(member.id, member.displayName,true));
            } else {
                members.add(new MembersWidgetChildVM(member.id, member.displayName,false));
            }
        }
        MemberWidgetParentVM communitiesVM = new MemberWidgetParentVM(members.size(), members);

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[c="+id+"] getCommunityMembers. Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(communitiesVM));
    }
    
    @Transactional
    public static Result postQuestionOnCommunity() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        DynamicForm form = DynamicForm.form().bindFromRequest();
        Long communityId = Long.parseLong(form.get("community_id"));
        String questionTitle = Emoticon.replace(form.get("questionTitle"));
        String questionText = Emoticon.replace(form.get("questionText"));

        Community c = Community.findById(communityId);
        if (CommunityPermission.canPostOnCommunity(localUser, c)) {
            String withPhotos = form.get("withPhotos");
            
            Post p = (Post) c.onPost(localUser, questionTitle, questionText, PostType.QUESTION);
            if(Boolean.parseBoolean(withPhotos)) {
                p.ensureAlbumExist();
            }

            p.indexPost(Boolean.parseBoolean(withPhotos));

            sw.stop();
            logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+c.id+"] postQuestionOnCommunity - photo="+withPhotos+". Took "+sw.getElapsedMS()+"ms");

            return ok(Json.toJson(p.id));
        }
        return ok("You are not member of this community");
    }
    
    @Transactional
    public static Result answerToQuestionOnQnACommunity() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        DynamicForm form = form().bindFromRequest();
        Long postId = Long.parseLong(form.get("post_id"));
        String answerText = Emoticon.replace(form.get("answerText"));
        
        Post p = Post.findById(postId);
        Community c = p.community;
        if(localUser.isMemberOf(c) == true || localUser.id.equals(c.owner.id)){
            try {
                Comment comment = (Comment) p.onComment(localUser, answerText, CommentType.ANSWER);

                String withPhotos = form.get("withPhotos");
                if(Boolean.parseBoolean(withPhotos)) {
                	comment.ensureAlbumExist();
                }
                p.setUpdatedDate(new Date());
                p.merge();

                sw.stop();
                logger.underlyingLogger().info("STS [u="+localUser.id+"][c="+c.id+"][p="+postId+"] answerToQuestionOnQnACommunity - photo="+withPhotos+". Took "+sw.getElapsedMS()+"ms");

                return ok(Json.toJson(comment.id));
            } catch (SocialObjectNotCommentableException e) {
                logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
            }
            return ok("Error in creating answer");
        }
        return ok("You are not a member of the community");
    }
    
    @Transactional
    public static Result getAllQuestionsOfCommunity(Long id) {
        final User localUser = Application.getLocalUser(session());
        final Community community = Community.findById(id);
        List<Post> posts = community.getQuestionsOfCommunity(0, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        
        CommunityPostsVM postsVM = CommunityPostsVM.posts(community, localUser, posts);
    
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][c="+id+"] getAllQuestionsOfCommunity="+postsVM.posts.size());
        }
        return ok(Json.toJson(postsVM));
    }

    @Transactional
    public static Result getAllPostsOfCommunity(Long id) {
        final User localUser = Application.getLocalUser(session());
        final Community community = Community.findById(id);
        List<Post> posts = community.getPostsOfCommunity(0, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        
        CommunityPostsVM postsVM = CommunityPostsVM.posts(community, localUser, posts);
    
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"][c="+id+"] getAllPostsOfCommunity="+postsVM.posts.size());
        }
        return ok(Json.toJson(postsVM));
    }
    
    @Transactional
    public static Result getCommunityIcons() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();
        List<Icon> icons = Icon.getCommunityIcons();
        
        List<IconVM> iconVMs = new ArrayList<>();
        for(Icon icon : icons) {
            IconVM vm = new IconVM(icon);
            iconVMs.add(vm);
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("getCommunityIcons. Took "+sw.getElapsedMS()+"ms");
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
            if (user.system) {
                continue;
            }
            
            object = new SocialObjectVM(user.id.toString(), user.displayName, "");
            objectVMs.add(object);
        }
        return ok(Json.toJson(objectVMs));
    }

    @Transactional
    public static Result sendInviteToJoinCommunity(Long community_id, Long user_id) {
        logger.underlyingLogger().info("[c="+community_id+"][recv="+user_id+"] sendInviteToJoinCommunity");

        Community community = Community.findById(community_id);
        User invitee = User.findById(user_id);
        if (invitee.system) {
            logger.underlyingLogger().info("[c="+community_id+"][recv="+user_id+"] Cannot send invitation to system users");
            return status(505, "Cannot send invitation to system users");
        }
        
        try {
            community.sendInviteToJoin(invitee);
        } catch (SocialObjectNotJoinableException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
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

    /**
     * Play routes AJAX call
     * @param offset
     * @return
     */
    @Transactional
    public static Result getNewsfeeds(int offset) {
        final User localUser = Application.getLocalUser(session());

        // reloading newsfeed
        if(offset == 0) {
            logger.underlyingLogger().info("STS [u="+localUser.id+"][name="+localUser.displayName+"] Reloading social newsfeed");
            // Re-index user's community feed
            NewsfeedCommTargetingEngine.indexCommNewsfeedForUser(localUser.getId());
    	}

        List<Post> newsFeeds = localUser.getFeedPosts(true, offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);

        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<CommunityPostVM> posts = new ArrayList<>();
        if (newsFeeds != null) {
            final boolean isCommentable = true;    // must be open for social NF entries

            for (Post p : newsFeeds) {
                CommunityPostVM post = CommunityPostVM.communityPostVM(p, localUser, isCommentable);
                posts.add(post);
            }
        }
        
        NewsFeedVM vm = new NewsFeedVM(localUser, posts);

        sw.stop();
        logger.underlyingLogger().info("[u="+localUser.id+"] getNewsfeeds(offset="+offset+") count="+posts.size()+". vm create Took "+sw.getElapsedMS()+"ms");
        return ok(Json.toJson(vm));
    }

    /**
     * Play routes AJAX call
     * @param offset
     * @return
     */
    @Transactional
    public static Result getBusinessfeedsByCategory(int offset, Long communityCategoryId) {
        final User localUser = Application.getLocalUser(session());

        // reloading business feed
        if(offset == 0) {
            logger.underlyingLogger().info("STS [u="+localUser.id+"][name="+localUser.displayName+"][cat="+communityCategoryId+"] Reloading business newsfeed");
            // Re-index user's biz feed
            BusinessFeedCommTargetingEngine.indexBusinessNewsfeedForUser(localUser.getId(), communityCategoryId);
    	}

        List<Post> newsFeeds = localUser.getFeedPosts(false, offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);

        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<CommunityPostVM> posts = new ArrayList<>();
        if (newsFeeds != null) {
            final boolean isMember = false;    // must NOT be member for biz NF entries

            for (Post p : newsFeeds) {
                CommunityPostVM post = CommunityPostVM.communityPostVM(p, localUser, isMember);
                posts.add(post);
            }
        }

        NewsFeedVM vm = new NewsFeedVM(localUser, posts);

        sw.stop();
        logger.underlyingLogger().info("[u="+localUser.id+"][cat="+communityCategoryId+"] getBusinessfeeds(offset="+offset+") count="+posts.size()+". vm create Took "+sw.getElapsedMS()+"ms");
        return ok(Json.toJson(vm));
    }

    @Transactional
    public static Result getBusinessfeeds(int offset) {
        return getBusinessfeedsByCategory(offset, 0L);
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
        response().setHeader("Cache-Control", "max-age=604800");
        Resource resource = Resource.findById(id);
        return ok(resource.getRealFile());
    }
    
    @Transactional
    public static Result likeThePost(Long post_id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(post_id);
        post.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result unlikeThePost(Long post_id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(post_id);
        post.onUnlikedBy(localUser);
        localUser.doUnLike(post_id, post.objectType);
        return ok();
    }

    @Transactional
    public static Result wantAnswerFromQuestion(Long post_id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(post_id);
        post.onWantAnswerBy(localUser);
        return ok();
    }

    @Transactional
    public static Result unwantAnswerFromQuestion(Long post_id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(post_id);
        post.onUnwantAnswerBy(localUser);
        localUser.doUnwantAnswer(post_id, post.objectType);
        return ok();
    }
    
    @Transactional
    public static Result likeTheComment(Long comment_id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Comment comment = Comment.findById(comment_id);
        comment.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result unlikeTheComment(Long comment_id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Comment comment = Comment.findById(comment_id);
        comment.onUnlikedBy(localUser);
        localUser.doUnLike(comment_id, comment.objectType);
        return ok();
    }
    
    @Transactional
    public static Result doBookmark(Long post_id){
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(post_id);
        post.onBookmarkedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result doUnBookmark(Long post_id){
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Post post = Post.findById(post_id);
        localUser.unBookmarkOn(post_id, post.objectType);
        return ok();
    }
    
    @Transactional
    public static Result getBookmarkedPosts(int offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        List<CommunityPostVM> posts = new ArrayList<>();
        List<Post> bookmarkedPosts = localUser.getBookmarkedPosts(offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        if(bookmarkedPosts != null ){
            for(Post p : bookmarkedPosts) {
                CommunityPostVM post = CommunityPostVM.communityPostVM(p,localUser);
                posts.add(post);
            }
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getBookmarkedPosts. Took "+sw.getElapsedMS()+"ms");
        }
        return ok(Json.toJson(posts));
    }
	
    @Transactional
	public static Result uploadCommentPhoto() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String commentId = form.get("commentId");
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("uploadCommentPhoto(cmt="+commentId+")");
        }

		FilePart picture = request().body().asMultipartFormData().getFile("comment-photo0");
		String fileName = picture.getFilename();
		File file = picture.getFile();
	    try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
	    	Long id = Comment.findById(Long.valueOf(commentId)).addCommentPhoto(fileTo).id;
	    	return ok(id.toString());
		} catch (IOException e) {
            logger.underlyingLogger().error("Error in uploadCommentPhoto", e);
			return status(500);
		}
	}
	
	@Transactional
	public static Result getCommentImageById(Long id) {
	    response().setHeader("Cache-Control", "max-age=604800");
		return ok(Resource.findById(id).getThumbnailFile());
	}
	
	@Transactional
	public static Result uploadQnACommentPhoto() {
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String commentId = form.get("commentId");
		if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("uploadQnACommentPhoto(cmt="+commentId+")");
        }

		FilePart picture = request().body().asMultipartFormData().getFile("comment-photo0");
		String fileName = picture.getFilename();
		File file = picture.getFile();
	    try {
            File fileTo = ImageFileUtil.copyImageFileToTemp(file, fileName);
	    	Long id = Comment.findById(Long.valueOf(commentId)).addCommentPhoto(fileTo).id;
	    	return ok(id.toString());
		} catch (IOException e) {
			logger.underlyingLogger().error("Error in uploadQnACommentPhoto", e);
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
        post.noOfViews++;
        return ok(Json.toJson(CommunityPostsVM.posts(post.community, localUser, post)));
    }
    
    @Transactional
    public static Result qnaLanding(Long id, Long communityId) {
        logger.underlyingLogger().debug("qnaLanding");
        final User localUser = Application.getLocalUser(session());
        
        final Post post = Post.findById(id);
        if (post == null) {
            return ok("NO_RESULT"); 
        }
        post.noOfViews++;
        return ok(Json.toJson(CommunityPostsVM.posts(post.community, localUser, post)));
    }
    
    @Transactional
    public static Result getAllCommunityCategories() {
        List<CommunityCategory> categories = CommunityCategory.getAllCategories();
        
        List<CommunityCategoryVM> communityCategoryVMs = new ArrayList<>();
        for(CommunityCategory communityCategory : categories) {
            CommunityCategoryVM vm = CommunityCategoryVM.communityCategoryVM(communityCategory);
            communityCategoryVMs.add(vm);
        }
        return ok(Json.toJson(communityCategoryVMs));
    }
}