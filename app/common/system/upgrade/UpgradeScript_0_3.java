package common.system.upgrade;

import javax.persistence.Query;

import play.db.jpa.JPA;
import models.Icon;
import models.SystemVersion;
import models.Icon.IconType;

/**
 * 1) Insert more icons for community. 
 * 
 * @author keithlei
 *
 */
public class UpgradeScript_0_3 extends UpgradeScript {
    private static final play.api.Logger logger = play.api.Logger.apply(UpgradeScript_0_3.class);
    
    public UpgradeScript_0_3() {
    }
    
    @Override
    public String getVersion() {
        return "0.3";
    }
    
    @Override
    public void insertToSystemVersion() {
        SystemVersion version = new SystemVersion(
                getVersion(), 
                this.getClass().getName(), 
                "1) Insert more icons for community.");
        version.save();
    }
    
    @Override
    public boolean upgrade() throws Exception {
        logger.underlyingLogger().info("Insert more icons for community...");
        
        Query q = JPA.em().createQuery("delete from Icon i");
        q.executeUpdate();
        
        bootstrapIcon();
        
        return true;
    }
    
    private static void bootstrapIcon() {
        Query q = JPA.em().createQuery("Select count(i) from Icon i");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapIcon()");
        
        Icon icon = null;
        
        // Weather icons - https://developer.yahoo.com/weather/
        icon = new Icon("0", IconType.WEATHER, "tornado", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("1", IconType.WEATHER, "tropical storm", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("2", IconType.WEATHER, "hurricane", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("3", IconType.WEATHER, "severe thunderstorms", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("4", IconType.WEATHER, "thunderstorms", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("5", IconType.WEATHER, "mixed rain and snow", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("6", IconType.WEATHER, "mixed rain and sleet", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("7", IconType.WEATHER, "mixed snow and sleet", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("8", IconType.WEATHER, "freezing drizzle", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("9", IconType.WEATHER, "drizzle", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("10", IconType.WEATHER, "freezing rain", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("11", IconType.WEATHER, "showers", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("12", IconType.WEATHER, "showers", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("13", IconType.WEATHER, "snow flurries", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("14", IconType.WEATHER, "light snow showers", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("15", IconType.WEATHER, "blowing snow", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("16", IconType.WEATHER, "snow", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("17", IconType.WEATHER, "hail", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("18", IconType.WEATHER, "sleet", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("19", IconType.WEATHER, "dust", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("20", IconType.WEATHER, "foggy", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("21", IconType.WEATHER, "haze", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("22", IconType.WEATHER, "smoky", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("23", IconType.WEATHER, "blustery", "/assets/app/images/weather/weather_icons-09.png");
        icon.save();
        icon = new Icon("24", IconType.WEATHER, "windy", "/assets/app/images/weather/weather_icons-09.png");
        icon.save();
        icon = new Icon("25", IconType.WEATHER, "cold", "/assets/app/images/weather/weather_icons-09.png");
        icon.save();
        icon = new Icon("26", IconType.WEATHER, "cloudy", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("27", IconType.WEATHER, "mostly cloudy (night)", "/assets/app/images/weather/weather_icons-02.png");
        icon.save();
        icon = new Icon("28", IconType.WEATHER, "mostly cloudy (day)", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("29", IconType.WEATHER, "partly cloudy (night)", "/assets/app/images/weather/weather_icons-02.png");
        icon.save();
        icon = new Icon("30", IconType.WEATHER, "partly cloudy (day)", "/assets/app/images/weather/weather_icons-06.png");
        icon.save();
        icon = new Icon("31", IconType.WEATHER, "clear (night)", "/assets/app/images/weather/weather_icons-04.png");
        icon.save();
        icon = new Icon("32", IconType.WEATHER, "sunny", "/assets/app/images/weather/weather_icons-08.png");
        icon.save();
        icon = new Icon("33", IconType.WEATHER, "fair (night)", "/assets/app/images/weather/weather_icons-04.png");
        icon.save();
        icon = new Icon("34", IconType.WEATHER, "fair (day)", "/assets/app/images/weather/weather_icons-08.png");
        icon.save();
        icon = new Icon("35", IconType.WEATHER, "mixed rain and hail", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("36", IconType.WEATHER, "hot", "/assets/app/images/weather/weather_icons-08.png");
        icon.save();
        icon = new Icon("37", IconType.WEATHER, "isolated thunderstorms", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("38", IconType.WEATHER, "scattered thunderstorms", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("39", IconType.WEATHER, "scattered thunderstorms", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("40", IconType.WEATHER, "scattered showers", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("41", IconType.WEATHER, "heavy snow", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("42", IconType.WEATHER, "scattered snow showers", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("43", IconType.WEATHER, "heavy snow", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("44", IconType.WEATHER, "partly cloudy", "/assets/app/images/weather/weather_icons-01.png");
        icon.save();
        icon = new Icon("45", IconType.WEATHER, "thundershowers", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        icon = new Icon("46", IconType.WEATHER, "snow showers", "/assets/app/images/weather/weather_icons-05.png");
        icon.save();
        icon = new Icon("47", IconType.WEATHER, "isolated thundershowers", "/assets/app/images/weather/weather_icons-07.png");
        icon.save();
        
        // Community icons
        icon = new Icon("bean_orange", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_orange.png");
        icon.save();
        icon = new Icon("bean_blue", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_blue.png");
        icon.save();
        icon = new Icon("bean_green", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_green.png");
        icon.save();
        icon = new Icon("bean_red", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_red.png");
        icon.save();
        icon = new Icon("bean_yellow", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bean_yellow.png");
        icon.save();
        icon = new Icon("book", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/book.png");
        icon.save();
        icon = new Icon("gift_box", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/gift_box.png");
        icon.save();
        icon = new Icon("balloons", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/balloons.png");
        icon.save();
        icon = new Icon("camera", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/camera.png");
        icon.save();
        icon = new Icon("music_note", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/music_note.png");
        icon.save();
        icon = new Icon("plane", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/plane.png");
        icon.save();
        icon = new Icon("shopping_bag", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/shopping_bag.png");
        icon.save();
        icon = new Icon("spoon_fork", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/spoon_fork.png");
        icon.save();
        icon = new Icon("ball", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/ball.png");
        icon.save();
        icon = new Icon("boy", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/boy.png");
        icon.save();
        icon = new Icon("girl", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/girl.png");
        icon.save();
        icon = new Icon("bottle", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bottle.png");
        icon.save();
        icon = new Icon("bed", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/bed.png");
        icon.save();
        icon = new Icon("stroller", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/stroller.png");
        icon.save();
        icon = new Icon("teddy", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/teddy.png");
        icon.save();
        icon = new Icon("icecream", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/icecream.png");
        icon.save();
        icon = new Icon("sun", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/sun.png");
        icon.save();
        icon = new Icon("rainbow", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/rainbow.png");
        icon.save();
        icon = new Icon("cloud", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/cloud.png");
        icon.save();
        icon = new Icon("loc_area", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/loc_city.png");
        icon.save();
        icon = new Icon("loc_area", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/loc_area.png");
        icon.save();
        icon = new Icon("loc_district", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/loc_district.png");
        icon.save();
        icon = new Icon("zodiac_rat", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/rat.png");
        icon.save();
        icon = new Icon("zodiac_ox", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/ox.png");
        icon.save();
        icon = new Icon("zodiac_tiger", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/tiger.png");
        icon.save();
        icon = new Icon("zodiac_rabbit", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/rabbit.png");
        icon.save();
        icon = new Icon("zodiac_dragon", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/dragon.png");
        icon.save();
        icon = new Icon("zodiac_snake", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/snake.png");
        icon.save();
        icon = new Icon("zodiac_horse", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/horse.png");
        icon.save();
        icon = new Icon("zodiac_goat", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/goat.png");
        icon.save();
        icon = new Icon("zodiac_monkey", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/monkey.png");
        icon.save();
        icon = new Icon("zodiac_rooster", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/rooster.png");
        icon.save();
        icon = new Icon("zodiac_dog", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/dog.png");
        icon.save();
        icon = new Icon("zodiac_pig", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/zodiac/pig.png");
        icon.save();
    }
}