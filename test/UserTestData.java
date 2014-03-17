import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import models.Resource;
import models.User;

import org.junit.Before;
import org.junit.Test;

import com.mnt.exception.SocialObjectNotJoinableException;

import play.Play;
import play.db.jpa.JPA;
import play.mvc.Result;

public class UserTestData {

	String unverifiedUser = "unverifiedUser@test.com";
	String verifiedUser = "verifiedUser@test.com";

	Map<String, String> nameEmailMap = new HashMap<>();

	public void addUser(final String name, final String email) {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						String password = "PaSSW0rd";
						String repeatPassword = "PaSSW0rd";

						Map<String, String> data = new HashMap<String, String>();
						data.put("email", email);
						data.put("password", password);
						data.put("name", name);
						data.put("repeatPassword", repeatPassword);
						Result result = callAction(
								controllers.routes.ref.Application
										.doSignupForTest(), fakeRequest()
										.withFormUrlEncodedBody(data));
						status(result);
					}
				});
			}
		});
	}

	@Before
	public void init() {
		/*
		 * nameEmailMap.put("Jagbir P", "jagbir.singh@test.com");
		 * nameEmailMap.put("amit G", "jagbir.friend1@test.com");
		 * nameEmailMap.put("Nagesh D", "jagbir.friend2@test.com");
		 * nameEmailMap.put("Dherej b", "jagbir.friend3@test.com");
		 * nameEmailMap.put("Harshad G", "jagbir.friend.Request1@test.com");
		 * nameEmailMap.put("Pravin B", "jagbir.friend.Request2@test.com");
		 * nameEmailMap.put("Harbir P", "jagbir.father@test.com");
		 */

		/*nameEmailMap.put("Ankush P", "jagbir.friend4@test.com");
		nameEmailMap.put("Ajinkya G", "jagbir.friend5@test.com");
		nameEmailMap.put("Ashish D", "jagbir.friend6@test.com");
		nameEmailMap.put("Dhananjay b", "jagbir.friend7@test.com");
		nameEmailMap.put("Tushar G", "jagbir.friend8@test.com");
		nameEmailMap.put("Pankaj B", "jagbir.friend9@test.com");
		nameEmailMap.put("Sharad P", "jagbir.friend10@test.com");
		nameEmailMap.put("Deepak b", "jagbir.friend11@test.com");
		nameEmailMap.put("Cheatan G", "jagbir.friend12@test.com");
		nameEmailMap.put("Pushkar B", "jagbir.friend13@test.com");
		nameEmailMap.put("Sharad P", "jagbir.friend14@test.com");

		for (Map.Entry<String, String> entry : nameEmailMap.entrySet()) {
			addUser(entry.getKey(), entry.getValue());
		}*/
	}

	@Test
	public void signupDone() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						for (Map.Entry<String, String> entry : nameEmailMap
								.entrySet()) {
							User.verify(User.searchEmail(entry.getValue()));
						}
					}
				});
			}
		});
		Assert.assertEquals(1, 1);
	}

	@Test
	public void userAdds() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						java.io.File source1 = new java.io.File(Play.application().path()
								+ "/test/files/profile2.jpg");
						
						java.io.File source2 = new java.io.File(Play.application().path()
								+ "/test/files/profile.jpg");
						
						
						User jagbir = User.findByEmail("jagbir.singh@test.com");
						User friend1 = User
								.findByEmail("jagbir.friend4@test.com");
						User friend2 = User
								.findByEmail("jagbir.friend5@test.com");
						User friend3 = User
								.findByEmail("jagbir.friend6@test.com");
						User friend4 = User
								.findByEmail("jagbir.friend7@test.com");
						User friend5 = User
								.findByEmail("jagbir.friend8@test.com");
						User friend6 = User
								.findByEmail("jagbir.friend9@test.com");
						/*User friend7 = User
								.findByEmail("jagbir.friend10@test.com");*/
						User friend8 = User
								.findByEmail("jagbir.friend11@test.com");
						User friend9 = User
								.findByEmail("jagbir.friend12@test.com");
						User friend10 = User
								.findByEmail("jagbir.friend13@test.com");
						User friend11 = User
								.findByEmail("jagbir.friend14@test.com");

						try {
							Resource photoProfile;
							photoProfile = friend1.setPhotoProfile(source1);
							JPA.em().persist(photoProfile);
							photoProfile = friend2.setPhotoProfile(source2);
							JPA.em().persist(photoProfile);
							photoProfile = friend3.setPhotoProfile(source1);
							JPA.em().persist(photoProfile);
							photoProfile = friend4.setPhotoProfile(source2);
							JPA.em().persist(photoProfile);
							photoProfile = friend5.setPhotoProfile(source1);
							JPA.em().persist(photoProfile);
							photoProfile = friend6.setPhotoProfile(source2);
							JPA.em().persist(photoProfile);
							photoProfile = friend8.setPhotoProfile(source1);
							JPA.em().persist(photoProfile);
							photoProfile = friend9.setPhotoProfile(source2);
							JPA.em().persist(photoProfile);
							photoProfile = friend10.setPhotoProfile(source1);
							JPA.em().persist(photoProfile);
							photoProfile = friend11.setPhotoProfile(source2);
							JPA.em().persist(photoProfile);
							
							/*friend1.onFriendRequest(jagbir);

							friend2.onFriendRequest(jagbir);
							friend3.onFriendRequest(jagbir);
							friend4.onFriendRequest(jagbir);
							friend5.onFriendRequest(jagbir);
							friend6.onFriendRequest(jagbir);
							//friend7.onFriendRequest(jagbir);
							friend8.onFriendRequest(jagbir);
							friend9.onFriendRequest(jagbir);
							friend10.onFriendRequest(jagbir);
							friend11.onFriendRequest(jagbir);
							
							
							jagbir.onFriendRequestAccepted(friend1);
							jagbir.onFriendRequestAccepted(friend2);
							jagbir.onFriendRequestAccepted(friend3);
							jagbir.onFriendRequestAccepted(friend4);
							jagbir.onFriendRequestAccepted(friend5);
							jagbir.onFriendRequestAccepted(friend6);
//							jagbir.onFriendRequestAccepted(friend7);
							jagbir.onFriendRequestAccepted(friend8);
							jagbir.onFriendRequestAccepted(friend9);
							jagbir.onFriendRequestAccepted(friend10);
							jagbir.onFriendRequestAccepted(friend11);*/

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});
			}
		});
		Assert.assertEquals(1, 1);
	}
}
