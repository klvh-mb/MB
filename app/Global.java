import java.util.Arrays;

import common.cache.FriendCache;
import models.SecurityRole;
import models.SystemVersion;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Call;
import play.mvc.Http.RequestHeader;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Results;
import processor.FeedProcessor;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;
import targeting.community.NewsfeedCommTargetingEngine;

public class Global extends GlobalSettings {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

    // Configurations for bootstrap
    private static final String STARTUP_BOOTSTRAP_PROP = "startup.data.bootstrap";

	@Transactional
	public void onStart(Application app) {
	    PlayAuthenticate.setResolver(new Resolver() {

            @Override
            public Call login(final Session session) {
                // Your login page
                if (controllers.Application.isMobileUser()) {
                    return routes.Application.mobileLogin();
                }
                return routes.Application.login();
            }

            @Override
            public Call afterAuth(final Session session) {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
                if (controllers.Application.isMobileUser()) {
                    return routes.Application.mobile();
                }
                return routes.Application.home();
            }

            @Override
            public Call afterLogout(final Session session) {
                if (controllers.Application.isMobileUser()) {
                    return routes.Application.mobileLogin();
                }
                return routes.Application.login();
            }

            @Override
            public Call auth(final String provider) {
                // You can provide your own authentication implementation,
                // however the default should be sufficient for most cases
                return com.feth.play.module.pa.controllers.routes.Authenticate
                        .authenticate(provider);
            }

            @Override
            public Call askMerge() {
                return routes.Account.askMerge();
            }

            @Override
            public Call askLink() {
                return routes.Account.askLink();
            }

            @Override
            public Call onException(final AuthException e) {
                if (e instanceof AccessDeniedException) {
                    return routes.Signup
                            .oAuthDenied(((AccessDeniedException) e)
                                    .getProviderKey());
                }

                // more custom problem handling here...
                return super.onException(e);
            }
        });

        final boolean doDataBootstrap = Play.application().configuration().getBoolean(STARTUP_BOOTSTRAP_PROP, false);

        if (doDataBootstrap) {
            logger.underlyingLogger().info("[Global.init()] Enabled");

            JPA.withTransaction(new play.libs.F.Callback0() {
                @Override
                public void invoke() throws Throwable {
                    init();
                }
            });

            // bootstrap community feed Redis lists
            FeedProcessor.bootstrapCommunityLevelFeed();

            // bootstrap friends Redis sets
            FriendCache.bootstrapFriendsSets();
        }
        else {
            logger.underlyingLogger().info("[Global.init()] Disabled");
        }

        logger.underlyingLogger().info("NewsFeed timeTolerance: " + NewsfeedCommTargetingEngine.NEWSFEED_TIME_TOL);
        logger.underlyingLogger().info("NewsFeed fullLength: "+ NewsfeedCommTargetingEngine.NEWSFEED_FULLLENGTH);
	}

	private void init() {
        if (SecurityRole.findRowCount() == 0L) {
            for (final String roleName : Arrays.asList(
                    SecurityRole.USER,
                    SecurityRole.SUPER_ADMIN,
                    SecurityRole.BUSINESS_ADMIN,
                    SecurityRole.COMMUNITY_ADMIN)) {
                final SecurityRole role = new SecurityRole();
                role.roleName = roleName;
                role.save();
            }
        }

        // data first time bootstrap
        DataBootstrap.bootstrap();

        // version upgrade if any
        SystemVersion.versionUpgradeIfAny();
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
	    return Results.badRequest(error);
	}
	
	@Override
    public Result onError(RequestHeader request, Throwable throwable) {
        return Results.internalServerError(throwable.getMessage());
    }
	
	@Override
    public Result onHandlerNotFound(RequestHeader request) {
        return Results.notFound(views.html.notFound404.render(request.path()));
    }
}