import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import models.Announcement;
import models.ArticleCategory;
import models.Community;
import models.Community.CommunityType;
import models.EmotIcons;
import models.Icon;
import models.Icon.IconType;
import models.Location;
import models.Location.LocationCode;
import models.SecurityRole;
import models.TargetingSocialObject;
import models.User;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import play.db.jpa.JPA;
import providers.MyUsernamePasswordAuthUser;
import common.model.TargetYear;
import controllers.Application;

import providers.MyUsernamePasswordAuthProvider.MySignup;

public class DataBootstrap {
    private static final play.api.Logger logger = play.api.Logger.apply(DataBootstrap.class);
    
    public static void bootstrap() {
        bootstrapAnnouncement();
        bootstrapIcon();
        bootstrapEmotIcon();
        bootstrapArticleCategory();
        bootstrapUser();
        bootstrapLocation();
        bootstrapCommunity();
	}
    
    private static void bootstrapEmotIcon() {
        Query q = JPA.em().createQuery("Select count(i) from EmotIcons i");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapEmotIcon()");
        
        EmotIcons emotIcons = new EmotIcons(":)", "<img class=\"emoticon\"  src=\"/assets/app/images/emoticons/smile.png\">");
        emotIcons.save();
        emotIcons = new EmotIcons(":(", "<img class=\"emoticon\"  src=\"/assets/app/images/emoticons/frown.png\">");
        emotIcons.save();
        emotIcons = new EmotIcons(":P", "<img class=\"emoticon\"  src=\"/assets/app/images/emoticons/tongue.png\">");
        emotIcons.save();
        emotIcons = new EmotIcons(":O", "<img class=\"emoticon\"  src=\"/assets/app/images/emoticons/gasp.png\">");
        emotIcons.save();
        emotIcons = new EmotIcons(":D", "<img class=\"emoticon\"  src=\"/assets/app/images/emoticons/grin.png\">");
        emotIcons.save();
        emotIcons = new EmotIcons(";)", "<img class=\"emoticon\"  src=\"/assets/app/images/emoticons/wink.png\">");
        emotIcons.save();
	}

	private static void bootstrapAnnouncement() {
        Query q = JPA.em().createQuery("Select count(a) from Announcement a");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapAnnouncement()");
        
        Announcement announcement = 
                new Announcement(
                        "歡迎來到 miniBean 小萌豆! 係呢度您地會搵到最啱傾嘅媽媽爸爸社群! 請開心分享!", 
                        new DateTime(2015,12,31,0,0).toDate());
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
        
        superAdmin.roles = Collections.singletonList(SecurityRole
                .findByRoleName(controllers.Application.SUPER_ADMIN_ROLE));
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
    
    private static Community createFeedbackCommunity(String name, String desc) {
        Community community = null;
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/community/beans.png");
            community.system = true;
            community.excludeFromNewsfeed = true;
            community.targetingType = TargetingSocialObject.TargetingType.ALL_USERS;
            //community.setCoverPhoto(new File(Resource.STORAGE_PATH + "/default/beans_cover.jpg"));
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }
    
    private static Community createZodiacCommunity(String name, String desc, int year) {
        Community community = null;
        TargetYear targetYear = TargetYear.valueOf(year);
        String zodiac = targetYear.getZodiac().name();
        String targetingInfo = targetYear.toString();
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/zodiac/" + zodiac.toLowerCase() + ".png");
            community.system = true;
            community.targetingType = TargetingSocialObject.TargetingType.ZODIAC_YEAR;
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
            community.targetingType = TargetingSocialObject.TargetingType.LOCATION_DISTRICT;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(file);
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        return community;
    }
}