package controllers;

import Decoder.BASE64Decoder;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import common.cache.LocationCache;
import indexing.PostIndex;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import models.GameAccount;
import models.GameAccountReferral;
import models.Location;
import models.SecurityRole;
import models.TermsAndConditions;
import models.TrackingCode.TrackingTarget;
import models.User;
import models.UserChild;
import models.UserInfo;
import models.UserInfo.ParentType;

import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import play.Play;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import Decoder.BASE64Encoder;
import targeting.community.CommunityTargetingEngine;
import viewmodel.ApplicationInfoVM;
import viewmodel.PostIndexVM;
import viewmodel.TodayWeatherInfoVM;
import viewmodel.UserTargetProfileVM;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;

import common.model.TargetGender;
import common.model.TargetProfile;
import common.model.TodayWeatherInfo;
import common.utils.DateTimeUtil;
import common.utils.UserAgentUtil;
import domain.DefaultValues;

public class Application extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply(Application.class);

    public static final String APPLICATION_ENV = 
            Play.application().configuration().getString("application.env", "dev");
    
    public static final boolean LOGIN_BYPASS_ALL = 
            Play.application().configuration().getBoolean("login.bypass.all", false);
    
    public static final String APPLICATION_BASE_URL = 
            Play.application().configuration().getString("application.baseUrl");
    
    public static final int SIGNUP_DAILY_THRESHOLD = 
            Play.application().configuration().getInt("signup.daily.threshold", 1000);
    public static final int SIGNUP_DAILY_LIMIT = 
            Play.application().configuration().getInt("signup.daily.limit", 1000);
    
    public static final String SIGNUP_EMAIL = "signup_email";
    public static final String SESSION_PROMOCODE = "PROMO_CODE";
    public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	private static final String USER_KEY = "pa.u.id";
	private static final String PROVIDER_KEY = "pa.p.id";

	@Transactional
    public static Result index() {
        return redirect("/frontpage");
    }
	
	//
	// Entry points
	//
    
    @Transactional
    public static Result mainFrontpage() {
        UserAgentUtil userAgentUtil = new UserAgentUtil(request());
        boolean isMobile = userAgentUtil.isMobileUserAgent();
        
        Application.setMobileUser(isMobile? "true":"false");
        
        if (isMobile) {
            return ok(views.html.mb.mobile.frontpage.render());
        }
        return ok(views.html.mb.site.frontpage.render());
    }
	
    @Transactional
    public static Result mainArticles() {
        UserAgentUtil userAgentUtil = new UserAgentUtil(request());
        boolean isMobile = userAgentUtil.isMobileUserAgent();
        
        Application.setMobileUser(isMobile? "true":"false");
        
        if (isMobile) {
            return ok(views.html.mb.mobile.articles.render());
        }
        return ok(views.html.mb.site.articles.render());
    }
    
    @Transactional
    public static Result mainKnowledge() {
        UserAgentUtil userAgentUtil = new UserAgentUtil(request());
        boolean isMobile = userAgentUtil.isMobileUserAgent();
        
        Application.setMobileUser(isMobile? "true":"false");
        
        if (isMobile) {
            return ok(views.html.mb.mobile.knowledge.render());
        }
        return ok(views.html.mb.site.knowledge.render());
    }
    
    @Transactional
    public static Result mainMagazine() {
    	return mainFrontpage();
    	/*
        UserAgentUtil userAgentUtil = new UserAgentUtil(request());
        boolean isMobile = userAgentUtil.isMobileUserAgent();
        
        Application.setMobileUser(isMobile? "true":"false");
        
        if (isMobile) {
            return ok(views.html.mb.mobile.magazine.render());
        }
        return ok(views.html.mb.site.magazine.render());
        */
    }
    
    @Transactional
    public static Result mainHome() {
        return home();
    }
	
    @Transactional
    public static Result mobileFrontpage() {
        setMobileUser();    // manually set mobile to true
        return ok(views.html.mb.mobile.frontpage.render());
    }
    
    @Transactional
    public static Result mobileArticles() {
        setMobileUser();    // manually set mobile to true
        return ok(views.html.mb.mobile.articles.render());
    }
    
    @Transactional
    public static Result mobileKnowledge() {
        setMobileUser();    // manually set mobile to true
        return ok(views.html.mb.mobile.knowledge.render());
    }
    
    @Transactional
    public static Result mobileMagazine() {
    	return mobileFrontpage();
    	/*
        setMobileUser();    // manually set mobile to true
        return ok(views.html.mb.mobile.magazine.render());
        */
    }
    
    @Transactional
    public static Result mobileHome() {
        setMobileUser();    // manually set mobile to true
        
        final User localUser = getLocalUser(session());
        if(!User.isLoggedIn(localUser)) {
            return login();
        }

        return home(localUser);
    }
    
	//
	// Mobile
	//

	public static boolean isMobileUser() {
	    try {
	        return "true".equalsIgnoreCase(session().get("mobile"));
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static void setMobileUser() {
	    setMobileUser("true");
	}
	
	public static void setMobileUser(String value) {
        session().put("mobile", value);
    }
	
	@Transactional
    public static Result mobileLogin() {
        final User localUser = getLocalUser(session());
        if(User.isLoggedIn(localUser)) {
            return redirect("/m-my");
        }
        return ok(views.html.mobile.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, isOverDailySignupThreshold()));
    }
	
	//
	// End Mobile
	//
	
	public static User getMBAdmin() {
	    return User.getMBAdmin();
	}
	
	@Transactional
    public static Result getApplicationInfo() {
	    return ok(Json.toJson(new ApplicationInfoVM(APPLICATION_BASE_URL))); 
	}
	
	@Transactional
    public static Result getUserTargetProfile() {
	    final User localUser = getLocalUser(session());
	    if (localUser.isLoggedIn() && localUser.userInfo != null) {
	        TargetProfile targetProfile = TargetProfile.fromUser(localUser);
	        return ok(Json.toJson(new UserTargetProfileVM(targetProfile)));
	    }
	    return ok();	    
    }
    
	@Transactional
	public static Result getTodayWeatherInfo() {
	    TodayWeatherInfo info = TodayWeatherInfo.getInfo();
	    return ok(Json.toJson(new TodayWeatherInfoVM(info)));
	}
	
	public static boolean isOverDailySignupThreshold() {
        return User.getTodaySignupCount() >= SIGNUP_DAILY_THRESHOLD;
    }
    
    public static boolean isOverDailySignupLimit() {
        return User.getTodaySignupCount() >= SIGNUP_DAILY_LIMIT;
    }

    @Transactional
	public static Result homeWithPromoCode(String promoCode) {
		// put into http session
        session().put(SESSION_PROMOCODE, promoCode);

	    UserAgentUtil userAgentUtil = new UserAgentUtil(request());
	    boolean isMobile = userAgentUtil.isMobileUserAgent();

	    setMobileUser(isMobile? "true":"false");

        final User localUser = getLocalUser(session());
		if(!User.isLoggedIn(localUser)) {
		    return login();
		}

		return home(localUser);
	}

	@Transactional
	public static Result home() {
	    UserAgentUtil userAgentUtil = new UserAgentUtil(request());
	    boolean isMobile = userAgentUtil.isMobileUserAgent();
	    
	    setMobileUser(isMobile? "true":"false");
	    
        final User localUser = getLocalUser(session());
		if(!User.isLoggedIn(localUser)) {
		    return login();
		}

		return home(localUser);
	}

	/**
	 * 1. if user login first time
	 *     i. bootstrap communities
	 *     ii. welcome page
	 */
	public static Result home(User user) {
	    if (isMobileUser()) {
            logger.underlyingLogger().info("STS [u="+user.id+"][name="+user.displayName+"] Login - mobile");
        } else {
            logger.underlyingLogger().info("STS [u="+user.id+"][name="+user.displayName+"] Login - PC");
        }
	    
	    // reset last login time
	    user.setLastLogin(new Date());
	    
		if (User.isLoggedIn(user) && user.userInfo == null) {
		    if (user.fbLogin) {
		        return isMobileUser()? 
		                ok(views.html.mobile.signup_info_fb.render(user)):
		                    ok(views.html.signup_info_fb.render(user));
		    }
    	    return isMobileUser()? 
    	            ok(views.html.mobile.signup_info.render(user)):
    	                ok(views.html.signup_info.render(user));
		}
		
	    if (user.isNewUser()) {
            logger.underlyingLogger().info("STS [u="+user.id+"][name="+user.displayName+"] Signup completed - "+(isMobileUser()?"mobile":"PC"));

            String promoCode = session().get(SESSION_PROMOCODE);
            GameAccount.setPointsForSignUp(user, promoCode);

	        CommunityTargetingEngine.assignSystemCommunitiesToUser(user);
	        
	        UserController.sendGreetingMessageToNewUser();
	        
	        user.setNewUser(false);
	        //return redirect("/my#!/communities-discover");
	    }
	    return isMobileUser()? ok(views.html.mb.mobile.home.render()) : ok(views.html.mb.site.home.render());
	}
	
	@Transactional
    public static Result saveSignupInfoFb() {
	    return doSaveSignupInfo(true);
	}
	
	@Transactional
	public static Result saveSignupInfo() {
	    return doSaveSignupInfo(false);
	}
	
	@Transactional
    public static Result doSaveSignupInfo(boolean fb) {
		final User localUser = getLocalUser(session());
		
		// UserInfo
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String parentDisplayName = form.get("parent_displayname").trim();
        String parentBirthYear = form.get("parent_birth_year");
        Location parentLocation = Location.getLocationById(Integer.valueOf(form.get("parent_location")));
        ParentType parentType = ParentType.valueOf(form.get("parent_type"));
        int numChildren = Integer.valueOf(form.get("num_children"));
        if (ParentType.NA.equals(parentType)) {
            numChildren = 0;
        }
        
        if (!User.isDisplayNameValid(parentDisplayName)) {
            return handleSaveSignupInfoError("\""+parentDisplayName+"\" 不可有空格", fb);
        }
        if (User.isDisplayNameExists(parentDisplayName)) {
            return handleSaveSignupInfoError("\""+parentDisplayName+"\" 已被選用。請選擇另一個顯示名稱重試", fb);
        }
        if (parentBirthYear == null || parentLocation == null || parentType == null) {
            return handleSaveSignupInfoError("請填寫您的生日，地區，媽媽身份", fb);
        }
        
        localUser.displayName = parentDisplayName;
        localUser.name = parentDisplayName;
        
        UserInfo userInfo = new UserInfo();
        userInfo.birthYear = parentBirthYear;
        userInfo.location = parentLocation;
        userInfo.parentType = parentType;
        
        if (ParentType.MOM.equals(parentType) || ParentType.SOON_MOM.equals(parentType)) {
            userInfo.gender = TargetGender.Female;
        } else if (ParentType.DAD.equals(parentType) || ParentType.SOON_DAD.equals(parentType)) {
            userInfo.gender = TargetGender.Male;
        } else {
            userInfo.gender = TargetGender.Female;   // default
        }
        userInfo.numChildren = numChildren;
        
        localUser.userInfo = userInfo;
        localUser.userInfo.save();
        
        // UseChild
        int maxChildren = (numChildren > 5)? 5 : numChildren;
        for (int i = 1; i <= maxChildren; i++) {
            String genderStr = form.get("bb_gender" + i);
            if (genderStr == null) {
                return handleSaveSignupInfoError("請選擇寶寶性別", fb);
            }
            
            TargetGender bbGender = TargetGender.valueOf(form.get("bb_gender" + i));
            String bbBirthYear = form.get("bb_birth_year" + i);
            String bbBirthMonth = form.get("bb_birth_month" + i);
            String bbBirthDay = form.get("bb_birth_day" + i);
            
            if (bbBirthDay == null) {
                bbBirthDay = "";
            }
            
            if (!DateTimeUtil.isDateOfBirthValid(bbBirthYear, bbBirthMonth, bbBirthDay)) {
                return handleSaveSignupInfoError("寶寶生日日期格式不正確。請重試", fb);
            }
            
            UserChild userChild = new UserChild();
            userChild.gender = bbGender;
            userChild.birthYear = bbBirthYear;
            userChild.birthMonth = bbBirthMonth;
            userChild.birthDay = bbBirthDay;
            
            userChild.save();
            localUser.children.add(userChild);
        }
        
        return redirect("/my");
	}
	
	private static Result handleSaveSignupInfoError(String error, boolean fb) {
        final User localUser = getLocalUser(session());
        if (isMobileUser()) {
            flash("error", error);
            return fb? badRequest(views.html.mobile.signup_info_fb.render(localUser)):
                badRequest(views.html.mobile.signup_info.render(localUser));
        } else {
            flash("error", error);
            return fb? badRequest(views.html.signup_info_fb.render(localUser)):
                badRequest(views.html.signup_info.render(localUser));
        }
    }
	   
	public static User getLocalUser(final Session session) {
		//if request from mobile 
		if(UserController.getQueryString(request(), "key") != null ){
			User localUser = null;
	    	try {
	    		Key dkey = UserController.generateKey();
	            Cipher c = Cipher.getInstance("AES");
	            c.init(Cipher.DECRYPT_MODE, dkey);
	            byte[] decordedValue = new BASE64Decoder().decodeBuffer(UserController.getQueryString(request(), "key"));
	            byte[] decValue = c.doFinal(decordedValue);
	            String decryptedValue = new String(decValue);
	    		System.out.println(UserController.getQueryString(request(), "key")+"hhhhhhhhhhhhh "+decryptedValue);
	    		localUser = Application.getMobileLocalUser(decryptedValue);
	    		return localUser;
			}catch(Exception e) { 
				return null;
			}
		}
		
		//if request from web
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		if (currentAuthUser == null) {
		    return User.noLoginUser();
		}
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		if (localUser == null) {
            return User.noLoginUser();
        }
		return localUser;
	}
	
	public static User getLocalUser(final String session) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		if (currentAuthUser == null) {
		    return User.noLoginUser();
		}
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		if (localUser == null) {
            return User.noLoginUser();
        }
		return localUser;
	}
	
	public static User getMobileLocalUser(final String decryptedValue) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(decryptedValue);
		

		if (currentAuthUser == null) {
		    return User.noLoginUser();
		}
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		if (localUser == null) {
            return User.noLoginUser();
        }
		return localUser;
	}
	
	public static Long getLocalUserId() {
        User user = null;
        try {
            user = getLocalUser(session());
        } catch (Exception e) {
            // ignore
        }

        if (user != null) {
            return user.id;
        }
        return -1L;
    }

    public static String getLocalUserName() {
        User user = null;
        try {
            user = getLocalUser(session());
        } catch (Exception e) {
            // ignore
        }

        if (user != null) {
            return user.name;
        }
        return "";
    }
	        
	@Restrict(@Group(SecurityRole.USER))
    public static Result restricted() {
        final User localUser = getLocalUser(session());
        return ok(views.html.restricted.render(localUser));
    }

    @Restrict(@Group(SecurityRole.USER))
    public static Result profile() {
        final User localUser = getLocalUser(session());
        return ok(views.html.profile.render(localUser));
    }
    
	@Transactional
	public static Result login() {
	    UserAgentUtil userAgentUtil = new UserAgentUtil(request());
        boolean isMobile = userAgentUtil.isMobileUserAgent();
        
        setMobileUser(isMobile? "true":"false");
        
		final User localUser = getLocalUser(session());
		if(User.isLoggedIn(localUser)) {
			return redirect("/my");
		}
		return isMobileUser()?
		        ok(views.html.mobile.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, isOverDailySignupThreshold())):
		            ok(views.html.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, isOverDailySignupThreshold()));
	}

	@Transactional
	public static Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			flash("error", "登入電郵或密碼錯誤");
			return isMobileUser()? 
			        badRequest(views.html.mobile.login.render(filledForm, isOverDailySignupThreshold())):
			            badRequest(views.html.login.render(filledForm, isOverDailySignupThreshold()));
		} else {
			// Everything was filled
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

    @Transactional
    public static Result doLoginPopup() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String redirectURL = form.get("rurl");
        session().put("pa.url.orig", redirectURL);
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            flash("error", "登入電郵或密碼錯誤");
            return isMobileUser()? 
                    badRequest(views.html.mobile.login.render(filledForm, isOverDailySignupThreshold())) : 
                        badRequest(views.html.login.render(filledForm, isOverDailySignupThreshold()));
        } else {
            // Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }
	
	@Transactional
	public static Result signup() {
		final User localUser = getLocalUser(session());
		if(User.isLoggedIn(localUser)) {
			return redirect("/my");
		}
		TrackingController.track(TrackingTarget.SIGNUP_PAGE, isMobileUser());
		return isMobileUser()? 
		        ok(views.html.mobile.signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM)):
		            ok(views.html.signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
	}

    public static Result jsRoutes() {
        return ok(Routes.javascriptRouter("jsRoutes", 
    	        controllers.routes.javascript.Signup.forgotPassword())).as("text/javascript");
    }

	@Transactional
	public static Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();
		
		if (!filledForm.hasErrors() && filledForm.get() != null) {
    		String email = filledForm.get().email;
    		if (email != null) {
    		    final User existingUser = User.findByEmail(email);
                if (existingUser != null && existingUser.emailValidated) {
        		    List<ValidationError> errors = new ArrayList<>();
        	        errors.add(new ValidationError(Signup.EMAIL_EXISTS_ERROR_KEY, Signup.EMAIL_EXISTS_ERROR_MESSAGE));
        	        filledForm.errors().put(Signup.EMAIL_EXISTS_ERROR_KEY, errors);
                }
    		}
		}
		
		if (filledForm.hasErrors()) {
		    String errorRequired = Messages.get("error.required") + " - ";
		    String errorRequiredFields = "";
		    String errorOther = "";
		    for (Entry<String, List<ValidationError>> errorEntry : filledForm.errors().entrySet()) {
		        List<ValidationError> errors = errorEntry.getValue();
		        for (ValidationError error : errors) {
		            if ("error.required".equalsIgnoreCase(error.message())) {
		                if ("lname".equalsIgnoreCase(error.key())) {
		                    errorRequiredFields += "'姓' ";
		                } else if ("fname".equalsIgnoreCase(error.key())) {
		                    errorRequiredFields += "'名' ";
                        } else if ("email".equalsIgnoreCase(error.key())) {
                            errorRequiredFields += "'電郵' ";
                        } else if ("password".equalsIgnoreCase(error.key())) {
                            errorRequiredFields += "'密碼' ";
                        } else if ("repeatPassword".equalsIgnoreCase(error.key())) {
                            errorRequiredFields += "'重複密碼' ";
                        } else {
                            errorRequiredFields += error.key() + " ";
                        }
		            } if ("error.minLength".equalsIgnoreCase(error.message()) ||
		                    "error.maxLength".equalsIgnoreCase(error.message())) {
		                if (!errorOther.isEmpty()) {
                            break;
                        }
		                if ("password".equalsIgnoreCase(error.key()) ||
		                        "repeatPassword".equalsIgnoreCase(error.key())) {
		                    errorOther += "密碼" + String.format(Messages.get(error.message()), error.arguments().get(0));
                        } else {
                            errorOther += error.key() + String.format(Messages.get(error.message()), error.arguments().get(0));
                        }
		            } else {
		                if (!errorOther.isEmpty()) {
		                    break;
		                }
		                errorOther += Messages.get(error.message());      // + " - " + error.key();
		            }
		        }
		    }
		    
		    if (!errorRequiredFields.isEmpty()) {
		        flash().put(controllers.Application.FLASH_ERROR_KEY, errorRequired + errorRequiredFields);
		    } else if (!errorOther.isEmpty()) {
		        flash().put(controllers.Application.FLASH_ERROR_KEY, errorOther);
		    } else {
                flash().put(controllers.Application.FLASH_ERROR_KEY, Messages.get("error.invalid"));
		    }
			return isMobileUser()? 
			        badRequest(views.html.mobile.signup.render(filledForm)):
			            badRequest(views.html.signup.render(filledForm));
		} else {
			// Everything was filled
		    String email = filledForm.get().email;
		    session().put(SIGNUP_EMAIL, email);

            // check if this native signup was from a referral promo code
            String promoCode = session().get(SESSION_PROMOCODE);
            if (promoCode != null) {
                GameAccountReferral.addReferralRecord(email, promoCode);
            }

            logger.underlyingLogger().info("STS [email="+email+"] Native signup submitted");
			return UsernamePasswordAuthProvider.handleSignup(ctx());
		}
	}
	
	@Transactional
	public static Result doSignupForTest() throws AuthException {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(views.html.signup.render(filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup
			Result r  = PlayAuthenticate.handleAnthenticationByProvider(ctx(),
					 com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.Case.SIGNUP,
					 new MyUsernamePasswordAuthProvider(Play.application()));
			return r;
			
		}
	}
	 private static Key generateKey() throws Exception {
	        Key key = new SecretKeySpec("TheBestSecretkey".getBytes(), "AES");
	        return key;
		}
	
	@Transactional
	public static Result doLoginForTest() throws AuthException {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(views.html.login.render(filledForm, isOverDailySignupThreshold()));
		} else {
			// Everything was filled
			Result r  = PlayAuthenticate.handleAnthenticationByProvider(ctx(),
					 com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.Case.LOGIN,
					 new MyUsernamePasswordAuthProvider(Play.application()));
			String encryptedValue = null;
			String plainData=session().get(PROVIDER_KEY)+"-"+session().get(USER_KEY);
			try { 
	    		
	    		Key key = generateKey();
	            Cipher c = Cipher.getInstance("AES");
	            c.init(Cipher.ENCRYPT_MODE, key);
	            byte[] encVal = c.doFinal(plainData.getBytes());
	            encryptedValue = new BASE64Encoder().encode(encVal);
	    		
	    	}
	    	catch(Exception e) { }
			return ok(encryptedValue.replace("+", "%2b"));
		}
	}

	@Transactional
	public static Result privacy() {
		TermsAndConditions terms = TermsAndConditions.getTermsAndConditions();
        return ok(views.html.privacy.render(terms.privacy));
    }
	
	@Transactional
	public static Result terms() {
		TermsAndConditions tAndC = TermsAndConditions.getTermsAndConditions();
        return ok(views.html.terms_and_conditions.render(tAndC.terms));
    }
	
	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}
    
	@Transactional
    public static Result getAllDistricts() {
        return ok(Json.toJson(LocationCache.getHongKongDistrictsVM()));
    }
   
	@Transactional
    public static Result searchForPosts(String query, Long community_id, Long offset){
		
		AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
		andFilterBuilder.add(FilterBuilders.queryFilter(QueryBuilders.fieldQuery("community_id", community_id)));
		
		OrFilterBuilder orFilterBuilder = FilterBuilders.orFilter();
		orFilterBuilder.add(FilterBuilders.queryFilter(QueryBuilders.fieldQuery("description", query)));
		orFilterBuilder.add(FilterBuilders.queryFilter(QueryBuilders.fieldQuery("comments.commentText", query)));
		
		andFilterBuilder.add(orFilterBuilder);
		
		IndexQuery<PostIndex> indexQuery = PostIndex.find.query();
		indexQuery.setBuilder(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), 
				 andFilterBuilder));
		
		indexQuery.from((int) (offset * DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT));
        indexQuery.size(DefaultValues.DEFAULT_INFINITE_SCROLL_COUNT);
		IndexResults<PostIndex> allPosts = PostIndex.find.search(indexQuery);

		List<PostIndexVM> vm = new ArrayList<>();
		for(PostIndex post : allPosts.results) {
			PostIndexVM p = new PostIndexVM(post);
			vm.add(p);
		}
    	return ok(Json.toJson(vm));
    }

	//
	// Webmaster
	//
	
	@Transactional
    public static Result bingWebmaster() {
        return ok(views.xml.bing_webmaster.render());
    }
	
	@Transactional
	public static Result googleWebmaster() {
	    return ok(views.html.google_webmaster.render());
	}
	
	@Transactional
	public static Result sitemap() {
	    return ok(views.xml.sitemap.render());
	}
	
	//
	// Test Ads
	//
	@Transactional
    public static Result adsMock() {
        return ok(views.html.ads_mock.render());
    }
	
	@Transactional
    public static Result adsPlain() {
        return ok(views.html.ads_plain.render());
    }
    
	@Transactional
    public static Result ngAds() {
        return ok(views.html.ng_ads.render());
    }
}
