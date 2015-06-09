package controllers;

import static play.data.Form.form;
import common.utils.ImageUploadUtil;
import common.utils.NanoSecondStopWatch;
import models.GameAccount;
import models.GameAccountStatistics;
import models.GameAccountTransaction;
import models.GameGift;
import models.RedeemTransaction;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.GameAccountVM;
import viewmodel.GameGiftVM;
import viewmodel.GameTransactionVM;
import viewmodel.ResponseStatusVM;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mnt.exception.SocialObjectNotLikableException;

import domain.SocialObjectType;
import email.EDMUtility;

/**
 *
 */
public class GameController extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(GameController.class);

    private static final ImageUploadUtil imageUploadUtil = new ImageUploadUtil("game");
    
    private static final int TRANSACTION_PAGESIZE = 30;

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

            List<GameAccountTransaction> transactions =
                    GameAccountTransaction.getTransactions(currUser.id, offsetInt, TRANSACTION_PAGESIZE);
            List<GameTransactionVM> vms = new ArrayList<GameTransactionVM>();
            for (GameAccountTransaction transaction : transactions) {
                vms.add(new GameTransactionVM(transaction));
            }
            
            sw.stop();
            logger.underlyingLogger().info("[u="+currUser.id+"] getGameTransactions(offset="+offset+"). Took "+sw.getElapsedMS()+"ms");
            return ok(Json.toJson(vms));
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

        if (enableSignInForToday()) {
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

    @Transactional
    public static Result getAllGameGifts() {
        List<GameGift> gameGifts = GameGift.getAllGameGifts();
        List<GameGiftVM> vms = new ArrayList<>();
        for (GameGift gameGift : gameGifts) {
        	GameGiftVM vm = new GameGiftVM(gameGift, true);
            vms.add(vm);
        }
        return ok(Json.toJson(vms));
    }
    
    @Transactional
    public static Result infoGameGift(Long gameGiftId) {
        final User localUser = Application.getLocalUser(session());
        
        GameGift gameGift = GameGift.findById(gameGiftId);
        if (gameGift == null) {
            return notFound();
        }
        gameGift.noOfViews++;
        GameGiftVM vm = new GameGiftVM(gameGift, localUser);
        return ok(Json.toJson(vm));
    }
    
    @Transactional
	public static Result redeemGameGift() {
		final User localUser = Application.getLocalUser(session());
		if (!localUser.isLoggedIn()) {
            return status(599);
        }
		
		DynamicForm form = form().bindFromRequest();
        Long id = Long.parseLong(form.get("id"));
        
		GameGift gameGift = GameGift.findById(id);
        if (gameGift == null) {
            logger.underlyingLogger().error(String.format("[u=%d][g=%d] User tried to redeem game gift which does not exist", localUser.id, id));
            return status(500);
        }
        
        ResponseStatusVM status = validateGameGiftRedeemTransaction(localUser, gameGift);
        if (status.success) {
        	// deduct points
        	GameAccount.redeemGameGift(localUser, gameGift);
        	
        	// record new redeem transaction
	        RedeemTransaction redeemTransaction = new RedeemTransaction();
	        redeemTransaction.user = localUser;
	        redeemTransaction.redeemType = RedeemTransaction.RedeemType.GAME_GIFT;
	        redeemTransaction.objId = gameGift.id;
	        redeemTransaction.transactionState = RedeemTransaction.TransactionState.REQUESTED;
	        redeemTransaction.setCreatedDate(new Date());
	        redeemTransaction.save();        

	        // email admin
	        String notifText = "New Redeem Request (u="+localUser.id+") to game gift (g="+gameGift.id+")";
	        EDMUtility.getInstance().sendMailToMB(notifText, notifText);
	        
	        logger.underlyingLogger().info(String.format("[u=%d][g=%d] Successfully requested redeem game gift", localUser.id, gameGift.id));
        }
        
        return ok(Json.toJson(status));
	}
	
	private static ResponseStatusVM validateGameGiftRedeemTransaction(User user, GameGift gameGift) {
		// Duplicate redeem!!
		RedeemTransaction redeemTransaction = 
        		RedeemTransaction.getPendingRedeemTransaction(user, gameGift.id, RedeemTransaction.RedeemType.GAME_GIFT);
        if (redeemTransaction != null) {
        	logger.underlyingLogger().error(String.format("[u=%d][g=%d] Duplicate redeem game gift!", user.id, gameGift.id));
        	String message = "您已要求換領這禮品，如未收到換領通知，請 1) PM miniBean Facebook 專頁 或 2) 電郵至 info@minibean.com.hk";
        	return new ResponseStatusVM(SocialObjectType.GAME_GIFT.name(), gameGift.id, user.id, false, message);
        }
        
        // TODO: validate points
        
        
        return new ResponseStatusVM(SocialObjectType.GAME_GIFT.name(), gameGift.id, user.id, true);
    }
	
    @Transactional
    public static Result onLike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        GameGift gameGift = GameGift.findById(id);
        gameGift.onLikedBy(localUser);
        return ok();
    }
    
    @Transactional
    public static Result onUnlike(Long id) throws SocialObjectNotLikableException {
        User localUser = Application.getLocalUser(session());
        if (!localUser.isLoggedIn()) {
            logger.underlyingLogger().error(String.format("[u=%d] User not logged in", localUser.id));
            return status(500);
        }
        
        GameGift gameGift = GameGift.findById(id);
        gameGift.onUnlikedBy(localUser);
        localUser.doUnLike(id, gameGift.objectType);
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
