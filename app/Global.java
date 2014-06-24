import java.util.Arrays;

import models.SecurityRole;
import play.Application;
import play.GlobalSettings;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Call;
import processor.FeedProcessor;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;

public class Global extends GlobalSettings {
    private static final play.api.Logger logger = play.api.Logger.apply("application");
    
	@Transactional
	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Application.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return routes.Application.index();
			}

			@Override
			public Call afterLogout() {
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
		
		JPA.withTransaction(new play.libs.F.Callback0() {
			@Override
			public void invoke() throws Throwable {
				initialData();
			}
		});
		
		//FeedProcessor.updatesUserLevelFeed();
		FeedProcessor.updateCommunityLevelFeed();
	}

	private void initialData() {
	    if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("[Global.initialData()]");
        }
	    
		if (SecurityRole.findRowCount() == 0L) {
			for (final String roleName : Arrays.asList(
			        controllers.Application.USER_ROLE, 
			        controllers.Application.SUPER_ADMIN_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
		
		DataBootstrap.bootstrap();
	}
}