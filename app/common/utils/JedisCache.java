package common.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.typesafe.plugin.RedisPlugin;

public class JedisCache {

    private static JedisPool jedisPool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
    
    JedisCache() {
    }
    
    public void put(String key, String value) {
        Jedis j = null;
        try {
            j = getResource();
            j.set(key, value);
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
    
    public void expire(String key, int secs) {
        Jedis j = null;
        try {
            j = getResource();
            j.expire(key, secs);
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
