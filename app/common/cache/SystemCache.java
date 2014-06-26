package common.cache;

import common.model.WeatherInfo;
import common.serialize.JsonSerializer;

/**
 * Holds system related common objects e.g. weather
 * 
 * @author keithlei
 *
 */
public class SystemCache {

    private final static JedisCache cache = new JedisCache();
    
    private SystemCache() {
    }
    
    private static void putTodayWeather(WeatherInfo weatherInfo) {
        String json = JsonSerializer.serialize(weatherInfo);
        cache.put(WeatherInfo.JEDIS_KEY, json);
        cache.expire(WeatherInfo.JEDIS_KEY, WeatherInfo.REFRESH_SECS);
    }
    
    public static WeatherInfo getTodayWeather() {
        WeatherInfo weatherInfo = null;
        String json = cache.get(WeatherInfo.JEDIS_KEY);
        if (json == null) {      // expired, refresh
            weatherInfo = WeatherInfo.getTodayWeather();
            putTodayWeather(weatherInfo);
        } else {
            weatherInfo = (WeatherInfo)JsonSerializer.deserialize(json, WeatherInfo.class);
        }
        return weatherInfo;
    }
}
