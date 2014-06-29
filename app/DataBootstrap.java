import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.joda.time.DateTime;

import com.mnt.exception.SocialObjectNotJoinableException;

import common.model.TargetYear;
import play.db.jpa.JPA;
import models.Announcement;
import models.ArticleCategory;
import models.Community;
import models.Icon;
import models.Icon.IconType;
import models.Location;
import models.SecurityRole;
import models.TargetingSocialObject;
import models.User;
import models.Community.CommunityType;
import models.Location.LocationCode;

public class DataBootstrap {
    
    public static void bootstrap() {
        bootstrapAnnouncement();
        bootstrapIcon();
        bootstrapArticleCategory();
        bootstrapUser();
        bootstrapLocation();
        bootstrapCommunity();
	}
    
    private static void bootstrapAnnouncement() {
        Query q = JPA.em().createQuery("Select count(a) from Announcement a");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
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
        
        Icon icon = new Icon("tag_1", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/tag_1.png");
        icon.save();
        icon = new Icon("tag_2", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/tag_2.png");
        icon.save();
        icon = new Icon("tag_3", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/tag_3.png");
        icon.save();
        icon = new Icon("tag_4", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/tag_4.png");
        icon.save();
        icon = new Icon("loc_district", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/loc_district.png");
        icon.save();
        icon = new Icon("loc_area", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/loc_area.png");
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
        
        ArticleCategory category = new ArticleCategory("親子資訊", "親子資訊", "/assets/app/images/article/cat_1.jpg");
        category.save();
        category = new ArticleCategory("家長必讀", "家長必讀", "/assets/app/images/article/cat_2.jpg");
        category.save();
        category = new ArticleCategory("教養專題", "教養專題", "/assets/app/images/article/cat_3.jpg");
        category.save();
        category = new ArticleCategory("分享專區", "分享專區", "/assets/app/images/article/cat_4.jpg");
        category.save();
    }
    
    private static void bootstrapUser() {
        Query q = JPA.em().createQuery("Select count(u) from User u where system = true");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        final User superAdmin = new User();
        superAdmin.roles = Collections.singletonList(SecurityRole
                .findByRoleName(controllers.Application.SUPER_ADMIN_ROLE));
        superAdmin.active = true;
        superAdmin.lastLogin = new Date();
        superAdmin.email = "minibean.hk@gmail.com";
        superAdmin.emailValidated = true;
        superAdmin.newUser = false;
        superAdmin.name = "miniBean";
        superAdmin.displayName = "miniBean";
        superAdmin.lastName = "HK";
        superAdmin.firstName = "miniBean";
        superAdmin.system = true;
        superAdmin.linkedAccounts = null;
        superAdmin.save();
    }
    
    private static void bootstrapLocation() {
        Query q = JPA.em().createQuery("Select count(l) from Location l");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
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
        
        // Zodiac communities
        
        // rat
        String name = "鼠年媽媽會♥2008";
        String desc = "鼠年媽媽會♥2008";
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
    
    private static Community createZodiacCommunity(String name, String desc, int year) {
        Community community = null;
        TargetYear targetYear = TargetYear.valueOf(year);
        String zodiac = targetYear.getZodiac().name();
        String targetingInfo = targetYear.toString();
        try {
            community = User.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/zodiac/" + zodiac.toLowerCase() + ".png");
            community.system = true;
            community.targetingType = TargetingSocialObject.TargetingType.ZODIAC_YEAR;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(file);
        } catch (SocialObjectNotJoinableException e) {
            e.printStackTrace();
        }
        return community;
    }
    
    private static Community createLocationCommunity(String name, String desc, Location location) {
        Community community = null;
        String targetingInfo = location.id.toString();
        try {
            community = User.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/community/loc_" + location.locationType.name().toLowerCase() + ".png");
            community.system = true;
            community.targetingType = TargetingSocialObject.TargetingType.LOCATION_DISTRICT;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(file);
        } catch (SocialObjectNotJoinableException e) {
            e.printStackTrace();
        }
        return community;
    }
}