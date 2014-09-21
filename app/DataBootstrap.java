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
        bootstrapTermsAndConditions();
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
    
    private static void bootstrapTermsAndConditions() {
        Query q = JPA.em().createQuery("Select count(tnc) from TermsAndConditions tnc");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }
        
        logger.underlyingLogger().info("bootstrapTermsAndConditions()");

        TermsAndConditions tnc = new TermsAndConditions();
        tnc.terms = 
                "<p>miniBean網站為閣下提供最佳服務及尊重閣下的個人私隱。因此，本公司在搜集、維護及使用個人資料時，保證遵守香港法例第 486 章《個人資料（私隱）條例》的要求。</p>" + 
                "<p>要求資料</p>" + 
                "<p>miniBean網站有數處地方可能要求閣下提供個人資料，這些資料可能包括：</p>" + 
                "<p>- 登入名稱及密碼</p>" + 
                "<p>- 姓名</p>" + 
                "<p>- 住址地區</p>" + 
                "<p>- 性別</p>" + 
                "<p>- 出生年份 </p>" + 
                "<p>- 電郵地址</p>" + 
                "<p>- 小孩子性別</p>" + 
                "<p>- 小孩子出生日期</p>" + 
                "<p>當登記成為會員或使用miniBean網站時，本公司可能要求閣下提供以上資料。閣下同意所提供給本公司的資料為正確、真實、有效和完整。</p>" + 
                "<p>資料使用</p>" + 
                "<p>若資料為不正確，不真實，無效或不完整，本公司有權利取消閣下用戶之註冊或使用網站之權利及服務。閣下知道及同意，你對所提供之資料的內容和準確性須負上所有責任。</p>" + 
                "<p>本公司偶爾會使用閣下所提供的資料通知閣下miniBean網站的轉變、新設的服務，及與閣下息息相關的優惠。如不想取得這些資料，可於電郵 info&#64;minibean.com.hk 通知我們。 閣下知道、同意及授權所提供的資料可能供給、披露及供存取予以下人士或公司及作出以下用途：</p>" + 
                "<p>- 本公司及 / 或本公司集團內的任何人士或公司；</p>" + 
                "<p>- 須向本公司履行保密責任之任何人士或公司；</p>" + 
                "<p>- 為上述目的或與上述目的有關而聘用之任何合約承包商、代理商、公司，或向本公司提供行政、電訊、電腦、付賬、專業服務或其他服務的公司；</p>" + 
                "<p>- 分析、核實及檢查閣下的信用、付款及相關服務的狀況；</p>" + 
                "<p>- 處理閣下要求的任何付款操作；</p>" + 
                "<p>- 收取閣下賬戶與服務有關的未付金額；</p>" + 
                "<p>- 與用戶有交往或計劃有交往之任何銀行或金融機構；或</p>" + 
                "<p>- 任何獲本公司轉讓或計劃轉讓權益及/或責任之人士或公司，而此等權益及 / 或責任與用戶或與向閣下提供之產品或服務有關。</p>" + 
                "<p>- 為向閣下提供購買之產品及服務，本公司需要向服務供應商或第三中介人提供用戶個人資料。如未能提供資料，本公司將不能有效地提供有關服務予閣下。</p>" + 
                "<p>- 部份資料可能會透過'cookies'收集，閣下可自行更改瀏覽的設定而使cookies失效。</p>" + 
                "<p>統計性資料</p>" + 
                "<p>請注意本公司有可能向第三者提供本公司客戶的統計資料，但這些統計資料將不會提及任何個別客戶。</p>" + 
                "<p>保護資料</p>" + 
                "<p>為防止不授權登入、保持數據的安全性及確保資料得到正確的運用，本公司不論在實體、電子化及管理上皆制定了合適程序去保障及保護本公司所收集的資料的安全性。</p>" + 
                "<p>連接到其他網站</p>" + 
                "<p>閣下可從miniBean網站連接到其他網站，但請注意該等網站的私隱政策很可能與本公司的不同。本公司建議閣下在該等網站披露其個人資料前，先詳細閱讀其私隱政策。[在任何情況下，該等網站都不會與本公司分享用戶的個人資料。]</p>" + 
                "<p>聊天室、交換電郵、告示板及網誌</p>" + 
                "<p>請留意若閣下自願在聊天室、交換電郵、告示板及網誌上公開披露其個人資料，該等資料很可能被收集及被他人使用及可能導致閣下因公開資料而收到其他不必要的訊息，本公司毋須就以上情況負責。</p>" + 
                "<p>登入/修改/更新個人資料</p>" + 
                "<p>閣下可以隨時在「我的帳號」裏修改及更新你的個人資料。會員戶口是受到密碼保護的，因此只有該會員能登入及檢視其會員戶口資料。</p>" + 
                "<p>通訊協定地址</p>" + 
                "<p>為方便管理伺服器及系統，本公司會收集用戶的通訊協定地址。請注意在miniBean網站上的連接網站很可能會收集閣下的個人資料，本私隱政策並不包括該等網站對其資料的處理及政策。</p>" + 
                "<p>私隱政策改動</p>" + 
                "<p>若本私穩政策有任何改動，本公司會在這裏刊登更新的政策及有關條文，以便閣下能查閱有關政策。閣下繼續使用miniBean網站代表你接受所有已更改的條款。除非有更新聲明，本公司不會在閣下沒有機會拒絕或避免的情況下，把你的個人資料運用在新的用途上。</p>" + 
                "<p>立法解除條款</p>" + 
                "<p>在法律要求下，本公司或會披露閣下個人資料而該等行為是必需的。本公司確信資料公開能保障及維護本公司、用戶及他人的權利、財產及安全，並相信該等資料會依據正確法律程序處理。</p>" + 
                "<p>電郵連接</p>" + 
                "<p>閣下可以利用miniBean網站上提供的電郵連接，直接與本公司聯絡並提出問題及發表意見。本公司會閱讀所有訊息及盡量給予回覆。該等資料會用作回應用戶問題及意見，或將用戶意見存檔，以便改善本公司之服務，或評論及摒棄該等資料。本公司不會在沒有用戶的同意下向第三者披露你的個人資料。</p>" + 
                "<p>聯絡我們</p>" + 
                "<p>若用戶有任何有關安全與私隱的問題，請電郵至 info&#64;minibean.com.hk 與我們聯絡。</p>";
        tnc.privacy = 
                "<p>miniBean網站為閣下提供最佳服務及尊重閣下的個人私隱。因此，本公司在搜集、維護及使用個人資料時，保證遵守香港法例第 486 章《個人資料（私隱）條例》的要求。</p>" + 
                "<p>要求資料</p>" + 
                "<p>miniBean網站有數處地方可能要求閣下提供個人資料，這些資料可能包括：</p>" + 
                "<p>- 登入名稱及密碼</p>" + 
                "<p>- 姓名</p>" + 
                "<p>- 住址地區</p>" + 
                "<p>- 性別</p>" + 
                "<p>- 出生年份 </p>" + 
                "<p>- 電郵地址</p>" + 
                "<p>- 小孩子性別</p>" + 
                "<p>- 小孩子出生日期</p>" + 
                "<p>當登記成為會員或使用miniBean網站時，本公司可能要求閣下提供以上資料。閣下同意所提供給本公司的資料為正確、真實、有效和完整。</p>" + 
                "<p>資料使用</p>" + 
                "<p>若資料為不正確，不真實，無效或不完整，本公司有權利取消閣下用戶之註冊或使用網站之權利及服務。閣下知道及同意，你對所提供之資料的內容和準確性須負上所有責任。</p>" + 
                "<p>本公司偶爾會使用閣下所提供的資料通知閣下miniBean網站的轉變、新設的服務，及與閣下息息相關的優惠。如不想取得這些資料，可於電郵 info&#64;minibean.com.hk 通知我們。 閣下知道、同意及授權所提供的資料可能供給、披露及供存取予以下人士或公司及作出以下用途：</p>" + 
                "<p>- 本公司及 / 或本公司集團內的任何人士或公司；</p>" + 
                "<p>- 須向本公司履行保密責任之任何人士或公司；</p>" + 
                "<p>- 為上述目的或與上述目的有關而聘用之任何合約承包商、代理商、公司，或向本公司提供行政、電訊、電腦、付賬、專業服務或其他服務的公司；</p>" + 
                "<p>- 分析、核實及檢查閣下的信用、付款及相關服務的狀況；</p>" + 
                "<p>- 處理閣下要求的任何付款操作；</p>" + 
                "<p>- 收取閣下賬戶與服務有關的未付金額；</p>" + 
                "<p>- 與用戶有交往或計劃有交往之任何銀行或金融機構；或</p>" + 
                "<p>- 任何獲本公司轉讓或計劃轉讓權益及/或責任之人士或公司，而此等權益及 / 或責任與用戶或與向閣下提供之產品或服務有關。</p>" + 
                "<p>- 為向閣下提供購買之產品及服務，本公司需要向服務供應商或第三中介人提供用戶個人資料。如未能提供資料，本公司將不能有效地提供有關服務予閣下。</p>" + 
                "<p>- 部份資料可能會透過'cookies'收集，閣下可自行更改瀏覽的設定而使cookies失效。</p>" + 
                "<p>統計性資料</p>" + 
                "<p>請注意本公司有可能向第三者提供本公司客戶的統計資料，但這些統計資料將不會提及任何個別客戶。</p>" + 
                "<p>保護資料</p>" + 
                "<p>為防止不授權登入、保持數據的安全性及確保資料得到正確的運用，本公司不論在實體、電子化及管理上皆制定了合適程序去保障及保護本公司所收集的資料的安全性。</p>" + 
                "<p>連接到其他網站</p>" + 
                "<p>閣下可從miniBean網站連接到其他網站，但請注意該等網站的私隱政策很可能與本公司的不同。本公司建議閣下在該等網站披露其個人資料前，先詳細閱讀其私隱政策。[在任何情況下，該等網站都不會與本公司分享用戶的個人資料。]</p>" + 
                "<p>聊天室、交換電郵、告示板及網誌</p>" + 
                "<p>請留意若閣下自願在聊天室、交換電郵、告示板及網誌上公開披露其個人資料，該等資料很可能被收集及被他人使用及可能導致閣下因公開資料而收到其他不必要的訊息，本公司毋須就以上情況負責。</p>" + 
                "<p>登入/修改/更新個人資料</p>" + 
                "<p>閣下可以隨時在「我的帳號」裏修改及更新你的個人資料。會員戶口是受到密碼保護的，因此只有該會員能登入及檢視其會員戶口資料。</p>" + 
                "<p>通訊協定地址</p>" + 
                "<p>為方便管理伺服器及系統，本公司會收集用戶的通訊協定地址。請注意在miniBean網站上的連接網站很可能會收集閣下的個人資料，本私隱政策並不包括該等網站對其資料的處理及政策。</p>" + 
                "<p>私隱政策改動</p>" + 
                "<p>若本私穩政策有任何改動，本公司會在這裏刊登更新的政策及有關條文，以便閣下能查閱有關政策。閣下繼續使用miniBean網站代表你接受所有已更改的條款。除非有更新聲明，本公司不會在閣下沒有機會拒絕或避免的情況下，把你的個人資料運用在新的用途上。</p>" + 
                "<p>立法解除條款</p>" + 
                "<p>在法律要求下，本公司或會披露閣下個人資料而該等行為是必需的。本公司確信資料公開能保障及維護本公司、用戶及他人的權利、財產及安全，並相信該等資料會依據正確法律程序處理。</p>" + 
                "<p>電郵連接</p>" + 
                "<p>閣下可以利用miniBean網站上提供的電郵連接，直接與本公司聯絡並提出問題及發表意見。本公司會閱讀所有訊息及盡量給予回覆。該等資料會用作回應用戶問題及意見，或將用戶意見存檔，以便改善本公司之服務，或評論及摒棄該等資料。本公司不會在沒有用戶的同意下向第三者披露你的個人資料。</p>" + 
                "<p>聯絡我們</p>" + 
                "<p>若用戶有任何有關安全與私隱的問題，請電郵至 info&#64;minibean.com.hk 與我們聯絡。</p>";
        tnc.save();
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
                        "小萌豆為所有龍媽媽蛇媽媽編制了2015-2016嘅幼兒班申請資訊。 請立即到 PN討論區 査看啦<br>" + 
                        "<span style='margin-left:7%;width:40%;display:inline-block;'><a href='my#/community/49'>港島PN討論區</a></span>" + 
                        "<span style='margin-left:7%;width:40%;display:inline-block;'><a href='my#/community/50'>九龍PN討論區</a></span>" + 
                        "<span style='margin-left:7%;width:40%;display:inline-block;'><a href='my#/community/51'>新界PN討論區</a></span>" +
                        "<span style='margin-left:7%;width:40%;display:inline-block;'><a href='my#/community/53'>離島PN討論區</a></span>",
                        AnnouncementType.TOP_INFO, 
                        new DateTime(2014,12,31,0,0).toDate());
        announcement.save();
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
        icon = new Icon("feedback", IconType.COMMUNITY_GENERAL, "/assets/app/images/general/icons/community/feedback.png");
        icon.save();
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
        
        ArticleCategory category = null;
        
        String[] cats = new String[] {
                "孕婦須知", "育兒通識", "親子活動", "教育與學校", "秀身扮靚", "有趣分享"
        };

        for (int i = 0; i < cats.length; i++) {
            category = new ArticleCategory(cats[i], cats[i], "/assets/app/images/article/cat_" + (i+1) + ".jpg", ArticleCategory.ArticleCategoryGroup.HOT_ARTICLES, i+1);
            category.save();
        }
        
        cats = new String[] {
                "準備中", "孕1月", "孕2月", "孕3月", "孕4月", "孕5月", "孕6月", "孕7月", "孕8月", "孕9月", "迎接小寶寶"
        };

        for (int i = 0; i < cats.length; i++) {
            category = new ArticleCategory(cats[i], cats[i], "/assets/app/images/article/soon_moms.jpg", ArticleCategory.ArticleCategoryGroup.SOON_TO_BE_MOMS_ARTICLES, i+1);
            category.save();
        }
    }

    private static void bootstrapCommunityCategory() {
        Query q = JPA.em().createQuery("Select count(cc) from CommunityCategory cc");
        Long count = (Long)q.getSingleResult();
        if (count > 0) {
            return;
        }

        logger.underlyingLogger().info("bootstrapCommunityCategory()");

        final String[] cats = new String[] {
                "熱門產品", "親子好去處", "優惠券", "送給您的小萌豆"
        };

        for (int i = 0; i < cats.length; i++) {
            CommunityCategory category = new CommunityCategory(cats[i], i+1);
            category.save();
        }
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
        name = "孕媽媽♥";
        desc = "孕媽媽♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/boy.png",
                TargetingType.SOON_MOMS_DADS,
                "SOON_MOMS_DADS");
        
        // NEW_MOMS_DADS
        name = "新手媽媽♥";
        desc = "新手媽媽♥";
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
        
        name = "小萌豆分享區♥";
        desc = "小萌豆分享區♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/beans.png",
                TargetingType.ALL_MOMS_DADS,
                "MINIBEAN_SHARING");
        
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
        name = "2008鼠年媽媽會♥";
        desc = "2008鼠年媽媽會♥";
        createZodiacCommunity(name, desc, 2008);
        
        // ox
        name = "2009牛年媽媽會♥";
        desc = "2009牛年媽媽會♥";
        createZodiacCommunity(name, desc, 2009);

        // tiger
        name = "2010虎年媽媽會♥";
        desc = "2010虎年媽媽會♥";
        createZodiacCommunity(name, desc, 2010);
        
        // rabbit
        name = "2011兔年媽媽會♥";
        desc = "2011兔年媽媽會♥";
        createZodiacCommunity(name, desc, 2011);
        
        // dragon
        name = "2012龍年媽媽會♥";
        desc = "2012龍年媽媽會♥";
        createZodiacCommunity(name, desc, 2012);
        
        // snake
        name = "2013蛇年媽媽會♥";
        desc = "2013蛇年媽媽會♥";
        createZodiacCommunity(name, desc, 2013);

        // horse
        name = "2014馬年媽媽會♥";
        desc = "2014馬年媽媽會♥";
        createZodiacCommunity(name, desc, 2014);
        
        // goat
        name = "2015羊年媽媽會♥";
        desc = "2015羊年媽媽會♥";
        createZodiacCommunity(name, desc, 2015);
        
        // monkey
        name = "2016猴年媽媽會♥";
        desc = "2016猴年媽媽會♥";
        createZodiacCommunity(name, desc, 2016);
        
        // rooster
        name = "2017鷄年媽媽會♥";
        desc = "2017鷄年媽媽會♥";
        createZodiacCommunity(name, desc, 2017);

        // dog
        name = "2018狗年媽媽會♥";
        desc = "2018狗年媽媽會♥";
        createZodiacCommunity(name, desc, 2018);
        
        // pig
        name = "2019猪年媽媽會♥";
        desc = "2019猪年媽媽會♥";
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
                    "/assets/app/images/general/icons/community/feedback.png");
            community.system = true;
            community.excludeFromNewsfeed = true;
            community.targetingType = TargetingType.PUBLIC;
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
    
    private static void createZodiacCommunity(String name, String desc, int year) {
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
            community.targetingInfo = targetingInfo;        // e.g. RAT
            //community.setCoverPhoto(file);
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
        
        for (int i = 1; i <= 12; i++) {
            createZodiacMonthCommunity(year, i);
        }
    }
    
    private static void createZodiacMonthCommunity(int year, int month) {
        String name = year + "年" + month + "月媽媽會♥";
        String desc = name;
        Community community = null;
        TargetYear targetYear = TargetYear.valueOf(new DateTime(year, month, 1, 0, 0));
        String zodiac = targetYear.getZodiac().name();
        String targetingInfo = year + "_" + month;      // e.g. 2013_8
        try {
            community = Application.getSuperAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/zodiac/" + zodiac.toLowerCase() + ".png");
            community.system = true;
            community.targetingType = TargetingType.ZODIAC_YEAR_MONTH;
            community.targetingInfo = targetingInfo;
            //community.setCoverPhoto(file);
        } catch (Exception e) {
            logger.underlyingLogger().error(ExceptionUtils.getStackTrace(e));
        }
    }
    
    private static void createLocationCommunity(String name, String desc, Location location) {
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