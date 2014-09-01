import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import models.*;
import models.Announcement.AnnouncementType;
import models.Community.CommunityType;
import models.Icon.IconType;
import models.Location.LocationCode;
import models.TargetingSocialObject.TargetingType;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import play.db.jpa.JPA;
import providers.MyUsernamePasswordAuthUser;
import common.model.TargetYear;
import common.model.TodayWeatherInfo;
import controllers.Application;
import providers.MyUsernamePasswordAuthProvider.MySignup;

public class DataBootstrap {
    private static final play.api.Logger logger = play.api.Logger.apply(DataBootstrap.class);
    
    public static void bootstrap() {
        bootstrapAnnouncement();
        bootstrapIcon();
        bootstrapEmoticon();
        bootstrapArticleCategory();
        bootstrapCommunityCategory();
        bootstrapUser();
        bootstrapLocation();
        bootstrapCommunity();
        bootstrapPNCommunity();

        // clear cache
        clearCache();
        
        /*
        TodayWeatherInfo info = TodayWeatherInfo.getInfo();
        logger.underlyingLogger().info(info.toString());
        
        WeatherUtil.debug();
        */
	}
    
    private static void clearCache() {
        TodayWeatherInfo.clearInfo();
    }
    
    private static void bootstrapEmoticon() {
        Query q = JPA.em().createQuery("Select count(i) from Emoticon i");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapEmoticon()");
        
        Emoticon emoticon = null; 
        emoticon = new Emoticon("angel", "O:)", 6, "/assets/app/images/emoticons/angel.png");
        emoticon.save();
        emoticon = new Emoticon("bad", "X-(", 12, "/assets/app/images/emoticons/bad.png");
        emoticon.save();
        emoticon = new Emoticon("blush", "^_^", 4, "/assets/app/images/emoticons/blush.png");
        emoticon.save();
        emoticon = new Emoticon("cool", "B)", 9, "/assets/app/images/emoticons/cool.png");
        emoticon.save();
        emoticon = new Emoticon("cry", ":'(", 11, "/assets/app/images/emoticons/cry.png");
        emoticon.save();
        emoticon = new Emoticon("dry", ":_", 15, "/assets/app/images/emoticons/dry.png");
        emoticon.save();
        emoticon = new Emoticon("frown", ":(", 10, "/assets/app/images/emoticons/frown.png");
        emoticon.save();
        emoticon = new Emoticon("gasp", ":O", 19, "/assets/app/images/emoticons/gasp.png");
        emoticon.save();
        emoticon = new Emoticon("grin", ":D", 3, "/assets/app/images/emoticons/grin.png");
        emoticon.save();
        //emoticon = new Emoticon("happy", "^^D", "/assets/app/images/emoticons/happy.png");
        //emoticon.save();
        emoticon = new Emoticon("huh", "O_o", 17, "/assets/app/images/emoticons/huh.png");
        emoticon.save();
        emoticon = new Emoticon("laugh", "XD", 16, "/assets/app/images/emoticons/laugh.png");
        emoticon.save();
        emoticon = new Emoticon("love", "**)", 5, "/assets/app/images/emoticons/love.png");
        emoticon.save();
        emoticon = new Emoticon("mad", "X(", 13, "/assets/app/images/emoticons/mad.png");
        emoticon.save();
        emoticon = new Emoticon("ohmy", ";O", 14, "/assets/app/images/emoticons/ohmy.png");
        emoticon.save();
        emoticon = new Emoticon("ok", ":|", 20, "/assets/app/images/emoticons/ok.png");
        emoticon.save();
        emoticon = new Emoticon("smile", ":)", 1, "/assets/app/images/emoticons/smile.png");
        emoticon.save();
        emoticon = new Emoticon("teat", ":+O", 21, "/assets/app/images/emoticons/teat.png");
        emoticon.save();
        emoticon = new Emoticon("teeth", "^^]", 8, "/assets/app/images/emoticons/teeth.png");
        emoticon.save();
        emoticon = new Emoticon("tongue", ":p", 7, "/assets/app/images/emoticons/tongue.png");
        emoticon.save();
        emoticon = new Emoticon("wacko", ":S", 18, "/assets/app/images/emoticons/wacko.png");
        emoticon.save();
        emoticon = new Emoticon("wink", ";)", 2, "/assets/app/images/emoticons/wink.png");
        emoticon.save();
	}

	private static void bootstrapAnnouncement() {
        Query q = JPA.em().createQuery("Select count(a) from Announcement a");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapAnnouncement()");
        
        // General
        Announcement announcement = 
                new Announcement(
                        "歡迎來到 miniBean小萌豆！喺呢度您地會搵到最啱傾嘅媽媽爸爸社群。 請開心分享！", 
                        new DateTime(2015,12,31,0,0).toDate());
        announcement.save();
        announcement = 
                new Announcement(
                        "我地有手機版啦！立即用手機登入 minibean.com.hk 試下啦", 
                        new DateTime(2015,12,31,0,0).toDate());
        announcement.save();
        
        // Top info
        announcement = 
                new Announcement(
                        "小萌豆為所有龍媽媽蛇媽媽編制了2015-2016嘅幼兒班申請資訊。<br>請立即到 PN討論區 査看啦<br>" + 
                        "<span style='margin-left:25px;width:40%;display:inline-block;'><a href='#/community/49/question'>港島PN討論區</a></span>" + 
                        "<span style='margin-left:25px;width:40%;display:inline-block;'><a href='#/community/50/question'>九龍PN討論區</a></span>" + 
                        "<span style='margin-left:25px;width:40%;display:inline-block;'><a href='#/community/51/question'>新界PN討論區</a></span>" +
                        "<span style='margin-left:25px;width:40%;display:inline-block;'><a href='#/community/53/question'>離島PN討論區</a></span>",
                        AnnouncementType.TOP_INFO, 
                        new DateTime(2014,12,31,0,0).toDate());
        announcement.save();
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
        icon = new Icon("29", IconType.WEATHER, "partly cloudy (night)", "/assets/app/images/weather/weather_icons-10.png");
        icon.save();
        icon = new Icon("30", IconType.WEATHER, "partly cloudy (day)", "/assets/app/images/weather/weather_icons-11.png");
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
        icon = new Icon("cat", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/cat.png");
        icon.save();
        icon = new Icon("helmet", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/helmet.png");
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
    
    private static void bootstrapArticleCategory() {
        Query q = JPA.em().createQuery("Select count(ac) from ArticleCategory ac");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapArticleCategory()");
        
        final String[] cats = new String[] {
                "孕婦須知", "育兒通識", "親子活動", "教育與學校", "秀身扮靚", "有趣分享"
        };

        ArticleCategory category = new ArticleCategory(cats[0], cats[0], "/assets/app/images/article/cat_1.jpg");
        category.save();
        category = new ArticleCategory(cats[1], cats[1], "/assets/app/images/article/cat_2.jpg");
        category.save();
        category = new ArticleCategory(cats[2], cats[2], "/assets/app/images/article/cat_3.jpg");
        category.save();
        category = new ArticleCategory(cats[3], cats[3], "/assets/app/images/article/cat_4.jpg");
        category.save();
        category = new ArticleCategory(cats[4], cats[4], "/assets/app/images/article/cat_5.jpg");
        category.save();
        category = new ArticleCategory(cats[5], cats[5], "/assets/app/images/article/cat_6.jpg");
        category.save();
    }

    private static void bootstrapCommunityCategory() {
        Query q = JPA.em().createQuery("Select count(cc) from CommunityCategory cc");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapCommunityCategory()");

        final String[] cats = new String[] {
                "最新動向", "熱門產品", "親子好去處", "送給您的小萌豆"
        };

        CommunityCategory category = new CommunityCategory(cats[0]);
        category.save();
        category = new CommunityCategory(cats[1]);
        category.save();
        category = new CommunityCategory(cats[2]);
        category.save();
        category = new CommunityCategory(cats[3]);
        category.save();
    }
    
    private static void bootstrapUser() {
        Query q = JPA.em().createQuery("Select count(u) from User u where system = true");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapUser()");
        
        // signup info for super admin
        MySignup signup = new MySignup();
        signup.email = "minibean.hk@gmail.com";
        signup.fname = "miniBean";
        signup.lname = "HK";
        signup.password = "m1n1Bean";
        signup.repeatPassword = "m1n1Bean";
        
        MyUsernamePasswordAuthUser authUser = new MyUsernamePasswordAuthUser(signup);
        User superAdmin = User.create(authUser);
        
        superAdmin.roles = Collections.singletonList(
                SecurityRole.findByRoleName(SecurityRole.RoleType.SUPER_ADMIN.name()));
        superAdmin.emailValidated = true;
        superAdmin.newUser = false;
        superAdmin.system = true;
        superAdmin.save();
        
        /*
        try {
            superAdmin.setPhotoProfile(new File(Resource.STORAGE_PATH + "/default/logo/logo-mB-1.png"));
        } catch (IOException e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        */
    }
    
    private static void bootstrapLocation() {
        Query q = JPA.em().createQuery("Select count(l) from Location l");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapLocation()");
        
        Location countryHK = new Location(LocationCode.HK, "香港", "全香港");    // country
        JPA.em().persist(countryHK);
        Location stateHK = new Location(countryHK, "香港", "全香港");            // state
        JPA.em().persist(stateHK);
        Location cityHK = new Location(stateHK, "香港", "全香港");               // city
        JPA.em().persist(cityHK);
        
        Location hkIsland = new Location(cityHK, "香港島");    // region
        JPA.em().persist(hkIsland);
        Location d1 = new Location(hkIsland, "中西區");        // district
        JPA.em().persist(d1);
        Location d2 = new Location(hkIsland, "東區");
        JPA.em().persist(d2);
        Location d3 = new Location(hkIsland, "南區");
        JPA.em().persist(d3);
        Location d4 = new Location(hkIsland, "灣仔區");
        JPA.em().persist(d4);
        
        Location kowloon = new Location(cityHK, "九龍");      // region
        JPA.em().persist(kowloon);
        Location d5 = new Location(kowloon, "九龍城區");       // district
        JPA.em().persist(d5);
        Location d6 = new Location(kowloon, "觀塘區");
        JPA.em().persist(d6);
        Location d8 = new Location(kowloon, "深水埗區");
        JPA.em().persist(d8);
        Location d9 = new Location(kowloon, "黃大仙區");
        JPA.em().persist(d9);
        Location d10 = new Location(kowloon, "油尖旺區");
        JPA.em().persist(d10);
        
        Location newTerritories = new Location(cityHK, "新界");   // region
        JPA.em().persist(newTerritories);
        Location d7 = new Location(newTerritories, "西貢區");  // district
        JPA.em().persist(d7);
        Location d11 = new Location(newTerritories, "北區");
        JPA.em().persist(d11);
        Location d12 = new Location(newTerritories, "沙田區");
        JPA.em().persist(d12);
        Location d13 = new Location(newTerritories, "大埔區");
        JPA.em().persist(d13);
        Location d14 = new Location(newTerritories, "葵青區");
        JPA.em().persist(d14);
        Location d15 = new Location(newTerritories, "荃灣區");
        JPA.em().persist(d15);
        Location d16 = new Location(newTerritories, "屯門區");
        JPA.em().persist(d16);
        Location d17 = new Location(newTerritories, "元朗區");
        JPA.em().persist(d17);
        
        Location islands = new Location(cityHK, "離島");      // region
        JPA.em().persist(islands);
        Location d18 = new Location(islands, "離島區");        // district
        JPA.em().persist(d18);
    }
    
    private static void bootstrapCommunity() {
        Query q = JPA.em().createQuery("Select count(c) from Community c where system = true");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapCommunity()");
        
        // Feedback community
        String name = "miniBean小萌豆意見區";
        String desc = "miniBean小萌豆意見區";
        createFeedbackCommunity(name, desc);
        
        // Targeting community
        
        // SOON_MOMS_DADS
        name = "準媽媽準爸爸♥";
        desc = "準媽媽準爸爸♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/boy.png",
                TargetingType.SOON_MOMS_DADS,
                "SOON_MOMS_DADS");
        
        // NEW_MOMS_DADS
        name = "新手媽媽爸爸♥";
        desc = "新手媽媽爸爸♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/boy.png",
                TargetingType.NEW_MOMS_DADS,
                "NEW_MOMS_DADS");
        
        // ALL_MOMS_DADS
        name = "親子好去處♥";
        desc = "親子好去處♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/stroller.png",
                TargetingType.ALL_MOMS_DADS,
                "BABY_FRIENDLY_PLACES");
        
        // PUBLIC
        name = "小寶寶去旅行♥";
        desc = "小寶寶去旅行♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/plane.png",
                TargetingType.PUBLIC,
                "TRAVEL");
        
        name = "寵物好朋友♥";
        desc = "寵物好朋友♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/cat.png",
                TargetingType.PUBLIC,
                "PETS");
        
        name = "安全知多D♥";
        desc = "安全知多D♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/helmet.png",
                TargetingType.PUBLIC,
                "BABY_SAFETY");
        
        // Zodiac communities
        
        // rat
        name = "鼠年媽媽會♥2008";
        desc = "鼠年媽媽會♥2008";
        createZodiacCommunity(name, desc, 2008);
        
        // ox
        name = "牛年媽媽會♥2009";
        desc = "牛年媽媽會♥2009";
        createZodiacCommunity(name, desc, 2009);

        // tiger
        name = "虎年媽媽會♥2010";
        desc = "虎年媽媽會♥2010";
        createZodiacCommunity(name, desc, 2010);
        
        // rabbit
        name = "兔年媽媽會♥2011";
        desc = "兔年媽媽會♥2011";
        createZodiacCommunity(name, desc, 2011);
        
        // dragon
        name = "龍年媽媽會♥2012";
        desc = "龍年媽媽會♥2012";
        createZodiacCommunity(name, desc, 2012);
        
        // snake
        name = "蛇年媽媽會♥2013";
        desc = "蛇年媽媽會♥2013";
        createZodiacCommunity(name, desc, 2013);

        // horse
        name = "馬年媽媽會♥2014";
        desc = "馬年媽媽會♥2014";
        createZodiacCommunity(name, desc, 2014);
        
        // goat
        name = "羊年媽媽會♥2015";
        desc = "羊年媽媽會♥2015";
        createZodiacCommunity(name, desc, 2015);
        
        // monkey
        name = "猴年媽媽會♥2016";
        desc = "猴年媽媽會♥2016";
        createZodiacCommunity(name, desc, 2016);
        
        // rooster
        name = "鷄年媽媽會♥2017";
        desc = "鷄年媽媽會♥2017";
        createZodiacCommunity(name, desc, 2017);

        // dog
        name = "狗年媽媽會♥2018";
        desc = "狗年媽媽會♥2018";
        createZodiacCommunity(name, desc, 2018);
        
        // pig
        name = "猪年媽媽會♥2019";
        desc = "猪年媽媽會♥2019";
        createZodiacCommunity(name, desc, 2019);
        
        // District communities
        List<Location> districts = Location.getHongKongDistricts();
        for (Location district : districts) {
            name = district.displayName + "媽媽會♥";
            desc = district.displayName + "媽媽會♥";
            createLocationCommunity(name, desc, district);
        }
    }

    private static void bootstrapPNCommunity() {
        Query q = JPA.em().createQuery("Select count(c) from Community c where c.targetingType = ?1 and c.system = true");
        q.setParameter(1, TargetingType.PRE_NURSERY);
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapPNCommunity()");

        // PN Region communities
        List<Location> regions = Location.getHongKongRegions();
        for (Location region : regions) {
            String name = region.displayName + "PN討論區2015-16";
            String desc = region.displayName + "PreNursery討論區 2015-2016";
            createPNCommunity(name, desc, region);
        }
    }

    private static Community createFeedbackCommunity(String name, String desc) {
        Community community = null;
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/community/beans.png");
            community.system = true;
            community.excludeFromNewsfeed = true;
            community.targetingType = TargetingType.ALL_MOMS_DADS;
            community.targetingInfo = "FEEDBACK";
            //community.setCoverPhoto(new File(Resource.STORAGE_PATH + "/default/beans_cover.jpg"));
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }

    private static Community createTargetingCommunity(String name, String desc, 
            String icon, TargetingType targetingType, String targetingInfo) {
        Community community = null;
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, icon);
            community.system = true;
            community.excludeFromNewsfeed = false;
            community.targetingType = targetingType;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(new File(Resource.STORAGE_PATH + "/default/beans_cover.jpg"));
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }
    
    private static Community createZodiacCommunity(String name, String desc, int year) {
        Community community = null;
        TargetYear targetYear = TargetYear.valueOf(new DateTime(year, 4, 1, 0, 0)); // april must be in the zodiac year already
        String zodiac = targetYear.getZodiac().name();
        String targetingInfo = targetYear.getZodiacInfo();
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/zodiac/" + zodiac.toLowerCase() + ".png");
            community.system = true;
            community.targetingType = TargetingType.ZODIAC_YEAR;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(file);
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }
    
    private static Community createLocationCommunity(String name, String desc, Location location) {
        Community community = null;
        String targetingInfo = location.id.toString();
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/community/loc_" + location.locationType.name().toLowerCase() + ".png");
            community.system = true;
            community.targetingType = TargetingType.LOCATION_DISTRICT;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(file);
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }

    private static Community createPNCommunity(String name, String desc, Location region) {
                Community community = null;
        String targetingInfo = region.id.toString();
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN,
                    "/assets/app/images/general/icons/community/grad_hat.png");
            community.system = true;
            community.targetingType = TargetingType.PRE_NURSERY;
            community.targetingInfo = targetingInfo;
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in createPNCommunity", e);
        }
        return community;
    }
}