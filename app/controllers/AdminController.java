package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Campaign;
import models.Campaign.CampaignType;
import models.CampaignActionsUser;

import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.CampaignJoinerVM;

public class AdminController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(AdminController.class);

    @Transactional
    public static Result getCampaignJoiners(Long campaignId) {
        Campaign campaign = Campaign.findById(campaignId);
        List<CampaignJoinerVM> vms = new ArrayList<>();
        if (CampaignType.ACTIONS == campaign.campaignType) {
            List<CampaignActionsUser> joiners = CampaignActionsUser.getCampaignActionsUsers(campaignId);
            for (CampaignActionsUser joiner : joiners) {
                CampaignJoinerVM vm = new CampaignJoinerVM(joiner.userId, joiner.campaignId, joiner.getCreatedDate());
                vms.add(vm);
            }    
        } else if (CampaignType.QUESTIONS == campaign.campaignType) {
            // TODO
        } else if (CampaignType.VOTING == campaign.campaignType) {
            // TODO
        } else if (CampaignType.PHOTO_CONTEST == campaign.campaignType) {
            // TODO
        }
        
        return ok(Json.toJson(vms));
    }
}