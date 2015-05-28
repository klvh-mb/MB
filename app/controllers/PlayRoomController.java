package controllers;

import models.PlayRoom;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.PlayRoomVM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 25/5/15
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayRoomController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(PlayRoomController.class);

    @Transactional
	public static Result getPlayRoomInfo(Long id) {
		final User localUser = Application.getLocalUser(session());
        PlayRoom p = PlayRoom.findById(id);
        if (p == null) {
            return notFound();
        }
        p.noOfViews++;

        logger.underlyingLogger().info("[u="+localUser.id+"][id="+id+"] getPlayRoom.");
		return ok(Json.toJson(new PlayRoomVM(p, localUser)));
    }

    @Transactional
	public static Result searchByName(String nameSubStr) {
        final User localUser = Application.getLocalUser(session());
        List<PlayRoom> ps = PlayRoom.searchByName(nameSubStr);

        final List<PlayRoomVM> vms = new ArrayList<>();
        for (PlayRoom p : ps) {
            vms.add(new PlayRoomVM(p, localUser));
        }
		return ok(Json.toJson(vms));
    }

    @Transactional
	public static Result getPlayRoomsByDistrict(Long districtId) {
		final User localUser = Application.getLocalUser(session());
        List<PlayRoom> pns = PlayRoom.getPNsByDistrict(districtId);

        final List<PlayRoomVM> pnVMs = new ArrayList<>();
        for (PlayRoom pn : pns) {
            pnVMs.add(new PlayRoomVM(pn, localUser));
        }

        logger.underlyingLogger().info("[u="+localUser.id+"][d="+districtId+"] getPlayRoomsByDistrict.");
		return ok(Json.toJson(pnVMs));
    }

}
