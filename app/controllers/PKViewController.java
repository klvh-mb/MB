package controllers;

import java.io.File;
import java.util.*;

import com.mnt.exception.SocialObjectNotLikableException;

import common.collection.Pair;
import common.utils.ImageUploadUtil;
import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;
import models.Post;
import models.PKViewMeta;
import models.User;
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
    public static Result getAllPKViews() {
        final User user = Application.getLocalUser(session());
        List<Pair<PKViewMeta, Post>> pkPosts = PKViewMeta.getAllPKViewMeta();

        final List<PKViewVM> vms = new ArrayList<>();
        for (Pair<PKViewMeta, Post> pkPost : pkPosts) {
            PKViewVM vm = new PKViewVM(pkPost.first, pkPost.second, user);
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
            PKViewVM vm = new PKViewVM(pkPost.first, pkPost.second, user);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result listLatestPKView() {
        final User localUser = Application.getLocalUser(session());

        Pair<PKViewMeta, Post> pkView = PKViewMeta.getLatestPKView();
        if (pkView == null) {
            logger.underlyingLogger().error("No latest pkViewMeta");
            return ok("NO_RESULT");
        }
        pkView.second.noOfViews++;                          // TODO: need to save

        PKViewVM vm = new PKViewVM(pkView.first, pkView.second, localUser, true);
        return ok(Json.toJson(vm));
    }
    
    @Transactional
    public static Result infoPKView(Long pkViewMetaId) {
        final User localUser = Application.getLocalUser(session());

        Pair<PKViewMeta, Post> pkView = null;
        if (pkViewMetaId == -1) {
            pkView = PKViewMeta.getLatestPKView();
        } else {
            pkView = PKViewMeta.getPKViewById(pkViewMetaId);
        }
        if (pkView == null) {
            logger.underlyingLogger().error("Invalid pkViewMetaId: "+pkViewMetaId);
            return ok("NO_RESULT");
        }
        pkView.second.noOfViews++;                          // TODO: need to save

        PKViewVM vm = new PKViewVM(pkView.first, pkView.second, localUser);
        return ok(Json.toJson(vm));
    }

    @Transactional
    public static Result onYesVote(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(id);
        pkView.first.onYesVote(localUser, pkView.second);
        return ok();
    }

    @Transactional
    public static Result onNoVote(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }

        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(id);
        pkView.first.onNoVote(localUser, pkView.second);
        return ok();
    }

    @Transactional
    public static Result onLike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(id);
        pkView.second.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result onUnlike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(id);
        pkView.second.onUnlikedBy(localUser);
        localUser.doUnLike(pkView.second.id, pkView.second.objectType);
        return ok();
    }
    
    @Transactional
    public static Result onBookmark(Long id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(id);
        pkView.second.onBookmarkedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result onUnBookmark(Long id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Pair<PKViewMeta, Post> pkView = PKViewMeta.getPKViewById(id);
        localUser.unBookmarkOn(pkView.second.id, pkView.second.objectType);
        return ok();
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