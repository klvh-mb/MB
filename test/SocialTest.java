import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.IOException;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Community;
import models.Notification;
import models.Resource;
import models.SocialObject;
import models.SocialRelation;
import models.SocialRelation.Action;
import models.User;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.Play;
import play.db.jpa.JPA;

import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotPostableException;

public class SocialTest {

	User user1 = new User("Test User 1", "Singh", "Paul","testuser1@test.com");
	User user2 = new User("Test User 2", "Singh", "Bedi","testuser2@test.com");
	User user3 = new User("Test User 3", "Kumar", "Sodhi","testuser3@test.com");
	Community community1 = new Community("Test Community 1", user2);
	Community community2 = new Community("Test Community 2", user2);

	@Before
	public void initSetUp() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						JPA.em().persist(user1);
						JPA.em().persist(user2);
						JPA.em().persist(user3);
						JPA.em().persist(community1);
						JPA.em().persist(community2);
						userRequestedToJoinCommunity();
						userPostedOnCommunity();
					}
				});

			}

		});

	}

	private void userRequestedToJoinCommunity() {
		try {
			user1.requestedToJoin(community1);
		} catch (SocialObjectNotJoinableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void userPostedOnCommunity() {
		try {
			user1.postedOn(community1, "Hello Community 1, Post 1");
			user1.postedOn(community1, "Hello Community 1, Post 2");
			user1.postedOn(community1, "Hello Community 1, Post 2");
			user1.postedOn(community1, "Hello Community 1");
			user1.postedOn(community2, "Hello Community 2, Post 1");
			user2.postedOn(community2, "Hello Community 2");
			user2.postedOn(community1, "Hello Community 1");

		} catch (SocialObjectNotPostableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void userSetProfileImage() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Resource photoProfile;
						java.io.File source = new java.io.File(Play.application()
								.path() + "/test/files/profile.jpg");
						try {
							user1 = User.searchUsername("testuser1@test.com");
							photoProfile = user1.setPhotoProfile(source);
							org.junit.Assert.assertEquals(
									photoProfile.resourceName, "profile.jpg");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						java.io.File source = new java.io.File(Play.application()
								.path() + "/test/files/profile.jpg");
						try {
							user1 = User.searchUsername("testuser1@test.com");
							Resource currentPhotoProfile = user1.getPhotoProfile();
							org.junit.Assert.assertEquals(FileUtils.checksumCRC32(source), FileUtils.checksumCRC32(currentPhotoProfile.getRealFile()));

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

			}
		});
		
	}

	@Test
	public void userLikeSearch_OnName() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						org.junit.Assert.assertEquals(User.searchLike("Singh")
								.size(), 2);
						org.junit.Assert.assertEquals(User.searchLike("Test")
								.size(), 3);
						org.junit.Assert.assertEquals(User.searchLike("Sodhi")
								.size(), 1);
					}
				});
			}
		});
	}

	@Test
	public void userPostCountOn_AnyCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<SocialRelation> q = cb
								.createQuery(SocialRelation.class);
						Root<SocialRelation> c = q.from(SocialRelation.class);
						q.select(c);
						q.where(cb.and(cb.equal(c.get("actor"), user2), cb
								.equal(c.get("action"),
										SocialRelation.Action.POSTED)));
						List<SocialRelation> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);

						q.where(cb.and(cb.equal(c.get("actor"), user1), cb
								.equal(c.get("action"),
										SocialRelation.Action.POSTED)));
						result = JPA.em().createQuery(q).getResultList();
						org.junit.Assert.assertEquals(result.size(), 5);
					}
				});

			}

		});
	}

	@Test
	public void userPostCountOn_AParticularCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<SocialRelation> q = cb
								.createQuery(SocialRelation.class);
						Root<SocialRelation> c = q.from(SocialRelation.class);
						q.select(c);
						q.where(cb.and(cb.equal(c.get("actor"), user1),
								c.get("target").in(community1.posts)));
						List<SocialRelation> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 4);
					}
				});

			}

		});
	}

	@Test
	public void userNotificationOn_JoinRequestOfCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						q.setParameter(1, user2);
						q.setParameter(2, Action.JOIN_REQUESTED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 1);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 1);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

					}
				});

			}

		});
	}

	@Test
	public void communityOwnerAccepts_JoinRequest_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							user2.joinRequestAccepted(community1, user1);
						} catch (SocialObjectNotJoinableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Community community = JPA.em().find(Community.class,
								community1.id);

						// Assert for new Member
						assertThat(community.members, contains(user1));

						// Assert for reation
						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<SocialRelation> q = cb
								.createQuery(SocialRelation.class);
						Root<SocialRelation> c = q.from(SocialRelation.class);
						q.select(c);
						q.where(cb.and(cb.equal(c.get("actor"), user1),
								cb.equal(c.get("target"), community1)));
						SocialRelation socialAction = JPA.em().createQuery(q)
								.getSingleResult();

						org.junit.Assert.assertEquals(socialAction.action,
								SocialRelation.Action.MEMBER);

						// Assert for user accepted notification.
						Query nq = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						nq.setParameter(1, user1);
						nq.setParameter(2, Action.MEMBER);
						Notification result = (Notification) nq
								.getSingleResult();
						org.junit.Assert.assertNotNull(result);
						org.junit.Assert.assertEquals(result.message,
								"You are now member of Test Community 1");

					}
				});

			}

		});
	}

	@After
	public void end() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {

						List<SocialObject> result_1 = JPA
								.em()
								.createQuery(
										"SELECT sa from SocialObject sa where name like  'Test%'")
								.getResultList();
						for (SocialObject _so : result_1) {
							Query q = JPA
									.em()
									.createQuery(
											"SELECT sa from SocialRelation sa where actor = ?1 or target = ?1");
							q.setParameter(1, _so);
							List<SocialRelation> result_2 = q.getResultList();

							for (SocialRelation _sa : result_2) {
								q = JPA.em()
										.createQuery(
												"Delete Notification  where socialAction = ?1 ");
								q.setParameter(1, _sa);
								q.executeUpdate();
								JPA.em().remove(_sa);
							}

						}
						for (SocialObject _so : result_1) {
							JPA.em().remove(_so);
						}
					}
				});

			}

		});
	}

}
