package processor;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;

import common.cache.JedisCache;
import common.utils.NanoSecondStopWatch;
import models.Post;
import models.User;
import play.db.jpa.JPA;
import play.libs.Akka;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import scala.concurrent.duration.Duration;
import akka.actor.ActorSystem;

import com.typesafe.plugin.RedisPlugin;

public class FeedProcessor {
    private static play.api.Logger logger = play.api.Logger.apply(FeedProcessor.class);

    private static final int MAX_COMM_QUEUE_LENGTH = 200;

	private static final String USER = JedisCache.USER_POST_PREFIX;
    private static final String COMMUNITY = JedisCache.COMMUNITY_POST_PREFIX;   // single queue for community posts

    /**
     * @param userId
     * @param postIds
     */
    public static void refreshUserCommunityFeed(Long userId, List<String> postIds) {
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.del(USER+userId);

            for (String postId : postIds) {
                j.rpush(USER+userId, postId);   // push to tail.
            }
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }
	}

    /**
     * For homepage display.
     * @param u
     * @param offset
     * @param pagerows
     * @return
     */
	public static List<String> getUserFeedIds(User u,int offset, int pagerows) {
        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis j = null;
        List<String> ids = null;
        try {
            j = jedisPool.getResource();
            ids = j.lrange(USER + u.id, offset * pagerows, ((offset + 1)*pagerows-1));
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }
		return ids;
	}

    // For debugging
    public static List<String> getUserFeedIds(User u) {
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        List<String> ids = null;
        try {
            j = jedisPool.getResource();
		    ids = j.lrange(USER + u.id, 0, -1);
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
        if (post.getCommunity().isExcludeFromNewsfeed()) {
            return;     // NF disable
        }

        Long commId = post.getCommunity().getId();

        if (logger.underlyingLogger().isDebugEnabled()) {
	        logger.underlyingLogger().debug("pushToCommunity(p="+post.getId()+" c="+commId+") - start");
        }
        NanoSecondStopWatch sw = new NanoSecondStopWatch();

        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.zadd(COMMUNITY+commId, post.getSocialUpdatedDate().getTime(), post.id.toString());

            long len = j.zcard(COMMUNITY+commId);
            if (len > MAX_COMM_QUEUE_LENGTH) {
                int remCount = (int) (len - MAX_COMM_QUEUE_LENGTH);
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
     * @param communityId
     * @param maxCount
     * @return
     */
    public static LinkedList<Tuple> getCommunityMostRecentPosts(Long communityId, int maxCount) {
        final LinkedList<Tuple> posts = new LinkedList<>();

        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            if (j.exists(COMMUNITY+communityId)) {
                posts.addAll(j.zrevrangeWithScores(COMMUNITY+communityId, 0, maxCount));
            }
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }
        return posts;
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
							final List<BigInteger> ids = JPA.em().createNativeQuery("SELECT id from Community where excludeFromNewsfeed = 0").getResultList();

							JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
							Jedis j = null;
                            try {
                                j = jedisPool.getResource();
                                for (BigInteger communityId : ids) {
                                    try {
                                        Query simpleQuery = JPA.em().createQuery("SELECT p from Post p where p.community.id = ?1 order by p.auditFields.updatedDate desc");
                                        simpleQuery.setParameter(1, communityId.longValue());
                                        simpleQuery.setFirstResult(0);
                                        simpleQuery.setMaxResults(MAX_COMM_QUEUE_LENGTH);
                                        List<Post> posts = (List<Post>)simpleQuery.getResultList();

                                        j.del(COMMUNITY+communityId.longValue());
                                        for(Post p: posts){
                                            j.zadd(COMMUNITY+communityId.longValue(), p.getSocialUpdatedDate().getTime(), p.id.toString());
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
}
