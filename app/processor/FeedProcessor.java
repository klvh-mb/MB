package processor;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import common.utils.NanoSecondStopWatch;
import models.Post;
import models.SocialRelation;
import models.SocialRelation.Action;
import models.User;
import org.apache.commons.lang3.StringUtils;
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
    private static play.api.Logger logger = play.api.Logger.apply(FeedProcessor.class);

    private static final int MAX_COMM_LENGTH = 200;

	private static String prefix = Play.application().configuration().getString("keyprefix", "prod_");
	private static final String USER = prefix + "user_";
    private static final String COMMUNITY = prefix + "comm_";   // single queue for community posts

	public static List<String> getUserFeedIds(User u) {
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		List<String> ids = j.lrange(USER + u.id, 0, -1);
		jedisPool.returnResource(j);
		return ids;
	}
	
	public static List<String> getUserFeedIds(User u,int offset, int pagerows) {
        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis j = null;
        List<String> ids = null;

        try {
            j = jedisPool.getResource();
            ids = j.lrange(USER + u.id, offset * pagerows, ((offset + 1)*pagerows-1));
            //List<String> ids = j.sort(USER + u.id, new SortingParams().alpha().asc());
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }

		return ids;
	}

    /**
     * Push post to community queue.
     * @param post
     */
	public static void pushToCommunity(Post post) {
        if (logger.underlyingLogger().isDebugEnabled()) {
	        logger.underlyingLogger().debug("pushToCommunity - start");
        }
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        Long commId = post.getCommunity().getId();

        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.zadd(COMMUNITY+commId, post.getUpdatedDate().getTime(), post.id.toString());

            long len = j.zcard(COMMUNITY+commId);
            if (len > MAX_COMM_LENGTH) {
                int remCount = (int) (len - MAX_COMM_LENGTH);
                Set<String> lastRecentN = j.zrange(COMMUNITY+commId, 0, -1*remCount);
                for (String key : lastRecentN) {
                    j.zrem(COMMUNITY+commId, key);
                }
            }
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }

        sw.stop();
        if (logger.underlyingLogger().isDebugEnabled()) {
	        logger.underlyingLogger().debug("pushToCommunity - end. Took "+sw.getElapsedMS()+"ms");
        }
	}

    /**
     * On system startup.
     */
	public static void updateCommunityLevelFeed() {
        if (logger.underlyingLogger().isDebugEnabled()) {
	        logger.underlyingLogger().debug("updateCommunityLevelFeed");
        }

		ActorSystem  actorSystem = Akka.system();
		 actorSystem.scheduler().scheduleOnce(
			Duration.create(0, TimeUnit.MILLISECONDS),
			new Runnable() {
				public void run() {
					JPA.withTransaction(new play.libs.F.Callback0() {
						@Override
						public void invoke() throws Throwable {
							final List<BigInteger> ids = JPA.em().createNativeQuery("SELECT id from Community").getResultList();

							JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
							Jedis j = null;
                            try {
                                j = jedisPool.getResource();
                                for (BigInteger communityId : ids) {
                                    try {
                                        Query simpleQuery = JPA.em().createQuery("SELECT p from Post p where p.community.id = ?1 order by p.auditFields.updatedDate desc");
                                        simpleQuery.setParameter(1, communityId.longValue());
                                        simpleQuery.setFirstResult(0);
                                        simpleQuery.setMaxResults(MAX_COMM_LENGTH);
                                        List<Post> posts = (List<Post>)simpleQuery.getResultList();

                                        j.del(COMMUNITY + communityId.longValue());
                                        for(Post p: posts){
                                            j.zadd(COMMUNITY + communityId.longValue(), p.getUpdatedDate().getTime(), p.id.toString());
                                        }
                                    } catch (Exception e) {
                                        logger.underlyingLogger().error("Error in updateCommunityLevelFeed", e);
                                    }
                                }
                            } finally {
                                if (j != null) {
                                    jedisPool.returnResource(j);
                                }
                            }
						}
					});
			    }
            }, actorSystem.dispatcher()
        );
	}
	
//	public static void updatesUserLevelFeed() {
//	    logger.underlyingLogger().debug("updatesUserLevelFeed");
//		ActorSystem  actorSystem = Akka.system();
//		actorSystem.scheduler().scheduleOnce(
//		        Duration.create(0, TimeUnit.MILLISECONDS),
//				new Runnable() {
//					public void run() {
//						JPA.withTransaction(new play.libs.F.Callback0() {
//							@Override
//							public void invoke() throws Throwable {
//								List<BigInteger> ids = JPA.em().createNativeQuery("SELECT id from User").getResultList();
//								JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
//								Jedis j = jedisPool.getResource();
//
//								for (BigInteger userID : ids) {
//									Query query = JPA.em().createQuery("SELECT p from Post p where p.community in (select sr.target " +
//											"from SocialRelation sr where sr.actor = ?1 and sr.action = ?2) order by p.auditFields.createdDate desc");
//									query.setParameter(1, userID.longValue());
//									query.setParameter(2, SocialRelation.Action.MEMBER);
//									query.setFirstResult(0);
//									query.setMaxResults(200);
//									List<Post> posts = (List<Post>)query.getResultList();
//
//									j.del(USER + userID.longValue());
//
//									for(Post p: posts){
//										j.rpush(USER + userID.longValue(), p.id.toString());
//									}
//								}
//								jedisPool.returnResource(j);
//							}
//						});
//
//						}
//					}, actorSystem.dispatcher()
//				);
//	}
	
	public static Set<String> buildPostQueueFromCommunities(List<Long> communities, int offset) {
		final Set<String> post_ids = new HashSet<String>();       // order of the ids does not matter

		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		for(Long c : communities) {
            post_ids.addAll(j.zrevrange(COMMUNITY + c.toString(), 0, offset));
		}
		jedisPool.returnResource(j);
		return post_ids;
	}


    /**
     * TODO: Relevance targeting for Community Posts.
     * @param post_ids
     * @param userId
     */
	public static void applyRelevances(Set<String> post_ids, Long userId) {
	    logger.underlyingLogger().info("[u="+userId+"] applyRelevances. numPostIds="+post_ids.size());

        List<Post> postInRelevance = Collections.EMPTY_LIST;

        if (post_ids.size() > 0) {
            String idsForIn = convertSetToString(post_ids, ",");
            Query query = JPA.em().createQuery("SELECT p from Post p where p.id in (" + idsForIn + ") order by p.auditFields.updatedDate desc");
            postInRelevance = (List<Post>)query.getResultList();
        }

		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = jedisPool.getResource();
		j.del(USER + userId);
		for (Post p : postInRelevance) {
			j.rpush(USER + userId, p.getId().toString());   // push to tail.
		}
		jedisPool.returnResource(j);
	}

    private static String convertSetToString(Set<String> set, String delim) {
        StringBuilder sb = new StringBuilder();

        String localDelim = "";
        for (String item : set) {
            sb.append(localDelim).append(item);
            localDelim = delim;
        }

        return sb.toString();
    }
}
