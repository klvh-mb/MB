package processor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Community;
import models.Post;
import models.SocialRelation;
import models.SocialRelation.Action;
import models.User;
import play.Play;
import play.db.jpa.JPA;
import play.libs.Akka;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;

import com.typesafe.plugin.RedisPlugin;

import domain.PostType;

public class FeedProcessor {
    private static play.api.Logger logger = play.api.Logger.apply("application");
    
	private static String prefix = Play.application().configuration().getString("keyprefix", "prod_");
	private static final String USER = prefix + "user_";
	private static final String MOMENT = prefix + "moment_";
	private static final String QNA = prefix + "qna_";

	public static void pushToMemebes(Post post) {
	    logger.underlyingLogger().debug("pushToMemebes");
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<SocialRelation> q = cb.createQuery(SocialRelation.class);
		Root<SocialRelation> c = q.from(SocialRelation.class);
		q.select(c);
		q.where(cb.and(cb.equal(c.get("target"), post.community.id),
				cb.equal(c.get("action"), Action.MEMBER)));

		List<SocialRelation> result = JPA.em().createQuery(q).getResultList();
		for(SocialRelation sr : result) {
			j.lpush(USER + sr.actor,post.id.toString());
			j.ltrim(USER + sr.actor, 0, 200);
		}
		jedisPool.returnResource(j);
	}
	
	public static List<String> getUserFeedIds(User u) {
	    logger.underlyingLogger().debug("getUserFeedIds");
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		List<String> ids = j.lrange(USER + u.id, 0, -1);
		jedisPool.returnResource(j);
		return ids;
	}
	
	public static List<String> getUserFeedIds(User u,int offset, int pagerows) {
	    logger.underlyingLogger().debug("getUserFeedIds");
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		List<String> ids = j.lrange(USER + u.id, offset * pagerows, ((offset + 1)*pagerows-1));
		//List<String> ids = j.sort(USER + u.id, new SortingParams().alpha().asc());
		jedisPool.returnResource(j);
		return ids;
	}
	
	
	public static void updateCommunityLevelFeed() {
	    logger.underlyingLogger().debug("updateCommunityLevelFeed");
		ActorSystem  actorSystem = Akka.system();
		 actorSystem.scheduler().scheduleOnce(
			Duration.create(0, TimeUnit.MILLISECONDS),
			new Runnable() {
				public void run() {
					JPA.withTransaction(new play.libs.F.Callback0() {
						@Override
						public void invoke() throws Throwable {
							List<BigInteger> ids = JPA.em().createNativeQuery("SELECT id from Community").getResultList();
							JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
							Jedis j = jedisPool.getResource();
							
							for (BigInteger communityId : ids) {
								Query simpleQuery = JPA.em().createQuery("SELECT p from Post p where p.community.id = ?1 and p.postType = ?2 order by p.auditFields.createdDate desc");
								simpleQuery.setParameter(1, communityId.longValue());
								simpleQuery.setParameter(2, PostType.SIMPLE);
								simpleQuery.setFirstResult(0);
								simpleQuery.setMaxResults(200);
								List<Post> posts = (List<Post>)simpleQuery.getResultList();
								
								//j.del(USER + communityId.longValue());
								//j.del(MOMENT + communityId.longValue(), QNA + communityId.longValue());
								for(Post p: posts){
									j.zadd(MOMENT + communityId.longValue(), p.getCreatedDate().getTime() , p.id.toString());
								}
								
								Query qnAQuery = JPA.em().createQuery("SELECT p from Post p where p.community.id = ?1 and p.postType = ?2 order by p.auditFields.createdDate desc");
								qnAQuery.setParameter(1, communityId.longValue());
								qnAQuery.setParameter(2, PostType.QUESTION);
								qnAQuery.setFirstResult(0);
								qnAQuery.setMaxResults(200);
								List<Post> qnAposts = (List<Post>)qnAQuery.getResultList();
								
								//j.del(USER + communityId.longValue());
								
								for(Post p: qnAposts){
									j.zadd(QNA + communityId.longValue(),  p.getCreatedDate().getTime() , p.id.toString());
								}
							}
							jedisPool.returnResource(j);
						}
					});
						 
					}
				}, actorSystem.dispatcher()
			);
	}
	
	public static void updatesUserLevelFeed() {
	    logger.underlyingLogger().debug("updatesUserLevelFeed");
		ActorSystem  actorSystem = Akka.system();
		actorSystem.scheduler().scheduleOnce(
		        Duration.create(0, TimeUnit.MILLISECONDS),
				new Runnable() {
					public void run() {
						JPA.withTransaction(new play.libs.F.Callback0() {
							@Override
							public void invoke() throws Throwable {
								List<BigInteger> ids = JPA.em().createNativeQuery("SELECT id from User").getResultList();
								JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
								Jedis j = jedisPool.getResource();
								
								for (BigInteger userID : ids) {
									Query query = JPA.em().createQuery("SELECT p from Post p where p.community in (select sr.target " +
											"from SocialRelation sr where sr.actor = ?1 and sr.action = ?2) order by p.auditFields.createdDate desc");
									query.setParameter(1, userID.longValue());
									query.setParameter(2, SocialRelation.Action.MEMBER);
									query.setFirstResult(0);
									query.setMaxResults(200);
									List<Post> posts = (List<Post>)query.getResultList();
									
									j.del(USER + userID.longValue());
									
									for(Post p: posts){
										j.rpush(USER + userID.longValue(), p.id.toString());
									}
								}
								jedisPool.returnResource(j);
							}
						});
							 
						}
					}, actorSystem.dispatcher()
				);
	}
	
	public static List<String> buildPostQueueFromCommunities(List<Community> communities, int offset) {
	    logger.underlyingLogger().debug("buildPostQueueFromCommunities");
		List<String> post_ids = new ArrayList<String>();
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		for(Community c : communities) {
			post_ids.addAll(j.zrange(MOMENT + c.id.toString(), 0, offset));
			post_ids.addAll(j.zrange(QNA + c.id.toString(), 0, offset));
		}
		jedisPool.returnResource(j);
		return post_ids;
	}

	public static void applyRelevances(List<String> post_ids, Long userId) {
	    logger.underlyingLogger().debug("applyRelevances");
	    
		// TODO Auto-generated method stub
		// APPLY relevance Logic.
		
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		j.del(USER + userId);
		for(String pid :post_ids){
			j.rpush(USER + userId, pid);
		}
		jedisPool.returnResource(j);
	}
	
	
	
	
}
