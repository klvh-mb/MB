package controllers;

import indexing.PostIndex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import models.Community;
import models.Location;
import models.TargetingSocialObject;
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
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import processor.FeedProcessor;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import redis.clients.jedis.Tuple;
import viewmodel.LocationVM;
import viewmodel.PostIndexVM;
import views.html.signup;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.utils.UtilRails;

import common.model.TargetGender;
import common.model.TargetProfile;
import common.model.TargetYear;
import common.utils.DateTimeUtil;

public class Application extends Controller {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

    public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "USER";
	public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

	@Transactional
	public static Result index() {
        final User localUser = getLocalUser(session());
		if(localUser == null) {
			return login();
		}

		List<Long> communities = localUser.getListOfJoinedCommunityIds();

        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[u="+localUser.getId()+"] index. numJoinedComm="+communities.size());
        }

        // TODO: Need to refactor to decide how many to pull from each community
		Set<Tuple> post_ids = FeedProcessor.buildPostQueueFromCommunities(communities, 20);
		
		FeedProcessor.applyRelevances(post_ids, localUser.id);
		return home(localUser);
	}

	/**
	 * 1. if user login first time
	 *     i. bootstrap communities
	 *     ii. welcome page
	 */
	public static Result home(User user) {
        if (logger.underlyingLogger().isDebugEnabled()) {
		    logger.underlyingLogger().debug("[u="+user.getId()+"] Application - home()");
        }
		
		if (user.userInfo == null) {
		    if (user.fbLogin) {
		        return ok(views.html.signup_info_fb.render(user));
		    }
	        return ok(views.html.signup_info.render(user));
		}
		
	    if (user.isNewUser()) {
	        TargetProfile targetProfile = TargetProfile.fromUser(user);
	        
	        // Zodiac community
	        for (TargetYear targetYear : targetProfile.getChildYears()) {
	            Community community = Community.findByTargetingTypeTargetingInfo(
	                    TargetingSocialObject.TargetingType.ZODIAC_YEAR, targetYear.toString());
	            if (community != null) {
    	            try {
    	                community.onJoinRequest(user);
    	            } catch (SocialObjectNotJoinableException e) {
    	                e.printStackTrace();
    	            }
	            }
	        }
	        
	        // District communities
	        if (targetProfile.getLocation() != null) {
    	        Location district = Location.getParentLocation(targetProfile.getLocation(), Location.LocationType.DISTRICT);
    	        Community community = Community.findByTargetingTypeTargetingInfo(
                        TargetingSocialObject.TargetingType.LOCATION_DISTRICT, district.id.toString());
                if (community != null) {
                    try {
                        community.onJoinRequest(user);
                    } catch (SocialObjectNotJoinableException e) {
                        e.printStackTrace();
                    }
                }
	        }
	        
	        // TODO - keith
	        // return welcome page
	        
	        user.setNewUser(false);
	    }
	    
	    return ok(views.html.home.render());
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
        
		return redirect("/");
	}
	
	public static User getLocalUser(final Session session) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		return localUser;
	}

	@Restrict(@Group(Application.USER_ROLE))
	public static Result restricted() {
		final User localUser = getLocalUser(session());
		return ok(views.html.restricted.render(localUser));
	}

	@Restrict(@Group(Application.USER_ROLE))
	public static Result profile() {
		final User localUser = getLocalUser(session());
		return ok(views.html.profile.render(localUser));
	}

	@Transactional
	public static Result login() {
		final User localUser = getLocalUser(session());
		if(localUser != null) {
			return redirect("/");
		}
		return ok(views.html.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	@Transactional
	public static Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(views.html.login.render(filledForm));
		} else {
			// Everything was filled
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

	@Transactional
	public static Result signup() {
		final User localUser = getLocalUser(session());
		if(localUser != null) {
			return redirect("/");
		}
		return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
	}

    public static Result jsRoutes() {
        return ok(Routes.javascriptRouter("jsRoutes", 
    	        controllers.routes.javascript.Signup.forgotPassword())).as("text/javascript");
    }

	@Transactional
	public static Result doSignup() {
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
			return badRequest(views.html.login.render(filledForm));
		} else {
			// Everything was filled
			Result r  = PlayAuthenticate.handleAnthenticationByProvider(ctx(),
					 com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider.Case.LOGIN,
					 new MyUsernamePasswordAuthProvider(Play.application()));
			return r;
		}
	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}
    
   @Transactional
    public static Result getAllDistricts() {
        List<Location> locations = Location.getHongKongDistricts();
        List<LocationVM> locationVMs = new ArrayList<>();
        for(Location location : locations) {
            LocationVM vm = LocationVM.locationVM(location);
            locationVMs.add(vm);
        }
        return ok(Json.toJson(locationVMs));
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
		
		indexQuery.from((int) (offset * UtilRails.noOfPost));
        indexQuery.size(UtilRails.noOfPost);
		IndexResults<PostIndex> allPosts = PostIndex.find.search(indexQuery);
		
		System.out.println(allPosts.getTotalCount());
		
		List<PostIndexVM> vm = new ArrayList<>();
		for(PostIndex post : allPosts.results) {
			PostIndexVM p = new PostIndexVM(post);
			vm.add(p);
		}
    	return ok(Json.toJson(vm));
    }
  
}
