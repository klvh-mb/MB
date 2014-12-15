package controllers;

import org.apache.commons.lang.StringUtils;

import models.TrackingCode;
import models.TrackingCode.TrackingTarget;
import play.db.jpa.Transactional;
import play.mvc.Controller;

public class TrackingController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(TrackingController.class);
    
    @Transactional(readOnly=true)
    public static void track(TrackingTarget trackingTarget, Boolean mobile) {
        String trackingSource = request().getQueryString("ts");
        if (!StringUtils.isEmpty(trackingSource)) {
            TrackingCode trackingCode = new TrackingCode(trackingSource, trackingTarget, mobile);
            trackingCode.save();
            logger.underlyingLogger().info(String.format("STS %s", trackingCode.toString()));
        }
    }
}