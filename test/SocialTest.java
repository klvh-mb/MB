import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import junit.framework.Assert;
import models.Folder;
import models.Album;
import models.Comment;
import models.Community;
import models.Conversation;
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
import org.junit.Ignore;
import org.junit.Test;

import play.Play;
import play.db.jpa.JPA;

import com.mnt.exception.SocialObjectNotCommentableException;
import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotPostableException;

import domain.CommentType;
import domain.PostType;
import domain.SocialObjectType;

public class SocialTest {

	User user1 = new User("Test User 1", "Singh", "Paul", "testuser1@Test.com");
	User user2 = new User("Test User 2", "Singh", "Bedi", "testuser2@Test.com");
	User user3 = new User("Test User 3", "Kumar", "Sodhi", "testuser3@Test.com");

	Community community1 = new Community("Test Community 1", user2);
	Community community2 = new Community("Test Community 2", user2);
	Community community3 = new Community("Test Community 3", user2);
	SocialObject post, question;
	Resource photoProfile;
	SocialObject photoComment, questionComment;

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
			user1.sendFriendInviteTo(user2);
			user3.sendFriendInviteTo(user2);
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
			photoComment = user2.commentedOn(photoProfile, "nice");
			JPA.em().persist(photoComment);
			onLike(photoComment);

			onLike(photoProfile);

			question = user2.questionedOn(community1, "What is Funtion ????? ");
			JPA.em().persist(question);
			userCommentedOnSocialObject(question);
			questionComment = user2.commentedOn(question, "good");
			userAnsweredOnSocialObject(question);
			onLike(question);
			onLike(questionComment);

			user1.postedOn(community1, "Hello Community 1, Post 2");
			user1.postedOn(community1, "Hello Community 1, Post 2");
			user1.postedOn(community1, "Hello Community 1");
			user1.postedOn(community2, "Hello Community 2, Post 1");
			user2.postedOn(community2, "Hello Community 2");
			user2.postedOn(community1, "Hello Community 1");

		} catch (SocialObjectNotPostableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocialObjectNotCommentableException e) {
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
	public void SearchCommunity() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						org.junit.Assert
								.assertEquals(
										User.searchCommunity("Test Community 1")
												.size(), 1);

					}
				});
			}
		});
	}

	@Test
	public void setCoverPhotoToCommunity() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() throws IOException {
						java.io.File source = new java.io.File(Play
								.application().path()
								+ "/test/files/profile2.jpg");
						Resource coverPhoto1 = community1.setCoverPhoto(source);
						org.junit.Assert.assertEquals(coverPhoto1.resourceName,
								"profile2.jpg");
					}
				});
			}
		});
	}

	@Test
	public void sendMessage() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Conversation c1 = user3.sendMessage(user2, "hello");

						Conversation c2 = user2.sendMessage(user3, "hi");

					}
				});
			}
		});
	}

	@Test
	public void isSameConversationObject() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Conversation c1 = user3.sendMessage(user2, "hello");

						Conversation c2 = user2.sendMessage(user3, "hi");

						org.junit.Assert.assertEquals(c1, c2);
					}
				});
			}
		});
	}

	@Test
	public void conversationMessageCount() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						user3.sendMessage(user2, "hello");
						user3.sendMessage(user2, "hello");
						user2.sendMessage(user3, "hi");
						Conversation conversation = user2.conversation.get(0);
						org.junit.Assert.assertEquals(
								conversation.messages.size(), 3);

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
							user1 = User.searchUsername("testuser1@Test.com");
							photoProfile = user1.setPhotoProfile(source);
							org.junit.Assert.assertEquals(
									photoProfile.resourceName, "profile.jpg");
							user1 = User.searchUsername("testuser1@Test.com");
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
							user1 = User.searchUsername("testuser1@Test.com");
							Resource currentPhotoProfile = user1
									.getPhotoProfile();
							org.junit.Assert.assertEquals(FileUtils
									.checksumCRC32(source), FileUtils
									.checksumCRC32(currentPhotoProfile
											.getRealFile()));
							File file = currentPhotoProfile.getRealFile()
									.getParentFile();
							org.junit.Assert.assertTrue(new File(file,
									"thumbnail.profile.jpg").exists());

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
	public void userLikeCountOnQuestionComment() {
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
						q.where(cb.and(cb.equal(c.get("target"),
								questionComment), cb.equal(c.get("action"),
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
	public void sendNotificationOn_OfLikeUserQuestionComment_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Query q = JPA
								.em()
								.createQuery(
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 and socialAction.target = ?3");
						q.setParameter(1, user2);
						q.setParameter(2, Action.LIKED);
						q.setParameter(3, questionComment);
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
	public void sendNotificationOn_OfLikeUserProfileImage_Test() {
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

	@Test
	public void userLikeCountOnProfilePhotoComment() {
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
						q.where(cb.and(cb.equal(c.get("target"), photoComment),
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
	public void sendNotificationOn_OfLikeUserProfileImageComment_Test() {
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
							user1 = User.searchUsername("testuser1@Test.com");
							photoProfile = user1.setPhotoProfile(source);
							org.junit.Assert.assertEquals(
									photoProfile.resourceName, "profile.jpg");
							user1 = User.searchUsername("testuser1@Test.com");
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
							user1 = User.searchUsername("testuser1@Test.com");
							Resource currentPhotoProfile = user1
									.getPhotoProfile();
							String toBeDeletedFilePath = currentPhotoProfile
									.getPath();

							user1.removePhotoProfile(currentPhotoProfile);
							user1 = User.searchUsername("testuser1@Test.com");
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
						List<User> friends = user2.getFriends();
						assertThat(friends, contains(user1));

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
	public void userAnswersAndComment_CountOn_AnyCommunityQuestion_Test() {
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
						org.junit.Assert.assertEquals(result.size(), 4);

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
						org.junit.Assert.assertEquals(result.size(), 4);

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
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 and socialAction.target = ?3 ");
						q.setParameter(1, user1);
						q.setParameter(2, Action.LIKED);
						q.setParameter(3, post);
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
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 and socialAction.target = ?3 ");
						q.setParameter(1, user2);
						q.setParameter(2, Action.LIKED);
						q.setParameter(3, question);
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
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 and socialAction.target = ?3");
						q.setParameter(1, user1);
						q.setParameter(2, Action.COMMENTED);
						q.setParameter(3, post);
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
										"SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 and socialAction.target = ?3");
						q.setParameter(1, user3);
						q.setParameter(2, Action.COMMENTED);
						q.setParameter(3, photoProfile);
						List<Notification> result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 4);
						org.junit.Assert.assertEquals(result.get(0).readed,
								false);

						// Mark as read
						user2.markNotificationRead(result.get(0));
						result = q.getResultList();
						org.junit.Assert.assertEquals(result.size(), 4);
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

	@Test
	public void sendNotifiacationOn_RelationShipRequest() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							user2.onFriendRequestAccepted(user3);
							user2.onFriendRequestAccepted(user1);
						} catch (SocialObjectNotJoinableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						List<User> friends = user2.getFriends();

						if (friends.contains(user1)) {
							try {
								user2.onRelationShipRequest(user1,
										SocialRelation.Action.BROTHER);
							} catch (SocialObjectNotJoinableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
							CriteriaQuery<SocialRelation> q = cb
									.createQuery(SocialRelation.class);
							Root<SocialRelation> c = q
									.from(SocialRelation.class);
							q.select(c);
							q.where(cb.and(cb.equal(c.get("target"), user1), cb
									.equal(c.get("action"),
											SocialRelation.Action.BROTHER)));
							SocialRelation result = JPA.em().createQuery(q)
									.getSingleResult();
							org.junit.Assert.assertEquals(result.actionType,
									ActionType.RELATIONSHIP_REQUESTED);

							// Notification
							Query nq = JPA
									.em()
									.createQuery(
											"SELECT n from Notification n where recipetent = ?1 and socialAction.actionType = ?2 and socialAction.action = ?3 ");
							nq.setParameter(1, user1);
							nq.setParameter(2,
									ActionType.RELATIONSHIP_REQUESTED);
							nq.setParameter(3, Action.BROTHER);
							Notification notificationResult = (Notification) nq
									.getSingleResult();
							org.junit.Assert.assertEquals(
									notificationResult.message, user2.name
											+ " wants to add you in "
											+ Action.BROTHER + " list. ");

						} else {

						}

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
							user2.onFriendRequestAccepted(user1);
						} catch (SocialObjectNotJoinableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						List<User> friends = user2.getFriends();

						for (User frnd : friends) {
							System.out.println("Friend :::::: " + frnd.name);
						}
						org.junit.Assert.assertEquals(friends.size(), 2);

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

	@Test
	public void userCreatesPhotoAlbum() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Resource photoProfile, coverPhoto = null;
						Album photoAlbum1, photoAlbum2;
						java.io.File source1 = new java.io.File(Play
								.application().path() + "/test/files/homer.jpg");
						java.io.File source2 = new java.io.File(Play
								.application().path()
								+ "/test/files/profile.jpg");
						java.io.File source3 = new java.io.File(Play
								.application().path()
								+ "/test/files/profile2.jpg");
						user3 = User.searchUsername("testuser3@Test.com");

						photoAlbum1 = user3.createAlbum("Test My Album 1",
								"this is a album", true,
								SocialObjectType.ALBUMN);
						user2 = User.searchUsername("testuser2@Test.com");
						photoAlbum2 = user2.createAlbum("Test My Album 1",
								"this is a album", true,
								SocialObjectType.ALBUMN);

						org.junit.Assert.assertEquals(user3.album.size(), 1);
						assertThat(user3.album, contains(photoAlbum1));
						assertThat(user2.album, contains(photoAlbum2));
					}
				});

			}
		});

	}

	@Test
	public void userAddPhotoToPhotoAlbum() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							Resource photoProfile;
							Album photoAlbum;
							java.io.File source1 = new java.io.File(Play
									.application().path()
									+ "/test/files/homer.jpg");
							java.io.File source2 = new java.io.File(Play
									.application().path()
									+ "/test/files/profile.jpg");
							java.io.File source3 = new java.io.File(Play
									.application().path()
									+ "/test/files/profile2.jpg");
							user3 = User.searchUsername("testuser3@Test.com");

							photoAlbum = user3.createAlbum("Test My Album 1",
									"this is a album", true,
									SocialObjectType.ALBUMN);
							assertNotNull(photoAlbum);

							photoAlbum.addFile(source1, SocialObjectType.PHOTO);
							photoAlbum.addFile(source2, SocialObjectType.PHOTO);
							photoAlbum.addFile(source3, SocialObjectType.PHOTO);

							org.junit.Assert.assertEquals(
									photoAlbum.resources.size(), 3);

							Resource profilePhotoPath1 = photoAlbum.resources
									.get(0);
							Resource profilePhotoPath2 = photoAlbum.resources
									.get(1);

							File file = profilePhotoPath1.getRealFile()
									.getParentFile();
							org.junit.Assert.assertTrue(new File(file,
									"thumbnail.homer.jpg").exists());

							File f1 = profilePhotoPath2.getRealFile()
									.getParentFile();

							org.junit.Assert.assertTrue(new File(f1,
									"thumbnail.profile.jpg").exists());

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
	public void setCoverPhotoToPhotoAlbum() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Resource photoProfile, coverPhoto1 = null, coverPhoto2 = null;
						Album photoAlbum1, photoAlbum2;
						java.io.File source1 = new java.io.File(Play
								.application().path() + "/test/files/homer.jpg");
						java.io.File source2 = new java.io.File(Play
								.application().path()
								+ "/test/files/profile.jpg");
						java.io.File source3 = new java.io.File(Play
								.application().path()
								+ "/test/files/profile2.jpg");
						user3 = User.searchUsername("testuser3@Test.com");

						photoAlbum1 = user3
								.createAlbum("Test My Album 1",
										"this is a album", true,
										SocialObjectType.PHOTO);
						user2 = User.searchUsername("testuser2@Test.com");
						photoAlbum2 = user2
								.createAlbum("Test My Album 2",
										"this is a album", true,
										SocialObjectType.PHOTO);
						try {
							coverPhoto1 = photoAlbum1
									.setCoverPhoto_TOAlbum(source1);
							coverPhoto2 = photoAlbum1
									.setCoverPhoto_TOAlbum(source2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						org.junit.Assert.assertEquals(coverPhoto1.resourceName,
								"homer.jpg");
						org.junit.Assert.assertEquals(coverPhoto2.resourceName,
								"profile.jpg");
						org.junit.Assert.assertEquals(user3.album.size(), 1);

					}
				});

			}
		});

	}

	@Test
	public void getAllAlbum() {

		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						user3 = User.searchUsername("testuser3@Test.com");
						Album photoAlbum1 = user3.createAlbum(
								"Test My Album 1", "this is a album1", true,
								SocialObjectType.ALBUMN);
						assertNotNull(photoAlbum1);

					}
				});

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						user3 = User.searchUsername("testuser3@Test.com");
						Album photoAlbum2 = user3.createAlbum(
								"Test My Album 2", "this is a album2", true,
								SocialObjectType.ALBUMN);
						assertNotNull(photoAlbum2);
					}
				});

				// Asserting that duplication albumn not allowed
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						user3 = User.searchUsername("testuser3@Test.com");
						Album photoAlbum2 = user3.createAlbum(
								"Test My Album 2", "this is a album2", true,
								SocialObjectType.ALBUMN);
						assertNull(photoAlbum2);
					}
				});

				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						user3 = User.searchUsername("testuser3@Test.com");

						assertEquals(user3.album.size(), 2);
					}
				});

			}
		});
	}

	@Test
	public void userDeletePhotoToPhotoAlbum() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							Resource photoProfile, coverPhoto;
							Album Album1, Album2;
							java.io.File source1 = new java.io.File(Play
									.application().path()
									+ "/test/files/homer.jpg");
							java.io.File source2 = new java.io.File(Play
									.application().path()
									+ "/test/files/profile.jpg");
							java.io.File source3 = new java.io.File(Play
									.application().path()
									+ "/test/files/profile2.jpg");
							user1 = User.searchUsername("testuser1@Test.com");

							Album1 = user1.createAlbum("Test My Album 2",
									"this is a album", true,
									SocialObjectType.PHOTO);

							Album1.addFile(source1, SocialObjectType.PHOTO);
							Album1.addFile(source2, SocialObjectType.PHOTO);
							Album1.addFile(source3, SocialObjectType.PHOTO);

							org.junit.Assert.assertEquals(
									Album1.resources.size(), 3);
							Resource profilePhotoPath1 = Album1.resources
									.get(0);
							Resource profilePhotoPath2 = Album1.resources
									.get(1);

							File file = profilePhotoPath1.getRealFile()
									.getParentFile();
							org.junit.Assert.assertTrue(new File(file,
									"thumbnail.homer.jpg").exists());
							File f1 = profilePhotoPath2.getRealFile()
									.getParentFile();
							org.junit.Assert.assertTrue(new File(f1,
									"thumbnail.profile.jpg").exists());

							deleteFolder(file);
							FileUtils.deleteDirectory(file.getParentFile());
							assertThat(user1.album, contains(Album1));

							user2 = User.searchUsername("testuser2@Test.com");

							Album2 = user2.createAlbum("Test My Album 3",
									"this is a album", true,
									SocialObjectType.PHOTO);

							Album2.addFile(source1, SocialObjectType.PHOTO);
							Album2.addFile(source2, SocialObjectType.PHOTO);
							Album2.addFile(source3, SocialObjectType.PHOTO);

							org.junit.Assert.assertEquals(
									Album2.resources.size(), 3);
							Resource profilePhotoPath3 = Album2.resources
									.get(0);
							Resource profilePhotoPath4 = Album2.resources
									.get(1);

							File file2 = profilePhotoPath3.getRealFile()
									.getParentFile();
							org.junit.Assert.assertTrue(new File(file2,
									"thumbnail.homer.jpg").exists());

							File f2 = profilePhotoPath4.getRealFile()
									.getParentFile();
							org.junit.Assert.assertTrue(new File(f2,
									"thumbnail.profile.jpg").exists());

							deleteFolder(file2);
							FileUtils.deleteDirectory(file.getParentFile());
							assertThat(user2.album, contains(Album2));

						} catch (IOException e) { // TODO Auto-generated catch
													// block
							e.printStackTrace();
						}

					}
				});

			}
		});

	}

	public static void deleteFolder(File d) {
		File[] files = d.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				deleteFolder(files[i]);
			} else {
				files[i].delete();
			}

		}

		d.delete();
	}

	@Test
	public void userSetProfilePhotoFormPhotoAlbum() {
		running(fakeApplication(), new Runnable() {
			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						try {
							Resource photoProfile;
							Album photoAlbum;
							java.io.File source1 = new java.io.File(Play
									.application().path()
									+ "/test/files/homer.jpg");
							java.io.File source2 = new java.io.File(Play
									.application().path()
									+ "/test/files/profile.jpg");
							java.io.File source3 = new java.io.File(Play
									.application().path()
									+ "/test/files/profile2.jpg");
							user3 = User.searchUsername("testuser3@Test.com");

							photoAlbum = user3.createAlbum("Test My Album 1",
									"this is a album", true,
									SocialObjectType.PHOTO);

							photoAlbum.addFile(source1, SocialObjectType.PHOTO);
							photoAlbum.addFile(source2, SocialObjectType.PHOTO);
							photoAlbum.addFile(source3, SocialObjectType.PHOTO);

							org.junit.Assert.assertEquals(
									photoAlbum.resources.size(), 3);

							Resource profilePhotoPath = photoAlbum.resources
									.get(0);

							user1 = User.searchUsername("testuser1@Test.com");
							photoProfile = user1
									.setPhotoProfile(profilePhotoPath
											.getRealFile());

							org.junit.Assert.assertEquals(
									photoProfile.resourceName, "homer.jpg");
							user1 = User.searchUsername("testuser1@Test.com");
							org.junit.Assert.assertEquals(
									user1.albumPhotoProfile.resources.size(), 1);

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
	public void userCreatesPhotoAlbumIsExist() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
					public void invoke() {
						Album photoAlbum1, photoAlbum2;
						java.io.File source1 = new java.io.File(Play
								.application().path() + "/test/files/homer.jpg");
						java.io.File source2 = new java.io.File(Play
								.application().path() + "/test/files/homer.jpg");
						user3 = User.searchUsername("testuser3@Test.com");

						photoAlbum1 = user3
								.createAlbum("Test My Album 1",
										"this is a album", true,
										SocialObjectType.PHOTO);

						photoAlbum2 = user2
								.createAlbum("Test My Album 1",
										"this is a album", true,
										SocialObjectType.PHOTO);
						assertThat(user3.album, contains(photoAlbum1));
						assertThat(user2.album, contains(photoAlbum1));

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

							if (_so instanceof User) {

								Query q = JPA
										.em()
										.createQuery(
												"Delete Conversation  where user1 = ?1 or user2 = ?1 ");
								q.setParameter(1, _so);
								q.executeUpdate();
							}

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
