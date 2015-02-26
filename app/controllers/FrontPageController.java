package controllers;

import common.utils.ImageUploadUtil;
import common.utils.NanoSecondStopWatch;
import models.Community;
import models.CommunityStatistics;
import models.FrontPageTopic;
import models.FrontPageTopic.TopicType;
import models.TargetingSocialObject;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.FrontPageTopicVM;
import viewmodel.HotCommunityParentVM;
import viewmodel.HotCommunityVM;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.DefaultValues;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/12/14
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrontPageController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(FrontPageController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("frontpage");
    
    @Transactional
    public static Result getHotCommunities() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<CommunityStatistics.StatisticsSummary> sortedStats =
                CommunityStatistics.getMostActiveCommunities(
                        DefaultValues.FRONTPAGE_HOT_COMMUNITIES_FOR_LAST_DAYS,
                        TargetingSocialObject.TargetingType.PRE_NURSERY);

        Map<Long, CommunityStatistics.StatisticsSummary> statsMap = new HashMap<>();
        List<Long> ids = new ArrayList<>(sortedStats.size());

        for (CommunityStatistics.StatisticsSummary sortedStat : sortedStats) {
            ids.add(sortedStat.commId);
            statsMap.put(sortedStat.commId, sortedStat);
        }

        List<Community> comms = Community.findOpenCommsByIds(ids, DefaultValues.FRONTPAGE_HOT_COMMUNITIES_COUNT);

        HotCommunityParentVM vms = new HotCommunityParentVM();
        for (Community comm : comms) {
            CommunityStatistics.StatisticsSummary stat = statsMap.get(comm.id);
            HotCommunityVM vm = HotCommunityVM.hotCommunityVM(comm, stat.numPosts, stat.numComments);
            vms.addHotCommunityVM(vm);
        }

        logger.underlyingLogger().info("FrontPage getHotCommunities. Took "+sw.getElapsedMS()+"ms. Count="+comms.size());
        return ok(Json.toJson(vms));
    }

    @Transactional
    public static Result getFrontPageTopics() {
        List<FrontPageTopic> topics = FrontPageTopic.getActiveFrontPageTopics();
        if (topics == null) {
            return ok();
        }
        List<FrontPageTopicVM> vms = new ArrayList<FrontPageTopicVM>();
        for (FrontPageTopic topic : topics) {
            vms.add(new FrontPageTopicVM(topic, Application.getLocalUser(session())));
        }
        return ok(Json.toJson(vms));
    }
    
    private static Result getTopics(TopicType topicType) {
        List<FrontPageTopic> topics = FrontPageTopic.getActiveFrontPageTopics(topicType);
        if (topics == null) {
            return ok();
        }
        List<FrontPageTopicVM> vms = new ArrayList<FrontPageTopicVM>();
        for (FrontPageTopic topic : topics) {
            vms.add(new FrontPageTopicVM(topic, Application.getLocalUser(session())));
        }
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result getSliderTopics() {
        return getTopics(TopicType.SLIDER);
    }
    
    @Transactional
    public static Result getPromoTopics() {
        return getTopics(TopicType.PROMO);
    }
    
    @Transactional
    public static Result getPromo2Topics() {
        return getTopics(TopicType.PROMO_2);
    }
    
    @Transactional
    public static Result getFeaturedTopics() {
        return getTopics(TopicType.FEATURED);
    }
    
    @Transactional
    public static Result getGameTopics() {
        return getTopics(TopicType.GAME);
    }
    
    @Transactional
    public static Result getImage(Long year, Long month, Long date, String name) {
        response().setHeader("Cache-Control", "max-age=604800");
        String path = imageUploadUtil.getImagePath(year, month, date, name);

        logger.underlyingLogger().debug("getImage. path="+path);
        return ok(new File(path));
    }
}