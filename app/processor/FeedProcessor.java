package processor;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.Query;

import common.cache.JedisCache;
import common.utils.NanoSecondStopWatch;
import domain.PostType;
import models.Community;
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

/**
 * Processor for NewsFeed.
 */
public class FeedProcessor {
    private static play.api.Logger logger = play.api.Logger.apply(FeedProcessor.class);

    private static final int MAX_COMM_QUEUE_LENGTH = 200;
    private static final int ttlSecs = 6 * 60 * 60;     // 6 hours

    // List (User social feed)
	private static final String SOCIAL_FEED_KEY = JedisCache.SOCIAL_FEED_PREFIX;
    // List (User business feed)
    private static final String BIZ_FEED_KEY = JedisCache.BIZ_FEED_PREFIX;
    // SortedSet (community post queue)
    private static final String COMMUNITY = JedisCache.COMMUNITY_POST_PREFIX;

    /**
     * @param userId
     * @param postIds
     */
    public static void refreshUserCommunityFeed(Long userId, List<String> postIds) {
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.del(SOCIAL_FEED_KEY +userId);                 // delete previous list

            for (String postId : postIds) {
                j.rpush(SOCIAL_FEED_KEY +userId, postId);   // push to list tail.
            }

            // mark TTL to cleanup for inactive accounts
            j.expire(SOCIAL_FEED_KEY +userId, ttlSecs);
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }
	}

    /**
     * @param userId
     * @param postIds
     */
    public static void refreshBusinessCommunityFeed(Long userId, List<String> postIds) {
		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.del(BIZ_FEED_KEY +userId);                 // delete previous list

            for (String postId : postIds) {
                j.rpush(BIZ_FEED_KEY +userId, postId);   // push to list tail.
            }

            // mark TTL to cleanup for inactive accounts
            j.expire(BIZ_FEED_KEY +userId, ttlSecs);
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
            // fetch front list entries
            ids = j.lrange(SOCIAL_FEED_KEY + u.id, offset * pagerows, ((offset + 1)*pagerows-1));
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
        }
		return ids;
	}

    /**
     * For homepage display.
     * @param u
     * @param offset
     * @param pagerows
     * @return
     */
	public static List<String> getBusinessFeedIds(User u, int offset, int pagerows) {
        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis j = null;
        List<String> ids = null;
        try {
            j = jedisPool.getResource();
            // fetch front list entries
            ids = j.lrange(BIZ_FEED_KEY + u.id, offset * pagerows, ((offset + 1)*pagerows-1));
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
		    ids = j.lrange(SOCIAL_FEED_KEY + u.id, 0, -1);
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
	        logger.underlyingLogger().debug("pushToCommunity(p="+post.getId()+" c="+commId+")");
        }

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
	}

	/**
     * Remove post from community queue.
     * @param post
     */
    public static void removeFromCommunity(Post post) {
        Long commId = post.getCommunity().getId();
        
        if (logger.underlyingLogger().isDebugEnabled()) {
            logger.underlyingLogger().debug("removeFromCommunity(p="+post.getId()+" c="+commId+")");
        }

        JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		Jedis j = null;
        try {
            j = jedisPool.getResource();
            j.zrem(COMMUNITY+commId, post.id.toString());   // remove from community sorted set
        } finally {
            if (j != null) {
		        jedisPool.returnResource(j);
            }
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
     * On system startup. Bootstrap different community NF queues.
     */
	public static void bootstrapCommunityLevelFeed() {
		ActorSystem actorSystem = Akka.system();
		 actorSystem.scheduler().scheduleOnce(
			Duration.create(0, TimeUnit.MILLISECONDS),
			new Runnable() {
				public void run() {
					JPA.withTransaction(new play.libs.F.Callback0() {
						@Override
						public void invoke() throws Throwable {
                            NanoSecondStopWatch sw = new NanoSecondStopWatch();

                            final List<Object[]> commEntries = JPA.em().createNativeQuery("SELECT id, deleted, excludeFromNewsfeed, communityType from Community").getResultList();

                            logger.underlyingLogger().info("bootstrapCommunityLevelFeed - start. Total communities: "+commEntries.size());

                            int numActive=0, numDeleted=0, numExcludeNF=0;

                            JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
							Jedis j = null;
                            try {
                                j = jedisPool.getResource();
                                for (Object[] commEntry : commEntries) {
                                    try {
                                        BigInteger communityId = (BigInteger) commEntry[0];
                                        Boolean deleted = (Boolean) commEntry[1];
                                        Boolean excludeFromNewsfeed = (Boolean) commEntry[2];   // community level exclude NF
                                        Integer commTypeInt = (Integer) commEntry[3];

                                        // purge old queue from Redis
                                        j.del(COMMUNITY+communityId.longValue());

                                        if (deleted) {
                                            numDeleted++;
                                        } else if (excludeFromNewsfeed) {
                                            numExcludeNF++;
                                        } else {
                                            String queryStr;
                                            // create queue and populate posts (un-deleted) if active
                                            if (commTypeInt != null && Community.CommunityType.BUSINESS.ordinal() == commTypeInt) {
                                                queryStr = "SELECT p from Post p where p.community.id=?1 and p.deleted=0 order by p.socialUpdatedDate desc";
                                            } else {
                                                int quesType = PostType.QUESTION.ordinal();
                                                queryStr = "SELECT p from Post p where p.community.id=?1 and p.deleted=0 and p.postType="+quesType+" order by p.socialUpdatedDate desc";
                                            }
                                            Query simpleQuery = JPA.em().createQuery(queryStr);
                                            simpleQuery.setParameter(1, communityId.longValue());
                                            simpleQuery.setFirstResult(0);
                                            simpleQuery.setMaxResults(MAX_COMM_QUEUE_LENGTH);

                                            List<Post> posts = (List<Post>)simpleQuery.getResultList();
                                            for (Post p: posts){
                                                j.zadd(COMMUNITY+communityId.longValue(), p.getSocialUpdatedDate().getTime(), p.id.toString());
                                            }
                                            numActive++;
                                        }
                                    } catch (Exception e) {
                                        logger.underlyingLogger().error("Error in bootstrapCommunityLevelFeed", e);
                                    }
                                }
                            } finally {
                                if (j != null) {
                                    jedisPool.returnResource(j);
                                }
                            }

                            sw.stop();
                            logger.underlyingLogger().info("bootstrapCommunityLevelFeed - end. Took "+sw.getElapsedMS()+
                                    "ms. NumActive="+numActive+", NumDeleted="+numDeleted+", NumExcludeNF="+numExcludeNF);
						}
					});
			    }
            }, actorSystem.dispatcher()
        );
	}
}
