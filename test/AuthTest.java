import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.running;
import static play.test.Helpers.status;
import java.util.HashMap;
import java.util.Map;

import models.TokenAction;
import models.TokenAction.Type;
import models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controllers.routes;
import static org.junit.Assert.*;
import play.db.jpa.JPA;
import play.mvc.Result;
public class AuthTest {

	String unverifiedUser = "unverifiedUser@test.com";
	String verifiedUser = "verifiedUser@test.com";
	@Before
	public void init() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						
						String password = "PaSSW0rd";
						String repeatPassword = "PaSSW0rd";
						String name = "Test User";
						Map<String, String> data = new HashMap<String, String>();
						data.put("email", unverifiedUser);
						data.put("password", password);
						data.put("name", name);
						data.put("repeatPassword", repeatPassword);
						Result result = callAction(
								controllers.routes.ref.Application.doSignupForTest(),
								fakeRequest().withFormUrlEncodedBody(data));
						redirectLocation(result);
					}
				});
				
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						
						String password = "PaSSW0rd";
						String repeatPassword = "PaSSW0rd";
						String name = "Test User";
						Map<String, String> data = new HashMap<String, String>();
						data.put("email", verifiedUser);
						data.put("password", password);
						data.put("name", name);
						data.put("repeatPassword", repeatPassword);
						Result result = callAction(
								controllers.routes.ref.Application.doSignupForTest(),
								fakeRequest().withFormUrlEncodedBody(data));
						redirectLocation(result);
						
						User.verify(User.searchEmail(verifiedUser));
						
						
					}
				});
				
				
				
			}
		});
	}
	
	
	
	@Test
	public void signupDone() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						User user = User.searchEmail(unverifiedUser);
						assertNotNull(user);
						
						user = User.searchEmail(verifiedUser);
						assertNotNull(user);
					}
				});
			}
		});
	}
	
	@Test
	public void tryingToLoginOnUnverifiedAccount() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						
						String password = "PaSSW0rd";
						Map<String, String> data = new HashMap<String, String>();
						data.put("email", unverifiedUser);
						data.put("password", password);
						
						Result result = callAction(
								controllers.routes.ref.Application.doLoginForTest(),
								fakeRequest().withFormUrlEncodedBody(data));
						
						assertThat(redirectLocation(result)).isEqualTo("/accounts/unverified");
					}
				});
				
			}
		});
	}
	
	@Test
	public void tryingToLoginOnVerifiedAccount() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						
						String password = "PaSSW0rd";
						Map<String, String> data = new HashMap<String, String>();
						data.put("email", verifiedUser);
						data.put("password", password);
						
						Result result = callAction(
								controllers.routes.ref.Application.doLoginForTest(),
								fakeRequest().withFormUrlEncodedBody(data));
						
						assertThat(redirectLocation(result)).isEqualTo(routes.Application.index().url());
						
					}
				});
				
			}
		});
	}
	
	@After
	public void endTest() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						User user = User.searchEmail(unverifiedUser);
						TokenAction.deleteByUser(user, Type.EMAIL_VERIFICATION);
						user.delete();
						
						user = User.searchEmail(verifiedUser);
						user.delete();
					}
				});
			}
		});
	}
	
	/*private Http.Session signupAndLogin() {
		String email = "user@example.com";
		String password = "PaSSW0rd";
		{
			// Signup with a username/password
			Map<String, String> data = new HashMap<String, String>();
			data.put("email", email);
			data.put("password", password);
			Result result = callAction(
					controllers.routes.ref.Application.doSignup(),
					fakeRequest().withFormUrlEncodedBody(data));
			
			assertThat(status(result)).isEqualTo(SEE_OTHER);
		}
		{
			// Validate the token
			String token = upAuthProvider().getVerificationToken(email);
			assertThat(token).isNotNull();
			Result result = callAction(controllers.routes.ref.Application
					.verify(token));
			assertThat(status(result)).isEqualTo(SEE_OTHER);
			assertThat(upAuthProvider().getVerificationToken(email)).isNull();
			play.api.mvc.Result actualResult = actualResult(result);
			// We should actually be logged in here, but let's ignore that
			// as we want to test login too.
			assertThat(
					play.api.test.Helpers.redirectLocation(actualResult).get())
					.isEqualTo("/");
		}
		{
			// Log the user in
			Map<String, String> data = new HashMap<String, String>();
			data.put("email", email);
			data.put("password", password);
			Result result = callAction(
					controllers.routes.ref.Application.doLogin(), fakeRequest()
							.withFormUrlEncodedBody(data));
			play.api.mvc.Result actualResult = actualResult(result);
			assertThat(status(result)).isEqualTo(SEE_OTHER);
			assertThat(
					play.api.test.Helpers.redirectLocation(actualResult).get())
					.isEqualTo("/");
			// Create a Java session from the Scala session
			Map<String, String> sessionData =
					asJavaMap(play.api.test.Helpers.session(actualResult)
							.data());
			return new Http.Session(sessionData);
		}
	}
	
	private TestUsernamePasswordAuthProvider upAuthProvider() {
		return Play.application()
				.plugin(TestUsernamePasswordAuthProvider.class);
	}
	
	private play.api.mvc.Result actualResult(Result asyncResult) {
		return (new Promise<play.api.mvc.Result>(
				((play.api.mvc.AsyncResult) asyncResult.getWrappedResult())
						.result())).get();
	}*/


}
