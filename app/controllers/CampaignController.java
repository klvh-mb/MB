package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import com.mnt.exception.SocialObjectNotLikableException;

import common.utils.ImageUploadUtil;
import models.Campaign;
import models.Campaign.CampaignType;
import models.CampaignActionsMeta;
import models.CampaignActionsUser;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CampaignUserJoinStatusVM;
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
        Campaign campaign = Campaign.findById(id);
        if (campaign == null) {
            return ok("NO_RESULT");
        }
        campaign.noOfViews++;
        CampaignVM vm = new CampaignVM(campaign, localUser);
        return ok(Json.toJson(vm));
    }
    
    @Transactional
    public static Result withdrawCampaign() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            return status(599);
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        Long campaignId = Long.parseLong(form.get("campaignId"));
        Campaign campaign = Campaign.findById(campaignId);
        if (campaign == null) {
            logger.underlyingLogger().error(String.format("[u=%d][c=%d] User tried to withdraw campaign which does not exist", localUser.id, campaignId));
            return status(500);
        }
        
        if (CampaignType.ACTIONS == campaign.campaignType) {
            if (!CampaignActionsUser.isJoinedCampaign(localUser.id, campaign.id)) {
                logger.underlyingLogger().error(String.format("[u=%d][c=%d] User cannot withdraw from campaign he did not join!", localUser.id, campaignId));
                return ok();    // no need to notify user
            }
            CampaignActionsUser.withdrawFromCampaign(localUser.id, campaign.id);
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        } else if (CampaignType.PHOTO_CONTEST == campaign.campaignType) {
            // TODO
        }
        
        logger.underlyingLogger().debug(String.format("[u=%d][c=%d] User withdrew from campaign", localUser.id, campaignId));
        
        return ok();
    }
    
    @Transactional
    public static Result joinCampaign() {
        final User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            return status(599);
        }
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        Long campaignId = Long.parseLong(form.get("campaignId"));
        Campaign campaign = Campaign.findById(campaignId);
        if (campaign == null) {
            logger.underlyingLogger().error(String.format("[u=%d][c=%d] User tried to join campaign which does not exist", localUser.id, campaignId));
            return status(500);
        }
        
        CampaignUserJoinStatusVM vm = null;
        if (CampaignType.ACTIONS == campaign.campaignType) {
            vm = validateUserActions(localUser, campaign);
            if (CampaignActionsUser.isJoinedCampaign(localUser.id, campaign.id)) {
                logger.underlyingLogger().error(String.format("[u=%d][c=%d] User already joined campaign!", localUser.id, campaignId));
                return status(501);
            }
            CampaignActionsUser campaignUser = new CampaignActionsUser(campaign.id, localUser.id);
            campaignUser.save();
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        } else if (CampaignType.PHOTO_CONTEST == campaign.campaignType) {
            // TODO
        }
        
        logger.underlyingLogger().debug(String.format("[u=%d][c=%d] User joined campaign", localUser.id, campaignId));
        
        return ok(Json.toJson(vm));
    }
    
    private static CampaignUserJoinStatusVM validateUserActions(User user, Campaign campaign) {
        return new CampaignUserJoinStatusVM(campaign.id, user.id, true, null); 
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