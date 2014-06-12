package controllers;

import static play.data.Form.form;
import indexing.PostIndex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Community;
import models.Location;
import models.TargetingSocialObject;
import models.User;
import models.UserInfo;

import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import play.Logger;
import play.Logger.ALogger;
import play.Play;
import play.Routes;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import processor.FeedProcessor;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import viewmodel.PostIndexVM;
import views.html.signup;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import views.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.exceptions.AuthException;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.typesafe.plugin.RedisPlugin;

import common.model.TargetProfile;
import common.model.TargetYear;

public class Application extends Controller {
    
    public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "USER";
	public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
	private static play.api.Logger logger = play.api.Logger.apply("application");
	
	private static String prefix = Play.application().configuration().getString("keyprefix", "prod_");
	private static final String USER = prefix + "user_";
	private static final String MOMENT = prefix + "moment_";
	private static final String QNA = prefix + "qna_";
	
	@Transactional
	public static Result index() {
			
		logger.underlyingLogger().debug("Start index");
        
        final User localUser = getLocalUser(session());
		if(localUser == null) {
			return login();
		}
		
		List<Community> communities = localUser.getListOfJoinedCommunities();
		
		List<String> post_ids = FeedProcessor.buildPostQueueFromCommunities(communities, 20);
		
		logger.underlyingLogger().debug("getting in applyRelevances");
		FeedProcessor.applyRelevances(post_ids, localUser.id);
		logger.underlyingLogger().debug("Done with in applyRelevances");
		return home(localUser);
	}

	/**
	 * 1. if user login first time
	 *     i. bootstrap communities
	 *     ii. welcome page
	 */
	public static Result home(User user) {
		logger.underlyingLogger().debug("Home ");
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
	        
	        // District commuinities
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
	        return ok(views.html.home.render());
	    }
	    	if(UserInfo.findByUserId(user.id)) {
	    		return ok(views.html.home.render());
	    	}
	    	else {
	    		return ok(views.html.signup_info.render());
	    	}
	    
	}
	
	@Transactional
	public static Result saveSignupInfo() {
		final User localUser = getLocalUser(session());
		final Form<UserInfo> filledForm =form(UserInfo.class)
				.bindFromRequest();
		filledForm.get().save(localUser);
		return ok(views.html.home.render());
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
		return ok(
				Routes.javascriptRouter("jsRoutes",
						controllers.routes.javascript.Signup.forgotPassword()))
				.as("text/javascript");
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
		
		indexQuery.from((int) (offset*5));
        indexQuery.size(5);
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
