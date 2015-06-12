package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import campaign.validator.CampaignValidationEngine;
import campaign.validator.ValidationResult;

import com.mnt.exception.SocialObjectNotLikableException;

import common.utils.ImageUploadUtil;
import domain.SocialObjectType;
import email.EDMUtility;
import models.Campaign;
import models.Campaign.CampaignType;
import models.CampaignActionsUser;
import models.CampaignWinner;
import models.CampaignWinner.WinnerState;
import models.GameAccount;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CampaignVM;
import viewmodel.CampaignWinnerVM;
import viewmodel.ResponseStatusVM;

public class CampaignController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(CampaignController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("campaign");
    
    public static Long getJoinedUsersCount(Long campaignId) {
        Campaign campaign = Campaign.findById(campaignId);
        if (campaign == null) {
            return -1L;
        }
        
        Long count = -1L;
        if (CampaignType.ACTIONS == campaign.campaignType) {
            count = CampaignActionsUser.getJoinedUsersCount(campaignId);
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        }
        
        return count;
    }
    
    @Transactional
    public static Result getCampaignAnnouncedWinners(Long campaignId) {
        List<CampaignWinner> winners = CampaignWinner.getWinners(campaignId);
        List<CampaignWinnerVM> vms = new ArrayList<>();
        for (CampaignWinner winner : winners) {
            if (WinnerState.ANNOUNCED.equals(winner.winnerState) || 
                    WinnerState.ACCEPTED.equals(winner.winnerState) || 
                    WinnerState.DELIVERED.equals(winner.winnerState)) {
                CampaignWinnerVM vm = new CampaignWinnerVM(winner);
                vms.add(vm);
            }
        }
        return ok(Json.toJson(vms));
    }
    
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
    public static Result infoCampaign(Long campaignId) {
        final User localUser = Application.getLocalUser(session());
        
        Campaign campaign = null;
        if (campaignId == -1) {
            campaign = Campaign.getActiveCampaign();
        } else {
            campaign = Campaign.findById(campaignId);
        }
        if (campaign == null) {
            return notFound();
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
        String realName = form.get("name");
        String phone = form.get("mobileNumber");
        String email = form.get("email");

        Campaign campaign = Campaign.findById(campaignId);
        if (campaign == null) {
            logger.underlyingLogger().error(String.format("[u=%d][c=%d] User tried to join campaign which does not exist", localUser.id, campaignId));
            return status(500);
        }
        if ((realName != null && StringUtils.isEmpty(realName)) || 
        		(phone != null && StringUtils.isEmpty(phone)) || 
        		(email != null && StringUtils.isEmpty(email))) {
            logger.underlyingLogger().error(String.format("[u=%d][c=%d] User tried to join campaign. Missing contact info.", localUser.id, campaignId));
            return status(502);
        }
        
        ResponseStatusVM status = null;

        switch (campaign.campaignType) {
            case ACTIONS: {
                if (CampaignActionsUser.isJoinedCampaign(localUser.id, campaign.id)) {
                    logger.underlyingLogger().error(String.format("[u=%d][c=%d] User already joined campaign!", localUser.id, campaignId));
                    return status(501);
                }

                status = validateUserActions(localUser, campaign);
                if (status.success) {
                    CampaignActionsUser campaignUser = CampaignActionsUser.getCampaignActionsUser(localUser.id, campaign.id);
                    if (campaignUser != null && campaignUser.withdraw) {
                        // user withdrawn before
                        campaignUser.withdraw = false;
                        campaignUser.setUpdatedDate(new Date());
                        campaignUser.merge();
                    } else {
                        // user newly joined
                        campaignUser = new CampaignActionsUser(campaign.id, localUser.id);
                        campaignUser.setCreatedDate(new Date());
                        campaignUser.save();
                    }
                    logger.underlyingLogger().info(String.format("[u=%d][c=%d] Successfully joined campaign", localUser.id, campaignId));

                    String notifText = "New joiner (u="+localUser.id+") to campaign (c="+campaignId+")";
                    EDMUtility.getInstance().sendMailToMB(notifText, notifText);
                }
                break;
            }
            case QUESTIONS:
            case VOTING: {
                // TODO
                break;
            }
        }

        // Capture contact info from form.
        if (status != null && status.success) {
            GameAccount gameAccount = GameAccount.findByUserId(localUser.id);
            gameAccount.setContactInfo(realName, phone, email);
            gameAccount.save();
        }

        return ok(Json.toJson(status));
    }

    /**
     * @param user
     * @param campaign
     * @return
     */
    private static ResponseStatusVM validateUserActions(User user, Campaign campaign) {
        ValidationResult result = CampaignValidationEngine.validateCampaign(campaign, user.getId());
        if (!result.isSuccess()) {
            logger.underlyingLogger().info(String.format("[u=%d][c=%d] Failed campaign validation. %s",
                    user.id, campaign.id, result.getSystemMessages().toString()));
        }
        return new ResponseStatusVM(SocialObjectType.CAMPAIGN.name(), campaign.id, user.id, result.isSuccess(), result.getMessages());
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