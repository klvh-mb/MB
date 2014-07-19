package common.utils;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import models.Icon;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Condition;
import com.github.fedy2.weather.data.unit.DegreeUnit;

import common.model.TodayWeatherInfo;

/**
 * https://github.com/fedy2/yahoo-weather-java-api
 * https://developer.yahoo.com/weather/
 * 
 * http://isithackday.com/geoplanet-explorer/index.php?woeid=24865698
 * Hong Kong            24865698
 * Hong Kong Island     24703007
 * Kowloon              24703006
 * New Territories      24703004
 * 
 * @author keithlei
 *
 */
public class WeatherUtil {
    private static final play.api.Logger logger = play.api.Logger.apply(WeatherUtil.class);
    
    public static final String HONG_KONG_WOEID = "24865698";
    
    public static void fillInfo(TodayWeatherInfo info) {
        YahooWeatherService service;
        try {
            service = new YahooWeatherService();
            Channel channel = service.getForecast(HONG_KONG_WOEID, DegreeUnit.CELSIUS);
            Condition condition = channel.getItem().getCondition();
            info.setTitle(channel.getTitle());
            info.setDescription(channel.getDescription());
            info.setCondition(condition.getText());
            info.setConditionCode(condition.getCode());
            info.setIcon(getIcon(condition.getCode()));
            info.setLocation(channel.getLocation().getCity());
            info.setTemperature(condition.getTemp());
            info.setUpdatedTime(new DateTime(condition.getDate()));
        } catch (JAXBException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
    }
    
    public static String getIcon(int conditionCode) {
        Icon icon = Icon.getWeatherIcon(conditionCode);
        if (icon == null) {
            DateTime now = new DateTime();
            int hour = now.getHourOfDay();
            if (hour >= 6 && hour <= 18) {      // day time 6am - 6pm
                return "/assets/app/images/weather/weather_icons-08.png";
            }
            return "/assets/app/images/weather/weather_icons-04.png";
        }
        return icon.url;
    }
    
    public static void debug() {
        YahooWeatherService service;
        try {
            service = new YahooWeatherService();
            
            Channel channel = service.getForecast("24865698", DegreeUnit.CELSIUS);
            debug(channel);
            
            channel = service.getForecast("24703007", DegreeUnit.CELSIUS);
            debug(channel);
            
            channel = service.getForecast("24703006", DegreeUnit.CELSIUS);
            debug(channel);
            
            channel = service.getForecast("24703004", DegreeUnit.CELSIUS);
            debug(channel);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void debug(Channel channel) {
        logger.underlyingLogger().info("=======================");
        logger.underlyingLogger().info(channel.getDescription());
        logger.underlyingLogger().info(channel.getTitle());
        logger.underlyingLogger().info(channel.getLink());
        logger.underlyingLogger().info(channel.getLanguage());
        logger.underlyingLogger().info(channel.getUnits().toString());
        logger.underlyingLogger().info(channel.getAstronomy().toString());
        logger.underlyingLogger().info(channel.getAtmosphere().toString());
        logger.underlyingLogger().info(channel.getWind().toString());
        logger.underlyingLogger().info(channel.getItem().getTitle());
        logger.underlyingLogger().info(channel.getItem().getDescription());
        logger.underlyingLogger().info(channel.getItem().getCondition().toString());
        logger.underlyingLogger().info(channel.getItem().getForecasts().toString());
        logger.underlyingLogger().info("");
    }
}
