package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.mnt.exception.SocialObjectNotLikableException;
import common.utils.ImageUploadUtil;

import models.Campaign;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CampaignVM;

public class CampaignController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(CampaignController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("campaign");
    
    @Transactional
    public static Result getAllCampaigns() {
        List<Campaign> allCampaigns = Campaign.getAllCampaigns();
        List<CampaignVM> listOfCampaigns = new ArrayList<>();
        for (Campaign campaign:allCampaigns) {
            CampaignVM vm = new CampaignVM(campaign, true);
            listOfCampaigns.add(vm);
        }
        return ok(Json.toJson(listOfCampaigns));
    }
    
    @Transactional
    public static Result infoCampaign(Long id) {
        final User localUser = Application.getLocalUser(session());
        Campaign campaign = null;
        try {
            campaign = Campaign.findById(id);
            campaign.noOfViews++;
        } catch(NoResultException e) {
            return ok("NO_RESULT");
        }
        CampaignVM vm = new CampaignVM(campaign, localUser);
        return ok(Json.toJson(vm));
    }
    
    @Transactional
    public static Result onLike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Campaign campaign = Campaign.findById(id);
        campaign.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result onUnlike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        Campaign campaign = Campaign.findById(id);
        campaign.onUnlikedBy(localUser);
        localUser.doUnLike(id, campaign.objectType);
        return ok();
    }
    
    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        response().setHeader("Cache-Control", "max-age=604800");
        String path = imageUploadUtil.getImagePath(year, month, date, name);

        logger.underlyingLogger().debug("getImage. path="+path);
        return ok(new File(path));
    }
}