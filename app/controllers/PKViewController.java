package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mnt.exception.SocialObjectNotLikableException;

import common.utils.ImageUploadUtil;
import common.utils.NanoSecondStopWatch;
import domain.DefaultValues;
import models.PKView;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.PKViewVM;

public class PKViewController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PKViewController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("pkview");
    
    @Transactional
    public static Result getAllPKViews() {
        List<PKView> allPKViews = PKView.getAllPKViews();
        List<PKViewVM> listOfPKViews = new ArrayList<>();
        for (PKView pkView : allPKViews) {
            PKViewVM vm = new PKViewVM(pkView, true);
            listOfPKViews.add(vm);
        }
        return ok(Json.toJson(listOfPKViews));
    }
    
    @Transactional
    public static Result infoPKView(Long pkViewId) {
        final User localUser = Application.getLocalUser(session());
        
        PKView pkView = PKView.findById(pkViewId);
        if (pkView == null) {
            //return ok("NO_RESULT");
            return ok(Json.toJson(new PKViewVM(pkView)));   // TODO
        }
        pkView.noOfViews++;
        PKViewVM vm = new PKViewVM(pkView, localUser);
        return ok(Json.toJson(vm));
    }
    
    @Transactional
    public static Result onLike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        PKView pkView = PKView.findById(id);
        pkView.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result onUnlike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        PKView pkView = PKView.findById(id);
        pkView.onUnlikedBy(localUser);
        localUser.doUnLike(id, pkView.objectType);
        return ok();
    }
    
    @Transactional
    public static Result onBookmark(Long id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        PKView pkView = PKView.findById(id);
        pkView.onBookmarkedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result onUnBookmark(Long id) {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        PKView pkView = PKView.findById(id);
        localUser.unBookmarkOn(id, pkView.objectType);
        return ok();
    }
    
    @Transactional
    public static Result getBookmarkedPKViews(int offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User localUser = Application.getLocalUser(session());
        List<PKViewVM> vms = new ArrayList<>();
        List<PKView> bookmarkPKViews = localUser.getBookmarkedPKViews(offset, DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
        if(bookmarkPKViews != null ){
            for(PKView a : bookmarkPKViews) {
                PKViewVM vm = new PKViewVM(a,localUser);
                vms.add(vm);
            }
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.id+"] getBookmarkedPKViews. Took "+sw.getElapsedMS()+"ms");
        }
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