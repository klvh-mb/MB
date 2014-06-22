package targeting.community;

import com.typesafe.plugin.RedisPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.HashSet;
import java.util.Set;

import static targeting.community.NewsfeedCommWeightDistributor.DistributionResult;

/**
 * Created by IntelliJ IDEA.
 * Date: 22/6/14
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewsfeedCommTargetingEngine {

    public static void indexCommNewsfeedForUser(Long userId) {
        DistributionResult distributionResult = NewsfeedCommWeightDistributor.process(userId);

//		JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
//		Jedis j = jedisPool.getResource();
//		for(Long c : communities) {
//            post_ids.addAll(j.zrangeWithScores(COMMUNITY + c.toString(), 0, offset));
//		}
//		jedisPool.returnResource(j);
	}
}
