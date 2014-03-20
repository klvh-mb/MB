import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import junit.framework.Assert;
import models.Community;
import models.Notification;
import models.Resource;
import models.User;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mnt.exception.SocialObjectNotJoinableException;

import play.Play;
import play.db.jpa.JPA;
import play.mvc.Result;


public class UserTestData {

	String unverifiedUser = "unverifiedUser@test.com";
	String verifiedUser = "verifiedUser@test.com";
	Community Group ;

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

		nameEmailMap.put("Jagbir P", "jagbir.singh@test.com");
		nameEmailMap.put("amit G", "jagbir.friend1@test.com");
		nameEmailMap.put("Nagesh D", "jagbir.friend2@test.com");
		nameEmailMap.put("Dherej b", "jagbir.friend3@test.com");
		nameEmailMap.put("Harshad G", "jagbir.friend.Request1@test.com");
		nameEmailMap.put("Pravin B", "jagbir.friend.Request2@test.com");
		nameEmailMap.put("Harbir P", "jagbir.father@test.com");
		nameEmailMap.put("Ankush P", "jagbir.friend4@test.com");
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
			
		}
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
	public void getCommList() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						User jagbir = User.findByEmail("jagbir.singh@test.com");
						User jagbir_friend_Amit = User
								.findByEmail("jagbir.friend1@test.com");
						User friend1 = User
								.findByEmail("jagbir.friend4@test.com");
						List<Community> commList = jagbir_friend_Amit.getListOfNotJoinedCommunities();
						System.out.println("jagbir_friend_Amit ::::: "+commList.size());
						for(Community community : commList){
							System.out.println("jagbir_friend_Amit community::::: "+community.name);
						}
					}
				});
			}
		});
		Assert.assertEquals(1, 1);
	}
	
	
	

	
	@Test
	public void communityAdds() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						User jagbir = User.findByEmail("jagbir.singh@test.com");
						User jagbir_friend_Amit = User
								.findByEmail("jagbir.friend9@test.com");
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
						
						Community Group7 = new Community(
								"Test Group1", friend5);
						Group7.save();
						Community Group6= new Community(
								"Test Group2", jagbir_friend_Amit);
						Group6.save();
						Community Group3 = new Community(
								"Test Group3", jagbir);
						Group3.save();
						
						Community Group1 = new Community(
								"Test Group4", friend2);
						Group1.save();
						
						Community Group2 = new Community(
								"Test Group5", friend1);
						Group2.save();
						Community Group5 = new Community(
								"Test Group6", friend3);
						Group5.save();
						Community Group4 = new Community(
								"Test Group7", friend4);
						Group4.save();
						
						try {
							Group1.ownerAsMember(friend2);
							Group2.ownerAsMember(friend1);
							Group3.ownerAsMember(jagbir);
							Group4.ownerAsMember(friend4);
							Group5.ownerAsMember(friend3);
							Group6.ownerAsMember(jagbir_friend_Amit);
							Group7.ownerAsMember(friend5);
							
						} catch (SocialObjectNotJoinableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						java.io.File source1 = new java.io.File(Play
								.application().path()
								+ "/test/files/people1.jpg");

						java.io.File source2 = new java.io.File(Play
								.application().path()
								+ "/test/files/people2.jpg");
						
						java.io.File source3 = new java.io.File(Play
								.application().path()
								+ "/test/files/people3.jpg");

						java.io.File source4 = new java.io.File(Play
								.application().path()
								+ "/test/files/people4.jpg");
						
						try {
							Group1.setCoverPhoto(source1);
							Group2.setCoverPhoto(source2);
							Group3.setCoverPhoto(source3);
							Group4.setCoverPhoto(source4);
							Group5.setCoverPhoto(source3);
							Group6.setCoverPhoto(source2);
							Group7.setCoverPhoto(source1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					

					}
				});

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
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
						java.io.File source1 = new java.io.File(Play
								.application().path()
								+ "/test/files/profile2.jpg");

						java.io.File source2 = new java.io.File(Play
								.application().path()
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

							friend1.sendFriendInviteTo(jagbir);
							friend2.sendFriendInviteTo(jagbir);
							friend3.sendFriendInviteTo(jagbir);
							friend4.sendFriendInviteTo(jagbir);
							friend5.sendFriendInviteTo(jagbir);
							friend6.sendFriendInviteTo(jagbir);
							friend8.sendFriendInviteTo(jagbir);
							friend9.sendFriendInviteTo(jagbir);
							friend10.sendFriendInviteTo(jagbir);
							friend11.sendFriendInviteTo(jagbir);

							jagbir.onFriendRequestAccepted(friend1);
							jagbir.onFriendRequestAccepted(friend2);
							jagbir.onFriendRequestAccepted(friend3);
							jagbir.onFriendRequestAccepted(friend4);
							jagbir.onFriendRequestAccepted(friend5);
							jagbir.onFriendRequestAccepted(friend6);
							jagbir.onFriendRequestAccepted(friend8);
							jagbir.onFriendRequestAccepted(friend9);
							jagbir.onFriendRequestAccepted(friend10);
							jagbir.onFriendRequestAccepted(friend11);

						} catch (IOException | SocialObjectNotJoinableException e) {
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
