package controllers;

import common.utils.NanoSecondStopWatch;
import models.Community;
import models.CommunityStatistics;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.HotCommunityParentVM;
import viewmodel.HotCommunityVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/12/14
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrontPageController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(FrontPageController.class);

    private static final int HOT_LAST_DAYS = 30;
    private static final int MAX_HOT_COMMS = 8;

    @Transactional
    public static Result getHotCommunities() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        List<CommunityStatistics.StatisticsSummary> sortedStats =
                CommunityStatistics.getMostActiveCommunities(HOT_LAST_DAYS);

        Map<Long, CommunityStatistics.StatisticsSummary> statsMap = new HashMap<>();
        List<Long> ids = new ArrayList<>(sortedStats.size());

        for (CommunityStatistics.StatisticsSummary sortedStat : sortedStats) {
            ids.add(sortedStat.commId);
            statsMap.put(sortedStat.commId, sortedStat);
        }

        List<Community> comms = Community.findOpenCommsByIds(ids, MAX_HOT_COMMS);

        HotCommunityParentVM vms = new HotCommunityParentVM();
        for (Community comm : comms) {
            CommunityStatistics.StatisticsSummary stat = statsMap.get(comm.id);
            HotCommunityVM vm = HotCommunityVM.hotCommunityVM(comm, stat.numPosts, stat.numComments);
            vms.addHotCommunityVM(vm);
        }

        logger.underlyingLogger().info("FrontPage getHotCommunities. Took "+sw.getElapsedMS()+"ms. Count="+comms.size());
        return ok(Json.toJson(vms));
    }

}
