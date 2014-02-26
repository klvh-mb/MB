import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Community;
import models.Notification;
import models.SocialAction;
import models.SocialAction.Action;
import models.SocialAction.Reaction;
import models.SocialObject;
import models.User;

import org.hibernate.event.internal.ReattachVisitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.db.jpa.JPA;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.contains;

import com.mnt.exception.SocialObjectNotJoinableException;
import com.mnt.exception.SocialObjectNotPostableException;


public class SocialTest {

	User user1  = new User("Test User 1");
	User user2  = new User("Test User 2");
	Community community1 = new Community("Test Community 1",user2);
	Community community2 = new Community("Test Community 2",user2);
	
	@Before
	public void initSetUp() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
				    public void invoke() {
				    	JPA.em().persist(user1);
				    	JPA.em().persist(user2);
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
	public void userPostCountOn_AnyCommunity_Test() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				JPA.withTransaction(new play.libs.F.Callback0() {
				    public void invoke() {
				    	 CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
				    	 CriteriaQuery<SocialAction> q = cb.createQuery(SocialAction.class);
				    	 Root<SocialAction> c = q.from(SocialAction.class);
				    	 q.select(c);
				    	 q.where(cb.and(cb.equal(c.get("actor"), user2),cb.equal(c.get("action"), SocialAction.Action.POSTED)));
				    	 List<SocialAction> result = JPA.em().createQuery(q).getResultList();
				    	 org.junit.Assert.assertEquals(result.size(), 2);
				    	 
				    	 q.where(cb.and(cb.equal(c.get("actor"), user1),cb.equal(c.get("action"), SocialAction.Action.POSTED)));
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
				    	 CriteriaQuery<SocialAction> q = cb.createQuery(SocialAction.class);
				    	 Root<SocialAction> c = q.from(SocialAction.class);
				    	 q.select(c);
				    	 q.where(cb.and(cb.equal(c.get("actor"), user1),c.get("target").in(community1.posts)));
				    	 List<SocialAction> result = JPA.em().createQuery(q).getResultList();
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
				    	 Query q = JPA.em().createQuery("SELECT n from Notification n where recipetent = ?1 and socialAction.action = ?2 ");
				    	 q.setParameter(1, user2);
				    	 q.setParameter(2, Action.JOIN_REQUESTED);
				    	 List<Notification> result = q.getResultList();
				    	 org.junit.Assert.assertEquals(result.size(), 1);
				    	 org.junit.Assert.assertEquals(result.get(0).readed, false);
				    	 
				    	 //Mark as read
				    	 user2.markNotificationRead(result.get(0));
				    	 result = q.getResultList();
				    	 org.junit.Assert.assertEquals(result.size(), 1);
				    	 org.junit.Assert.assertEquals(result.get(0).readed, true);
				    	 
				    	 
				    }
				});
				
			}
			
		});
	}
	
	@Test
	public void communityOwnerAccepts_JoinRequestOfCommunity_Test() {
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
				    	 Community community = JPA.em().find(Community.class, community1.id);
				    	 
				    	 // Assert for new Member
				    	 assertThat(community.members, contains(user1));
				    	 
				    	// Assert for reation
				    	 CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
				    	 CriteriaQuery<SocialAction> q = cb.createQuery(SocialAction.class);
				    	 Root<SocialAction> c = q.from(SocialAction.class);
				    	 q.select(c);
				    	 q.where(cb.and(cb.equal(c.get("actor"), user1),cb.equal(c.get("target"), community1)));
				    	 SocialAction socialAction = JPA.em().createQuery(q).getSingleResult();
				    	 
				    	 org.junit.Assert.assertEquals(socialAction.reaction,SocialAction.Reaction.APPROVED);
				    	 
				    	 // Assert for user accepted notification.
				    	 Query nq = JPA.em().createQuery("SELECT n from Notification n where recipetent = ?1 and socialAction.reaction = ?2 ");
				    	 nq.setParameter(1, user1);
				    	 nq.setParameter(2, Reaction.APPROVED);
				    	 Notification result = (Notification)nq.getSingleResult();
				    	 org.junit.Assert.assertNotNull(result);
				    	 org.junit.Assert.assertEquals(result.message, "You are now member of Test Community 1");
				    	 
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
				    	 
				    	 List<SocialObject> result_1 = JPA.em().createQuery("SELECT sa from SocialObject sa where name like  'Test%'").getResultList();
				    	 for(SocialObject _so : result_1) {
				    		 Query q = JPA.em().createQuery("SELECT sa from SocialAction sa where actor = ?1 or target = ?1");
				    		 q.setParameter(1, _so);
				    		 List<SocialAction> result_2 = q.getResultList();
				    		 
				    		 for (SocialAction _sa : result_2){
				    			 q = JPA.em().createQuery("Delete Notification  where socialAction = ?1 ");
					    		 q.setParameter(1, _sa);
					    		 q.executeUpdate();
				    			 JPA.em().remove(_sa);
				    		 }
				    		 
				    	 }
				    	 for(SocialObject _so : result_1) {
				    		 JPA.em().remove(_so);
				    	 }
				    }
				});
				
			}
			
		});
	}
	
	

}
