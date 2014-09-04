package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import common.cache.LocationCache;
import indexing.PostIndex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import models.Location;
import models.SecurityRole;
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
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
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

    public static final String APPLICATION_BASE_URL = 
            Play.application().configuration().getString("application.baseUrl");
    
    public static final int SIGNUP_DAILY_THRESHOLD = 
            Play.application().configuration().getInt("signup.daily.threshold", 1000);
    public static final int SIGNUP_DAILY_LIMIT = 
            Play.application().configuration().getInt("signup.daily.limit", 1000);
    
    public static final String SIGNUP_EMAIL = "signup_email";
    public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";

	//
	// Mobile
	//

	public static boolean isMobileUser() {
	    return "true".equalsIgnoreCase(session().get("mobile"));
	}
	
	public static void setMobileUser() {
	    setMobileUser("true");
	}
	
	public static void setMobileUser(String value) {
        session().put("mobile", value);
    }
	
	@Transactional
    public static Result mobile() {
        setMobileUser();    // manually set mobile to true
        
        final User localUser = getLocalUser(session());
        if(!User.isLoggedIn(localUser)) {
            return mobileLogin();
        }

        return home(localUser);
    }

	@Transactional
    public static Result mobileLogin() {
        final User localUser = getLocalUser(session());
        if(User.isLoggedIn(localUser)) {
            return redirect("/mobile");
        }
        return ok(views.html.mobile.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM, isOverDailySignupThreshold()));
    }
	
	//
	// End Mobile
	//
	
	public static User getSuperAdmin() {
	    return User.getSuperAdmin();
	}
	
	@Transactional
    public static Result getApplicationInfo() {
	    return ok(Json.toJson(new ApplicationInfoVM(APPLICATION_BASE_URL, isMobileUser()))); 
	}
	
	@Transactional
    public static Result getUserTargetProfile() {
	    final User localUser = getLocalUser(session());
	    if (!localUser.isLoggedIn()) {
	        return ok();
	    }
	    TargetProfile targetProfile = TargetProfile.fromUser(localUser);
        return ok(Json.toJson(new UserTargetProfileVM(targetProfile)));
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
	public static Result index() {
	    UserAgentUtil userAgentUtil = new UserAgentUtil(request());
	    boolean isMobile = userAgentUtil.isMobileUserAgent();
	    
	    setMobileUser(isMobile? "true":"false");
	    
        final User localUser = getLocalUser(session());
		if(!User.isLoggedIn(localUser)) {
			//return ok(views.html.magazine.home.render());
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
            logger.underlyingLogger().info("STS [u="+user.id+"][name="+user.name+"] Login - mobile");
        } else {
            logger.underlyingLogger().info("STS [u="+user.id+"][name="+user.name+"] Login - PC");
        }
	    
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
            logger.underlyingLogger().info("STS [u="+user.id+"][name="+user.name+"] Signup completed");

	        CommunityTargetingEngine.assignSystemCommunitiesToUser(user);
	        
	        // TODO - keith
	        // return welcome page
	        
	        user.setNewUser(false);
	    }

        // reset last login time
        user.setLastLogin(new Date());
	    return isMobileUser()? ok(views.html.mobile.home.render()) : ok(views.html.home.render());
	}
	
	@Transactional
    public static Result saveSignupInfoFb() {
	    return saveSignupInfo();
	}
	
	@Transactional
	public static Result saveSignupInfo() {
		final User localUser = getLocalUser(session());
		
		// UserInfo
        DynamicForm form = DynamicForm.form().bindFromRequest();
        String parentBirthYear = form.get("parent_birth_year");
        Location parentLocation = Location.getLocationById(Integer.valueOf(form.get("parent_location")));
        ParentType parentType = ParentType.valueOf(form.get("parent_type"));
        int numChildren = Integer.valueOf(form.get("num_children"));
        if (ParentType.NA.equals(parentType)) {
            numChildren = 0;
        }
        
        if (parentBirthYear == null)
            throw new RuntimeException("Parent UserInfo must be filled out");
        
        UserInfo userInfo = new UserInfo();
        userInfo.birthYear = parentBirthYear;
        userInfo.location = parentLocation;
        userInfo.parentType = parentType;
        
        if (parentBirthYear == null || parentLocation == null || parentType == null){
        	return ok(views.html.signup_info.render(localUser));
        }
        
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
            if (genderStr == null)
                throw new RuntimeException("Please select a gender for child " + i);
            
            TargetGender bbGender = TargetGender.valueOf(form.get("bb_gender" + i));
            String bbBirthYear = form.get("bb_birth_year" + i);
            String bbBirthMonth = form.get("bb_birth_month" + i);
            String bbBirthDay = form.get("bb_birth_day" + i);
            
            if (!DateTimeUtil.isDateOfBirthValid(bbBirthYear, bbBirthMonth, bbBirthDay)) 
                throw new RuntimeException("Please check child birthday for child " + i);
            
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
	
	public static User getLocalUser(final Session session) {
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
	public static Result signup() {
		final User localUser = getLocalUser(session());
		if(User.isLoggedIn(localUser)) {
			return redirect("/my");
		}
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
			return r;
		}
	}

	public static Result privacy() {
        return ok(views.html.privacy.render());
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
  
}
