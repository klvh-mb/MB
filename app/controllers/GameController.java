package controllers;

import common.utils.NanoSecondStopWatch;
import models.GameAccount;
import models.GameAccountStatistics;
import models.GameAccountTransaction;
import models.User;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.GameAccountVM;

import java.util.List;

/**
 *
 */
public class GameController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(GameController.class);

    private static final int TRANSACTION_PAGESIZE = 10;

    @Transactional
    public static Result getGameAccount() {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User currUser = Application.getLocalUser(session());

        if (currUser.isLoggedIn()) {
            GameAccount gameAccount = GameAccount.findByUserId(currUser.id);
            GameAccountStatistics stat = GameAccountStatistics.getGameAccountStatistics(currUser.id);

            GameAccountVM vm = new GameAccountVM(gameAccount, stat);

            sw.stop();
            logger.underlyingLogger().info("[u="+currUser.id+"] getGameAccount. Took "+sw.getElapsedMS()+"ms");
            return ok(Json.toJson(vm));
        }
        else {
            logger.underlyingLogger().info("User is not logged in, no game account returning");
            return ok();
        }
    }

    @Transactional
    public static Result getGameTransactions(String offset) {
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        final User currUser = Application.getLocalUser(session());

        if (currUser.isLoggedIn()) {
            int offsetInt = Integer.parseInt(offset);

            List<GameAccountTransaction> trans =
                    GameAccountTransaction.getTransactions(currUser.id, offsetInt, TRANSACTION_PAGESIZE);
            // TODO

            sw.stop();
            logger.underlyingLogger().info("[u="+currUser.id+"] getGameTransactions(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
            return ok();
        }
        else {
            logger.underlyingLogger().info("User is not logged in, no game transactions returning");
            return ok();
        }
    }

    @Transactional
    public static boolean enableSignInForToday() {
        final User currUser = Application.getLocalUser(session());

        if (currUser.isLoggedIn()) {
            GameAccountStatistics stat = GameAccountStatistics.getGameAccountStatistics(currUser.getId());
            if (stat == null) {
                return true;    // no stat today, not signed in yet
            }
            return stat.num_sign_in < 1;
        }

        return false;
    }
    
    @Transactional
    public static Result signInForToday() {
        final User currUser = Application.getLocalUser(session());

        if (currUser.isLoggedIn()) {
            // check if user signed in for today
            GameAccountStatistics stat = GameAccountStatistics.recordSignin(currUser.getId());
            if (stat.num_sign_in == 1) {
                GameAccount.setPointsForSignin(currUser);
            }
            else {
                logger.underlyingLogger().info("[u="+currUser.id+"] Gamification - Already signed in today. Not Crediting.");
            }
        }

        return ok();
    }
}
