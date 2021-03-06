import java.util.Arrays;
import java.util.Date;

import common.cache.FriendCache;
import common.schedule.CommandChecker;
import common.schedule.JobScheduler;
import data.DataBootstrap;
import models.Notification;
import models.SecurityRole;
import models.SystemVersion;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.db.jpa.JPA;
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
import tagword.TaggingEngine;
import targeting.community.NewsfeedCommTargetingEngine;

/**
 *
 */
public class Global extends GlobalSettings {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

    // Configurations
    private static final String STARTUP_BOOTSTRAP_PROP = "startup.data.bootstrap";
    private static final String RUN_BACKGROUND_TASKS_PROP = "run.backgroundtasks";

    /**
     * @param app
     */
	public void onStart(Application app) {
        final boolean runBackgroundTasks = Play.application().configuration().getBoolean(RUN_BACKGROUND_TASKS_PROP, false);
        if (runBackgroundTasks) {
            // schedule background jobs
            scheduleJobs();
        }

	    PlayAuthenticate.setResolver(new Resolver() {
            @Override
            public Call login(final Session session) {
                // Your login page
                return routes.Application.login();
            }

            @Override
            public Call afterAuth(final Session session) {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
            	
            	// reset last login time
            	final User user = controllers.Application.getLocalUser(session);
    		    controllers.Application.setMobileUserAgent(user);
    		    user.setLastLogin(new Date());
    		    
                //return routes.Application.mainHome();
                return routes.Application.mainFrontpage();
            }

            @Override
            public Call afterLogout(final Session session) {
                return routes.Application.mainFrontpage();
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

    /**
     * scheduleJobs
     */
    private void scheduleJobs() {
        // Note: (OFF as of 20150621) schedule Gamification EOD accounting daily at 3:00am HKT
//        JobScheduler.getInstance().schedule("gamificationEOD", "0 00 3 ? * *",
//            new Runnable() {
//                public void run() {
//                    try {
//                       JPA.withTransaction(new play.libs.F.Callback0() {
//                            public void invoke() {
//                                GameAccountTransaction.performEndOfDayTasks(1);
//                            }
//                        });
//                    } catch (Exception e) {
//                        logger.underlyingLogger().error("Error in gamificationEOD", e);
//                    }
//                }
//            }
//        );

        // schedule to purge notifications daily at 4:00am HKT
        JobScheduler.getInstance().schedule("purgeNotification", "0 00 4 ? * *",
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                                   Notification.purgeNotification();
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in purgeNotification", e);
                    }
                }
            }
        );

        // schedule to index tag words daily at 4:15am HKT
        JobScheduler.getInstance().schedule("indexTagWords", "0 15 4 ? * *",
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                                   TaggingEngine.indexTagWords();
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in indexTagWords", e);
                    }
                }
            }
        );

        // schedule to check command every 2 min.
        JobScheduler.getInstance().schedule("commandCheck", 120000,
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                                CommandChecker.checkCommandFiles();
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in indexTagWords", e);
                    }
                }
            }
        );
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