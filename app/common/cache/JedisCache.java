package common.cache;

import play.Play;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.typesafe.plugin.RedisPlugin;

public class JedisCache {
    private static final play.api.Logger logger = play.api.Logger.apply(JedisCache.class);
    private static final String SYS_PREFIX = Play.application().configuration().getString("keyprefix", "prod_");

    // All Redis Cache Key Prefix
    public static final String ARTICLE_SLIDER_PREFIX = SYS_PREFIX + "user_sc_";
    public static final String USER_POST_PREFIX = SYS_PREFIX + "user_";
    public static final String COMMUNITY_POST_PREFIX = SYS_PREFIX + "comm_";


    private static JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
    
    public enum Status {
        OK,
        ERROR
    }
    
    JedisCache() {
    }
    
    public Status put(String key, String value) {
        Jedis j = null;
        try {
            j = getResource();
            String ret = j.set(key, value);
            if (!"OK".equalsIgnoreCase(ret)) {
                logger.underlyingLogger().error(ret);
                return Status.ERROR;
            }
            return Status.OK;
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    public String get(String key) {
        Jedis j = null;
        try {
            j = getResource();
            if (!j.exists(key)) {
                return null;
            }
            String value = j.get(key);
            if ("".equals(value.trim())) {
                j.del(key);     // del key with invalid value
                return null;
            }
            return value;
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    public boolean exists(String key) {
        Jedis j = null;
        try {
            j = getResource();
            return j.exists(key);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    public long remove(String key) {
        Jedis j = null;
        try {
            j = getResource();
            return j.del(key);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    public long expire(String key, int secs) {
        Jedis j = null;
        try {
            j = getResource();
            return j.expire(key, secs);
        } finally {
            if (j != null)
                returnResource(j);
        }
    }
    
    private Jedis getResource() {
        return jedisPool.getResource();
    }
    
    private void returnResource(Jedis j) {
        jedisPool.returnResource(j);
    }
}
