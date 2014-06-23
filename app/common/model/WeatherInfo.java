package common.model;

import java.io.Serializable;

import org.joda.time.DateTime;

public class WeatherInfo implements Serializable {
    
    private static final long serialVersionUID = -247911530124299908L;

    public final static String JEDIS_KEY = "TODAY_WEATHER";
    
    public static int REFRESH_SECS = 5 * 60;
    public static long REFRESH_MILLIS = REFRESH_SECS * 1000;
    
    private String location;
    private String temperature;
    private String humidity;
    private DateTime dateTime;
    
    private WeatherInfo() {
    }
    
    public String getLocation() {
        return location;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    @Override
    public String toString() {
        return "WeatherInfo [location=" + location + ", temperature="
                + temperature + ", humidity=" + humidity + ", dateTime="
                + dateTime + "]";
    }
    
    /**
     * TODO - keith
     * @return
     */
    public static WeatherInfo getTodayWeather() {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.location = "HONG KONG";
        weatherInfo.temperature = "20C";
        weatherInfo.humidity = "95";
        weatherInfo.dateTime = new DateTime();
        return weatherInfo;
    }
}