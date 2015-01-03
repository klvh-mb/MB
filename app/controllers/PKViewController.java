package controllers;

import java.io.File;
import java.util.*;

import common.collection.Pair;
import common.utils.ImageUploadUtil;
import common.utils.NanoSecondStopWatch;
import common.utils.StringUtil;
import domain.DefaultValues;
import domain.PostType;
import domain.SocialObjectType;
import models.Community;
import models.Emoticon;
import models.Post;
import models.PKViewMeta;
import models.User;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import viewmodel.PKViewVM;

/**
 * Controller for PKView
 */
public class PKViewController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PKViewController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("pkview");


    @Transactional
    public static Result postPKOnCommunity() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User user = Application.getLocalUser(session());
        if (!user.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("User not logged in to postPKOnCommunity"));
            return status(500);
        }

        DynamicForm form = DynamicForm.form().bindFromRequest();
        Long communityId = Long.parseLong(form.get("community_id"));
        String pkTitle = Emoticon.replace(form.get("pkTitle"));
        String pkText = Emoticon.replace(form.get("pkText"));
        int shortBodyCount = StringUtil.computePostShortBodyCount(pkText);

        String pkYesText = Emoticon.replace(form.get("pkYesText"));
        String pkNoText = Emoticon.replace(form.get("pkNoText"));

        Community community = Community.findById(communityId);
        if (community == null) {
            logger.underlyingLogger().error("Invalid communityId: "+communityId);
            return status(501);
        }

        // create Post
        Post post = new Post(user, pkTitle, pkText, community);
        post.objectType = SocialObjectType.PK_VIEW;
        post.postType = PostType.PK_VIEW;
        post.shortBodyCount = shortBodyCount;
        post.setUpdatedDate(new Date());
        post.save();
        // create PKViewMeta
        PKViewMeta pkViewMeta = new PKViewMeta(post.id, pkYesText, pkNoText);
        pkViewMeta.save();

        sw.stop();
        logger.underlyingLogger().info("[c="+communityId+"] postPKOnCommunity. Took "+sw.getElapsedMS()+"ms");

        Map<String,String> map = new HashMap<>();
        map.put("id", post.id.toString());

        if (post.shortBodyCount > 0) {
            map.put("text", post.body.substring(0,post.shortBodyCount));
            map.put("showM", "true");
        } else{
            map.put("text", post.body);
            map.put("showM", "false");
        }
        return ok(Json.toJson(map));
    }


    @Transactional
    public static Result getAllPKViews() {
        final User user = Application.getLocalUser(session());
        List<Pair<PKViewMeta, Post>> pkPosts = PKViewMeta.getAllPKViewMeta();

        final List<PKViewVM> vms = new ArrayList<>();
        for (Pair<PKViewMeta, Post> pkPost : pkPosts) {
            PKViewVM vm = new PKViewVM(pkPost.first, pkPost.second, user, true);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }

    @Transactional
    public static Result getPKViewsByCommunity(Long communityId) {
        final User user = Application.getLocalUser(session());
        List<Pair<PKViewMeta, Post>> pkPosts = PKViewMeta.getPKViewsByCommunity(communityId);

        final List<PKViewVM> vms = new ArrayList<>();
        for (Pair<PKViewMeta, Post> pkPost : pkPosts) {
            PKViewVM vm = new PKViewVM(pkPost.first, pkPost.second, user, true);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result infoPKView(Long pkViewMetaId) {
        final User localUser = Application.getLocalUser(session());

        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(pkViewMetaId);
        if (pkView == null) {
            logger.underlyingLogger().error("Invalid pkViewMetaId: "+pkViewMetaId);
            return status(500);
        }
        pkView.second.noOfViews++;                          // TODO: need to save

        PKViewVM vm = new PKViewVM(pkView.first, pkView.second, localUser);
        return ok(Json.toJson(vm));
    }

    @Transactional
    public static Result getBookmarkedPKViews(int offset) {
        final List<PKViewVM> vms = new ArrayList<>();

        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            return ok(Json.toJson(vms));
        }

        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<Pair<PKViewMeta, Post>> pkPosts = localUser.getBookmarkedPKViews(offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        for(Pair<PKViewMeta, Post> pkPost : pkPosts) {
            PKViewVM vm = new PKViewVM(pkPost.first, pkPost.second, localUser);
            vms.add(vm);
        }

        sw.stop();
        logger.underlyingLogger().info("[u="+localUser.id+"] getBookmarkedPKViews - ret="+vms.size()+". Took "+sw.getElapsedMS()+"ms");
        return ok(Json.toJson(vms));
    }

    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        response().setHeader("Cache-Control", "max-age=604800");
        String path = imageUploadUtil.getImagePath(year, month, date, name);

        logger.underlyingLogger().debug("getImage. path="+path);
        return ok(new File(path));
    }
}