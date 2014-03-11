import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Comment;
import models.Community;
import models.Notification;
import models.Post;
import models.Resource;
import models.SocialObject;
import models.SocialRelation;
import models.SocialRelation.Action;
import models.SocialRelation.ActionType;
import models.User;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.db.jpa.JPA;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotPostableException;

import domain.CommentType;
import domain.PostType;

public class SocialTest {

	User user1 = new User("Test User 1", "Singh", "Paul", "testuser1@test.com");
	User user2 = new User("Test User 2", "Singh", "Bedi", "testuser2@test.com");
	User user3 = new User("Test User 3", "Kumar", "Sodhi", "testuser3@test.com");
	Community community1 = new Community("Test Community 1", user2);
	Community community2 = new Community("Test Community 2", user2);
	SocialObject post, question;
	Resource photoProfile;

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
						userRequestedToJoin();
						userPostedOnCommunity();
					}
				});

			}

		});

	}

	private void userRequestedToJoin() {
		try {
			user1.requestedToJoin(community1);
			user1.onFriendRequest(user2);
			user3.onFriendRequest(user2);
		} catch (SocialObjectNotJoinableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void userCommentedOnSocialObject(SocialObject socialObject) {
		try {
			user3.commentedOn(socialObject, "This is First Comment : "
					+ user3.name);
			user2.commentedOn(socialObject, "This is Secound Comment : "
					+ user2.name);
			user1.commentedOn(socialObject, "This is Third Comment : "
					+ user2.name);
		} catch (SocialObjectNotCommentableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void userAnsweredOnSocialObject(SocialObject socialObject) {
		try {
			user3.answeredOn(socialObject, "This is First Answer : "
					+ user3.name);
			user2.answeredOn(socialObject, "This is Secound Answer : "
					+ user2.name);
		} catch (SocialObjectNotCommentableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void onLike(SocialObject socialObject) {

		try {
			user2.likesOn(socialObject);
			user3.likesOn(socialObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	

	private void userPostedOnCommunity() {
		try {
			java.io.File source = new java.io.File(Play.application().path()
					+ "/test/files/profile2.jpg");

			post = user1.postedOn(community1, "Hello Community 1, Post 1");
			JPA.em().persist(post);
			userCommentedOnSocialObject(post);
			onLike(post);
			
			photoProfile = user3.setPhotoProfile(source);
			JPA.em().persist(photoProfile);
			userCommentedOnSocialObject(photoProfile);
			onLike(photoProfile);

			question = user2.questionedOn(community1, "What is Funtion ????? ");
			JPA.em().persist(question);
			userCommentedOnSocialObject(question);
			userAnsweredOnSocialObject(question);
			onLike(question);

			user1.postedOn(community1, "Hello Community 1, Post 2");
			user1.postedOn(community1, "Hello Community 1, Post 2");
			user1.postedOn(community1, "Hello Community 1");
			user1.postedOn(community2, "Hello Community 2, Post 1");
			user2.postedOn(community2, "Hello Community 2");
			user2.postedOn(community1, "Hello Community 1");

		} catch (SocialObjectNotPostableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void userSetProfileImage() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Resource photoProfile;
						java.io.File source = new java.io.File(Play
								.application().path()
								+ "/test/files/profile.jpg");
						try {
							user1 = User.searchUsername("testuser1@test.com");
							photoProfile = user1.setPhotoProfile(source);
							org.junit.Assert.assertEquals(
									photoProfile.resourceName, "profile.jpg");
							user1 = User.searchUsername("testuser1@test.com");
							org.junit.Assert.assertEquals(
									user1.albumPhotoProfile.resources.size(), 1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						java.io.File source = new java.io.File(Play
								.application().path()
								+ "/test/files/profile.jpg");
						try {
							user1 = User.searchUsername("testuser1@test.com");
							Resource currentPhotoProfile = user1
									.getPhotoProfile();
							org.junit.Assert.assertEquals(FileUtils
									.checksumCRC32(source), FileUtils
									.checksumCRC32(currentPhotoProfile
											.getRealFile()));
							File file = currentPhotoProfile.getRealFile()
									.getParentFile();
							/*org.junit.Assert.assertTrue(new File(file,
									"thumbnail.profile.jpg").exists());*/
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
	public void userLikeOnProfilePhoto() {
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
						q.where(cb.and(cb.equal(c.get("target"), photoProfile),
								cb.equal(c.get("action"),
										SocialRelation.Action.LIKED)));

						List<SocialRelation> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
					}
				});

			}
		});

	}

	@Test
	public void userLikeCountOn_AnyCommunityPost_Test() {
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
						q.where(cb.and(cb.equal(c.get("target"), post), cb
								.equal(c.get("action"),
										SocialRelation.Action.LIKED)));

						List<SocialRelation> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);

					}
				});

			}

		});
	}

	@Test
	public void userRemoveProfileImage_Test() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Resource photoProfile;
						java.io.File source = new java.io.File(Play
								.application().path()
								+ "/test/files/profile.jpg");
						try {
							user1 = User.searchUsername("testuser1@test.com");
							photoProfile = user1.setPhotoProfile(source);
							org.junit.Assert.assertEquals(
									photoProfile.resourceName, "profile.jpg");
							user1 = User.searchUsername("testuser1@test.com");
							org.junit.Assert.assertEquals(
									user1.albumPhotoProfile.resources.size(), 1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {

						try {
							user1 = User.searchUsername("testuser1@test.com");
							Resource currentPhotoProfile = user1
									.getPhotoProfile();
							String toBeDeletedFilePath = currentPhotoProfile
									.getPath();

							user1.removePhotoProfile(currentPhotoProfile);
							user1 = User.searchUsername("testuser1@test.com");
							org.junit.Assert.assertEquals(
									user1.albumPhotoProfile.resources.size(), 0);
							File f = new File(toBeDeletedFilePath);
							org.junit.Assert.assertTrue(!f.exists());

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
	public void userPostCountOn_AnyCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {

						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<Post> q = cb.createQuery(Post.class);
						Root<Post> c = q.from(Post.class);
						q.select(c);
						q.where(cb.and(
								cb.equal(c.get("postType"), PostType.SIMPLE),
								cb.equal(c.get("community"), community1)));
						List<Post> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 5);

						q.where(cb.and(
								cb.equal(c.get("postType"), PostType.QUESTION),
								cb.equal(c.get("community"), community1)));
						result = JPA.em().createQuery(q).getResultList();
						org.junit.Assert.assertEquals(result.size(), 1);

					}
				});

			}

		});
	}

	@Test
	public void userQuestionsCountOn_AnyCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {

						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<Post> q = cb.createQuery(Post.class);
						Root<Post> c = q.from(Post.class);
						q.select(c);
						q.where(cb.and(
								cb.equal(c.get("postType"), PostType.QUESTION),
								cb.equal(c.get("community"), community1)));
						List<Post> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 1);

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

	@Test
	public void userAccepts_FriendRequest_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							user2.onFriendRequestAccepted(user1);
						} catch (SocialObjectNotJoinableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Assert for new Member
						assertThat(user2.friends, contains(user1));

						// Assert for reation
						CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
						CriteriaQuery<SocialRelation> q = cb
								.createQuery(SocialRelation.class);
						Root<SocialRelation> c = q.from(SocialRelation.class);
						q.select(c);
						q.where(cb.and(cb.equal(c.get("actor"), user1),
								cb.equal(c.get("target"), user2)));
						SocialRelation socialAction = JPA.em().createQuery(q)
								.getSingleResult();

						org.junit.Assert.assertEquals(socialAction.action,
								SocialRelation.Action.FRIEND);

						// Assert for user accepted notification.
						Query nq = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						nq.setParameter(1, user1);
						nq.setParameter(2, Action.FRIEND);
						Notification result = (Notification) nq
								.getSingleResult();
						org.junit.Assert.assertNotNull(result);
						org.junit.Assert.assertEquals(result.message,
								"You are now Friend of Test User 2");

					}
				});

			}

		});
	}

	@Test
	public void userCommentCountOn_AnyCommunityPost_Test() {
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
						q.where(cb.and(cb.equal(c.get("target"), post), cb
								.equal(c.get("action"),
										SocialRelation.Action.COMMENTED)));
						List<SocialRelation> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);

					}
				});

			}

		});
	}

	@Test
	public void userAnswersAndComment_CountOn_AnyCommunityAuestion_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT c from Comment c where commentType = ?1 and socialObject = ?2 ");
						q.setParameter(1, CommentType.SIMPLE);
						q.setParameter(2, question);
						List<Comment> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);

						q.setParameter(1, CommentType.ANSWER);
						q.setParameter(2, question);
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);

					}
				});

			}

		});
	}

	@Test
	public void userCommentCountOn_AnyProfilePhoto_Test() {
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
						q.where(cb.and(cb.equal(c.get("target"), photoProfile),
								cb.equal(c.get("action"),
										SocialRelation.Action.COMMENTED)));
						List<SocialRelation> result = JPA.em().createQuery(q)
								.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);

					}
				});

			}

		});
	}

	@Test
	public void sendNotificationOn_JoinRequestOfCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.actionType = ?2 ");
						q.setParameter(1, user2);
						q.setParameter(2, ActionType.JOIN_REQUESTED);
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
	public void sendNotificationOn_LikeToOwnerOfCommunityPost_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						q.setParameter(1, user1);
						q.setParameter(2, Action.LIKED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

					}
				});

			}

		});
	}
	
	@Test
	public void sendNotificationOn_LikeToOwnerOfQusetionpost_Test() {
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
						q.setParameter(2, Action.LIKED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

					}
				});

			}

		});
	}

	@Test
	public void sendNotificationOn_CommentToOwnerOfCommunityPost_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						q.setParameter(1, user1);
						q.setParameter(2, Action.COMMENTED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

					}
				});

			}

		});
	}
	
	@Test
	public void sendNotificationOn_AnswerToOwnerOfCommunityQuestion_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent.owner = ?1 and socialAction.action = ?2 ");
						q.setParameter(1, user2);
						q.setParameter(2, Action.ANSWERED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

					}
				});

			}

		});
	}

	@Test
	public void sendNotificationOn_CommentToOwnerOfProfiliePhoto_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						q.setParameter(1, user3);
						q.setParameter(2, Action.COMMENTED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 3);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

					}
				});

			}

		});
	}

	@Test
	public void sendNotifiacationOn_FriendRequests() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						// Assert for user Friend Request notification.
						Query nq = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.actionType = ?2 ");
						nq.setParameter(1, user2);
						nq.setParameter(2, ActionType.FRIEND_REQUESTED);
						List<Notification> result = nq.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
					}
				});

			}
		});

	}
	
	//@Test
	public void sendNotifiacationOn_RelationShipRequest() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						// Assert for user Friend Request notification.
						Query nq = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						nq.setParameter(1, user1);
						nq.setParameter(2, Action.RELATIONSHIP_REQUESTED);
						List<Notification> result = nq.getResultList();
						org.junit.Assert.assertEquals(result.size(), 1);
					}
				});

			}
		});

	}
	
	@Test
	public void getUser_FriendList() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							user2.onFriendRequestAccepted(user3);
						} catch (SocialObjectNotJoinableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for(User frnd : user2.friends){
							System.out.println("Friend :::::: "+frnd.name);
						}
						org.junit.Assert.assertEquals(user2.friends.size(), 1);
						


					}
				});

			}
		});

	}

	@Test
	public void sendNotificationOn_LikeToOwnerOfProfilePhoto_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
						q.setParameter(1, user3);
						q.setParameter(2, Action.LIKED);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 2);
						org.junit.Assert.assertEquals(result.get(0).readed,
								true);

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
