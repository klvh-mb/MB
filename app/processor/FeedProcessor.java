package processor;

import java.math.BigInteger;
import java.util.*;
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
import redis.clients.jedis.Tuple;
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
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		List<String> ids = j.lrange(USER + u.id, 0, -1);
		jedisPool.returnResource(j);
		return ids;
	}
	
	public static List<String> getUserFeedIds(User u,int offset, int pagerows) {
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
								j.del(MOMENT + communityId.longValue(), QNA + communityId.longValue());
								for(Post p: posts){
									j.zadd(MOMENT + communityId.longValue(), p.getUpdatedDate().getTime() , p.id.toString());
								}
								
								Query qnAQuery = JPA.em().createQuery("SELECT p from Post p where p.community.id = ?1 and p.postType = ?2 order by p.auditFields.createdDate desc");
								qnAQuery.setParameter(1, communityId.longValue());
								qnAQuery.setParameter(2, PostType.QUESTION);
								qnAQuery.setFirstResult(0);
								qnAQuery.setMaxResults(200);
								List<Post> qnAposts = (List<Post>)qnAQuery.getResultList();
								
								//j.del(USER + communityId.longValue());
								
								for(Post p: qnAposts){
									j.zadd(QNA + communityId.longValue(),  p.getUpdatedDate().getTime() , p.id.toString());
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
	
	public static Set<Tuple> buildPostQueueFromCommunities(List<Long> communities, int offset) {
		final Set<Tuple> post_ids = new HashSet<Tuple>();       // order of the ids does not matter

		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		for(Long c : communities) {
			post_ids.addAll(j.zrangeWithScores(MOMENT + c.toString(), 0, offset));
			post_ids.addAll(j.zrangeWithScores(QNA + c.toString(), 0, offset));
		}
		jedisPool.returnResource(j);
		return post_ids;
	}


    /**
     * TODO: Relevance targeting for Community Posts.
     * @param post_ids
     * @param userId
     */
	public static void applyRelevances(Set<Tuple> post_ids, Long userId) {
	    logger.underlyingLogger().info("[u="+userId+"] applyRelevances. numPostIds="+post_ids.size());

        String idsForIn = convertSetToString(post_ids, ",");
        Query query = JPA.em().createQuery("SELECT p from Post p where p.id in (" + idsForIn + ") order by p.auditFields.updatedDate desc");
        List<Post> postInRelevance = (List<Post>)query.getResultList();

		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		j.del(USER + userId);
		for(Post p : postInRelevance){
			j.rpush(USER + userId, p.getId().toString());   // push to tail.
		}
		jedisPool.returnResource(j);
	}

    private static String convertSetToString(Set<Tuple> tupleSet, String delim) {
        StringBuilder sb = new StringBuilder();

        String localDelim = "";
        for (Tuple tuple : tupleSet) {
            sb.append(localDelim).append(tuple.getElement());
            localDelim = delim;
        }

        return sb.toString();
    }
}
