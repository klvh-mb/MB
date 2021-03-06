package data;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import common.cache.CommunityMetaCache;
import common.thread.ThreadLocalOverride;
import customdata.file.PlayGroupFileReader;
import customdata.file.ReviewFileReader;
import customdata.file.PostsFileReader;
import domain.CommentType;
import domain.PostType;
import models.Announcement;
import models.ArticleCategory;
import models.Comment;
import models.Community;
import models.CommunityCategory;
import models.Community.CommunityType;
import models.Emoticon;
import models.Icon;
import models.Icon.IconType;
import models.Kindergarten;
import models.Location;
import models.Location.LocationCode;
import models.PlayGroup;
import models.PreNursery;
import models.Post;
import models.SecurityRole;
import models.TagWord;
import models.TargetingSocialObject.TargetingType;
import models.TermsAndConditions;
import models.User;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import providers.MyUsernamePasswordAuthUser;
import common.model.TargetYear;
import common.model.TodayWeatherInfo;
import controllers.Application;
import providers.MyUsernamePasswordAuthProvider.MySignup;

/**
 * data.DataBootstrap
 */
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
        bootstrapTagWords();

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
                "<p>歡迎你加入miniBean成為會員，miniBean會員服務(以下稱會員服務)是由miniBean Ltd(以下稱本公司)所建置提供，所有申請使用會員服務之使用者(以 下稱會員)，都應該詳細閱讀下列使用條款，這些使用條款訂立的目的，是為了保護會員服務的提供者以及所有使用者的利益，並構成使用者與會員服務提供者之間 的契約，使用者完成註冊手續、或開始使miniBean所提供之會員服務時，即視為已知悉、並完全同意本使用條款的所有約定。</p>" +
                "<p>會員服務</p>" +
                "<p>一旦本公司完成並確認你的申請後，本公司將提供予你的免費會員服務內容有：會員專區、會員電子報，及其他未來可能新增之一般會員服務。會員服務之期間，是指使用者填妥申請表格並完成註冊程序後，本公司於完成相關系統設定、使會員服務達於可供使用之日。因會員服務所提供之所有相關網域名稱、 網路位址、功能以及其他一切因會員身分得享有之權益，均仍屬本公司或其他合法權利人所有，會員除僅得於服務期間內依本使用條款之約定為使用外，均不得以任 何方式將其轉讓、移轉、出租或出借予其他任何第三人。 會員服務僅依當時所提供之功能及狀態提供服務；本公司並保留新增、修改或取消會員服務內相關系統或功能之全部或一部之權利。帳號、密碼與安全性在使用會員 服務以前，必須經過完整的註冊手續，在註冊過程中你必須填入完整、而且正確的資料。在註冊過程中你可以自行選擇使用者名稱和密碼，但在使用會員服務的過程 中，你必須為經由這個使用者名稱和密碼所進行的所有行為負責。 對於你所取得的使用者名稱和密碼，你必須負妥善保管和保密的義務，如果你發現或懷疑這個使用者名稱和密碼被其他人冒用或不當使用，你必須立即通知 info&#64;minibean.com.hk，讓本公司儘快採取適當之因應措施，但上述因應措施不得因此解釋為本公司明示或默示對你負有任何形式之賠償或補償之責任或義務。</p>" +
                "<p>使用者的行為</p>" +
                "<p>1. 任何未經事前授權的商業行為都是被禁止的。</p>" +
                "<p>2. 你必須遵守相關法律，並且對於經由使用者名稱和密碼所進行的任何行為、以及所儲存的所有資料負責。你必須同意不從事以下的行為：</p>" +
                "<p>(a) 傳送何任違反香港特別行政區法律之留言、討論、電郵，及私人訊息。</p>" +
                "<p>(b) 刊載、傳輸、發送或儲存任何誹謗、欺詐、傷害、猥褻、色情、賭博或其他一切違反法律之留言、討論、電郵、私人訊息、檔案或資料。</p>" +
                "<p>(c) 刊載、傳輸、發送或儲存任何侵害他人智慧財產權或其他權益的資料。</p>" +
                "<p>(d) 未經同意收集他人電子郵件位址以及其他個人資料。</p>" +
                "<p>(e) 未經同意擅自摘錄或使用會員服務內任何資料庫內容之部份或全部。</p>" +
                "<p>(f) 刊載、傳輸、發送、儲存病毒、或其他任何足以破壞或干擾電腦系統或資料的程式或訊息。</p>" +
                "<p>(g) 破壞或干擾會員服務的系統運作或違反一般網路禮節之行為。</p>" +
                "<p>(h) 在未經授權下進入會員服務系統或是與系統有關之網路、或冒用他人帳號或偽造寄件人辨識資料傳送郵件或留言，企圖誤導收件人之判斷。</p>" +
                "<p>(i) 任何妨礙或干擾其他使用者使用會員服務之行為。</p>" +
                "<p>(j)傳送幸運連鎖信、垃圾郵件、廣告信或其他漫無目的之訊息。</p>" +
                "<p>(k)於站內進行任可未經許可的銷售及訂購活動。</p>" +
                "<p>(l)於未經許可的情況下在站內進行與保險、投資、傳銷、市場調查、座談會、僱傭及各類轉介等有關之宣傳及行銷活動。</p>" +
                "<p>(m)任何透過不正當管道竊取會員服務之會員帳號、密碼或存取權限之行為。</p>" +
                "<p>(n)其他不符合會員服務所提供的使用目的之行為。</p>" +
                "<p>責任限制</p>" +
                "<p>本公司所提供之各項會員服務，均僅依各該服務當時之功能及現況提供使用， 對於使用者之特定要求或需求，包括但不限於速度、安全性、可靠性、完整性、正確性及不會斷線和出錯等，本公司不負任何形式或內容之擔保或保證責任。 本公司不保證任何郵件、檔案或資料於傳送過程均可靠且正確無誤，亦不保證所儲存或所傳送之郵件、檔案或資料之安全性、可靠性、完整性、正確性及不會斷線和 出錯等，會員應對傳送過程中或儲存時之郵件、檔案或資料遺失或電腦系統損壞自負完全責任，與本公司無涉。因本公司所提供的會員服務本身之使用，所造成之任 何直接或間接之損害，本公司均不負任何責任，即使係本公司曾明白建議之注意事項亦同。</p>" +
                "<p>服務暫停或中斷</p>" +
                "<p>1.在下列情形，本公司將暫停或中斷本服務之全部或一部，且對使用者任何直接或間接之損害，均不負任何責任：</p>" +
                "<p>(a)對本服務相關軟硬體設備進行搬遷、更換、升級、保養或維修時。</p>" +
                "<p>(b)使用者有任何違反政府法律或本使用條款情形。</p>" +
                "<p>(c)天災或其他不可抗力所致之服務停止或中斷。</p>" +
                "<p>(d)其他不可歸責於本公司之事由所致之服務停止或中斷。</p>" +
                "<p>2.會員服務系統或功能『例行性』之維護、改置或變動所發生之服務暫停或中斷，本公司將於該暫停或中斷前以電子郵件、公告或其他適當之方式告知會員。</p>" +
                "<p>3.因使用者違反相關法令或本使用條款、或依相關主管機關之要求、或因其他不可歸 責於本公司之事由，而致本服務全部或一部暫停或中斷時，暫停或中斷期間仍照常計費。</p>" +
                "<p>終止服務</p>" +
                "<p>1.基於公司的運作，會員服務有可能停止提供服務之全部或一部，使用者不可以因此而要求任何賠償或補償。</p>" +
                "<p>2.如果你違反了本使用條款，本公司保留隨時暫時停止提供服務、或終止提供服務之權利，你不可以因此而要求任何賠償或補償。</p>" +
                "<p>3.如果你在會員服務上所刊載、傳輸、發送或儲存的郵件、檔案或資料，有任何違反法令、違反本使用條款、或有侵害第三人權益之虞者，本公司保留隨時得不經通知直接加以移動或刪除之權利。若本公司因此受到任何損害，你應對本公司負損害賠償之責任。</p>" +
                "<p>4.會員服務系統會自動偵測沒有使用的帳號，如超過九十天均未使用， 本公司將有權將會員的帳戶、檔案或資料全數刪除且不予另行備份而毋需另行通知，如你需重新啟用該電子郵件帳號，請另與網站管理者聯繫。上述有無使用之紀錄，均以會員服務系統內留存之紀錄為準。</p>" +
                "<p>修改本使用條款的權利</p>" +
                "<p>本公司保留隨時修改本會員服務使用規則的權利，修改後的會員服務使用條款將公佈在本公司的網站上，不另外個別通知使用者。</p>" +
                "<p>準據法及管轄權</p>" +
                "<p>1.本約定條款解釋、補充及適用均以香港特別行政區為準據法。</p>" +
                "<p>2.因本約定條款所發生之訴訟，以香港特別行政區法院為第一審管轄法院。</p>";
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
                "<p>閣下可以隨時在miniBean裏修改及更新你的個人資料。會員戶口是受到密碼保護的，因此只有該會員能登入及檢視其會員戶口資料。</p>" +
                "<p>通訊協定地址</p>" + 
                "<p>為方便管理伺服器及系統，本公司會收集用戶的通訊協定地址。請注意在miniBean網站上的連接網站很可能會收集閣下的個人資料，本私隱政策並不包括該等網站對其資料的處理及政策。</p>" + 
                "<p>私隱政策改動</p>" + 
                "<p>若本私穩政策有任何改動，本公司會在這裏刊登更新的政策及有關條文，以便閣下能查閱有關政策。閣下繼續使用miniBean網站代表你接受所有已更改的條款。除非有更新聲明，本公司不會在閣下沒有機會拒絕或避免的情況下，把你的個人資料運用在新的用途上。</p>" + 
                "<p>立法解除條款</p>" + 
                "<p>在法律要求下，本公司或會披露閣下個人資料而該等行為是必需的。本公司確信資料公開能保障及維護本公司、用戶及他人的權利、財產及安全，並相信該等資料會依據正確法律程序處理。</p>" + 
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
                        "我地有手機版啦！立即用手機登入 www.minibean.hk 試下啦",
                        new DateTime(2015,12,31,0,0).toDate());
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
                "嬰兒產品", "用家推薦", "優惠券", "送給您的小萌豆"
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
        name = "親子去處♥";
        desc = "親子去處♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/stroller.png",
                TargetingType.ALL_MOMS_DADS,
                "BABY_FRIENDLY_PLACES");
        
        name = "閒聊專區♥";
        desc = "閒聊專區♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/beans.png",
                TargetingType.ALL_MOMS_DADS,
                "MINIBEAN_SHARING");
        
        // PUBLIC
        name = "親子旅遊♥";
        desc = "親子旅遊♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/plane.png",
                TargetingType.PUBLIC,
                "TRAVEL");
        
        name = "寵物朋友♥";
        desc = "寵物朋友♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/cat.png",
                TargetingType.PUBLIC,
                "PETS");
        
        name = "單親媽媽♥";
        desc = "單親媽媽♥";
        createTargetingCommunity(name, desc, 
                "/assets/app/images/general/icons/community/mom.png",
                TargetingType.PUBLIC,
                "SINGLE_MOMS");
        
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

    private static void bootstrapTagWords() {
        String soonMomsCatId = ArticleCategory.ArticleCategoryGroup.SOON_TO_BE_MOMS_ARTICLES.name();

        Query q = JPA.em().createQuery("Select count(t) from TagWord t where t.tagCategoryId = ?1");
        q.setParameter(1, soonMomsCatId);
        Long count = (Long)q.getSingleResult();
        if (count == 0) {
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "造人", "造人,生B");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "早產", "早產");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "官外孕", "官外孕");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "分娩", "分娩,產程,陣痛");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "水腫", "水腫");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "胎教", "胎教");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "爸爸", "爸爸,父親,老公");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "營養", "營養,碘,飲食,鈣,鋅,蛋白質");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "皮膚", "皮膚");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "旅行", "旅行,旅遊,坐飛機");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "黃疸", "黃疸");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "掃風", "掃風");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "坐月", "坐月,陪月,薑醋");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "母乳", "母乳,人奶,上奶,奶泵");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "奶粉", "奶粉");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "雙胞胎", "雙胞胎,龍鳳胎,孖仔,孖女");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "臍帶血", "臍帶血");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "改名", "改名");
            createTagWord(TagWord.TagCategory.ARTICLE, soonMomsCatId, "孕婦裝", "孕婦裝,大肚婆衫,孕婦褲,連身裙,孕期內衣");
        }

        String hotArticlesCatId = ArticleCategory.ArticleCategoryGroup.HOT_ARTICLES.name();
        q = JPA.em().createQuery("Select count(t) from TagWord t where t.tagCategoryId = ?1");
        q.setParameter(1, hotArticlesCatId);
        count = (Long)q.getSingleResult();
        if (count == 0) {
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "爸爸", "爸爸,父親,老公");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "營養", "營養,碘,飲食,鈣,鋅,蛋白質");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "旅行", "旅行,旅遊,坐飛機");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "母乳", "母乳,人奶,上奶,奶泵");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "奶粉", "奶粉");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "雙胞胎", "雙胞胎,龍鳳胎,孖仔,孖女");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "寵物", "寵物,拳師犬,狗狗");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "食譜", "食譜,菜單,餐單");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "閱讀", "閱讀,圖書,看書,故事書,繪本");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "尿片", "尿布,尿褲,尿片");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "攝影", "攝影,拍攝,相機,閃光燈");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "學說話", "學說話,學會說話,不會說話,說話能力");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "瘦身", "瘦身,瘦腿,減肥");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "音樂", "音樂,古典樂,莫扎特,樂器,練琴");
            createTagWord(TagWord.TagCategory.ARTICLE, hotArticlesCatId, "聖誕", "聖誕,聖誕老人,聖誕氣氛");
        }
    }

    /**
     * Invoked from command line
     */
    public static void bootstrapPNCommunity() {
        // PN communities
        List<PreNursery> pns = PreNursery.findAll();
        logger.underlyingLogger().info("bootstrapPNCommunity() - count="+pns.size());

        for (PreNursery pn : pns) {
            String name = pn.getName();
            String desc = pn.getName()+" PreNursery討論區";
            String targetingInfo = pn.getId().toString();
            Community community = getOrCreateSchoolCommunity(TargetingType.PRE_NURSERY, name, desc, targetingInfo);
            if (community != null) {
                pn.communityId = community.getId();
                pn.merge();
            }
        }

        // reload cache
        CommunityMetaCache.loadPreNurseryComms();
    }

    /**
     * Invoked from command line
     */
    public static void bootstrapKGCommunity() {
        int commExistsCount = 0;
        int mergeCount = 0;
        int commCreatedCount = 0;

        List<Kindergarten> kgs = Kindergarten.findAll();
        for (Kindergarten kg : kgs) {
            if (kg.communityId == null) {
                PreNursery pn = PreNursery.findBy(kg.name, kg.nameEn, kg.address);
                if (pn != null) {
                    kg.communityId = pn.communityId;
                    kg.noOfPosts = pn.noOfPosts;
                    kg.merge();
                    mergeCount++;
                }
                else {
                    String name = kg.getName();
                    String desc = kg.getName()+" 討論區";
                    String targetingInfo = kg.getId().toString();
                    Community community = getOrCreateSchoolCommunity(TargetingType.KINDY, name, desc, targetingInfo);
                    if (community != null) {
                        kg.communityId = community.getId();
                        kg.merge();
                        commCreatedCount++;
                    }
                }
            } else {
                commExistsCount++;
                logger.underlyingLogger().info("Has Comm already - KG("+kg.name+", "+kg.nameEn+", "+kg.address+")");
            }
        }
        logger.underlyingLogger().info("commExistsCount("+commExistsCount+") commCreatedCount("+commCreatedCount+") mergeCount("+mergeCount+")");

        // reload cache
        CommunityMetaCache.loadKindergartenComms();
    }

    /**
     * Invoked from command line
     */
    public static void bootstrapPlayGroups(String filePath) {
        PlayGroupFileReader reader = new PlayGroupFileReader();
        try {
            reader.read(filePath);
            List<PlayGroup> pgs = reader.getPGs();
            logger.underlyingLogger().info("bootstrapPlayGroups() creating - count="+pgs.size());
            for (PlayGroup pg : pgs) {
                pg.save();
            }
            logger.underlyingLogger().info("bootstrapPlayGroups() done - count="+pgs.size());
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in bootstrapPlayGroups", e);
        }
    }

    public static void bootstrapPGCommunity() {
        // PG communities
        List<PlayGroup> pns = PlayGroup.findAll();
        logger.underlyingLogger().info("bootstrapPGCommunity() - count="+pns.size());

        for (PlayGroup pn : pns) {
            String name = pn.getName();
            String desc = pn.getName()+" PlayGroup討論區";
            String targetingInfo = pn.getId().toString();
            Community community = getOrCreateSchoolCommunity(TargetingType.PLAYGROUP, name, desc, targetingInfo);
            if (community != null) {
                pn.communityId = community.getId();
                pn.merge();
            }
        }
        // reload cache
        CommunityMetaCache.loadPlayGroupComms();
    }

    /**
     * Invoked from command line
     */
    public static void bootstrapPNReviews(String filePath) {
        ReviewFileReader reader = new ReviewFileReader();
        try {
            reader.read(filePath);
            List<ReviewFileReader.ReviewEntry> reviewEntries = reader.getReviews();

            boolean allCompleted = true;
            for (ReviewFileReader.ReviewEntry review : reviewEntries) {
                if (!review.isCompleted()) {
                    logger.underlyingLogger().info("Incomplete post: "+review.toString());
                    allCompleted = false;
                }
                else {
                    for (ReviewFileReader.ReviewComment reviewComment : review.comments) {
                        if (!reviewComment.isCompleted()) {
                            logger.underlyingLogger().info("Incomplete comment: "+reviewComment.toString());
                            allCompleted = false;
                        }
                    }
                }
            }

            if (!allCompleted) {
                return;     // return if incomplete input data
            }

            for (ReviewFileReader.ReviewEntry review : reviewEntries) {
                logger.underlyingLogger().info("Creating post: "+review.toString());

                User owner = User.findById(review.userId);
                PreNursery preNursery = PreNursery.findByNameDistrictId(review.pnName, review.districtId);

                if (owner == null) {
                    logger.underlyingLogger().info("Invalid data. userId="+review.userId);
                } else if (preNursery == null) {
                    logger.underlyingLogger().info("Invalid data. pnName="+review.pnName+" districtId="+review.districtId);
                } else {
                    Community community = Community.findById(preNursery.communityId);
                    ThreadLocalOverride.setSocialUpdatedDate(review.dateTime.toDate());
                    Post post = (Post) community.onPost(owner, review.title, review.body, PostType.QUESTION);
                    ThreadLocalOverride.setSocialUpdatedDate(null);
                    post.setCreatedDate(review.dateTime.toDate());
                    post.setUpdatedDate(review.dateTime.toDate());

                    for (ReviewFileReader.ReviewComment reviewComment : review.comments) {
                        owner = User.findById(reviewComment.userId);
                        ThreadLocalOverride.setSocialUpdatedDate(reviewComment.dateTime.toDate());
                        Comment comment = (Comment) post.onComment(owner, reviewComment.body, CommentType.ANSWER);
                        ThreadLocalOverride.setSocialUpdatedDate(null);
                        comment.setCreatedDate(reviewComment.dateTime.toDate());
                        comment.setUpdatedDate(reviewComment.dateTime.toDate());
                        comment.merge();

                        post.socialUpdatedDate = reviewComment.dateTime.toDate();
                    }

                    post.merge();
                }
            }
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in bootstrapPNReviews", e);
        }
    }

    public static void bootstrapCommunityPosts(String filePath) {
        PostsFileReader reader = new PostsFileReader();
        try {
            reader.read(filePath);
            List<PostsFileReader.PostEntry> postEntries = reader.getPosts();

            boolean allCompleted = true;
            for (PostsFileReader.PostEntry post : postEntries) {
                if (!post.isCompleted()) {
                    logger.underlyingLogger().info("Incomplete post: "+post.toString());
                    allCompleted = false;
                }
                else {
                    for (PostsFileReader.Comment comment : post.comments) {
                        if (!comment.isCompleted()) {
                            logger.underlyingLogger().info("Incomplete comment: "+comment.toString());
                            allCompleted = false;
                        }
                    }
                }
            }

            if (!allCompleted) {
                return;     // return if incomplete input data
            }

            for (PostsFileReader.PostEntry entry : postEntries) {
                logger.underlyingLogger().info("Creating post: "+entry.toString());

                User owner = User.findById(entry.userId);
                Community community = Community.findByName(entry.commName);
                 if (owner == null) {
                     logger.underlyingLogger().info("Invalid data. userId="+entry.userId);
                } else if (community == null) {
                     logger.underlyingLogger().info("Invalid data. commName="+entry.commName);
                } else {
                     ThreadLocalOverride.setSocialUpdatedDate(entry.dateTime.toDate());
                     Post post = (Post) community.onPost(owner, entry.title, entry.body, PostType.QUESTION);
                     ThreadLocalOverride.setSocialUpdatedDate(null);
                     post.setCreatedDate(entry.dateTime.toDate());
                     post.setUpdatedDate(entry.dateTime.toDate());

                     for (PostsFileReader.Comment pComment : entry.comments) {
                         owner = User.findById(pComment.userId);
                         ThreadLocalOverride.setSocialUpdatedDate(pComment.dateTime.toDate());
                         Comment comment = (Comment) post.onComment(owner, pComment.body, CommentType.ANSWER);
                         ThreadLocalOverride.setSocialUpdatedDate(null);
                         comment.setCreatedDate(pComment.dateTime.toDate());
                         comment.setUpdatedDate(pComment.dateTime.toDate());
                         comment.merge();
                     }

                     post.merge();
                }
            }
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in bootstrapCommunityPosts", e);
        }
    }

    //////////////// Creation Helpers ////////////////

    private static Community createFeedbackCommunity(String name, String desc) {
        Community community = null;
        try {
            community = Application.getMBAdmin().createCommunity(
                    name, desc, CommunityType.OPEN, 
                    "/assets/app/images/general/icons/community/feedback.png");
            community.system = true;
            community.excludeFromNewsfeed = true;
            community.targetingType = TargetingType.OTHER;
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
            community = Application.getMBAdmin().createCommunity(
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
        String zodiac = targetYear.getZodiacYear().getZodiac().name();
        String targetingInfo = targetYear.getZodiacInfo();
        try {
            community = Application.getMBAdmin().createCommunity(
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
        String zodiac = targetYear.getZodiacYear().getZodiac().name();
        String targetingInfo = year + "_" + month;      // e.g. 2013_8
        try {
            community = Application.getMBAdmin().createCommunity(
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
            community = Application.getMBAdmin().createCommunity(
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

    @Transactional
    private static Community getOrCreateSchoolCommunity(TargetingType targetingType, String name, String desc, String targetingInfo) {
        Community newComm = Community.findByNameTargetingTypeInfo(name, targetingType, targetingInfo);
        if (newComm == null) {
            try {
                newComm = Application.getMBAdmin().createCommunity(
                        name, desc, CommunityType.OPEN,
                        "/assets/app/images/general/icons/community/grad_hat.png");
                newComm.system = true;
                newComm.excludeFromNewsfeed = true;
                newComm.targetingType = targetingType;
                newComm.targetingInfo = targetingInfo;

                logger.underlyingLogger().info("Created "+targetingType+" community (id="+newComm.getId()+"): "+name);
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in getOrCreateSchoolCommunity", e);
            }
        } else {
            logger.underlyingLogger().info("Updated with "+targetingType+" community (id="+newComm.getId()+"): "+name);
        }
        return newComm;
    }

    private static TagWord createTagWord(TagWord.TagCategory tagCategory, String tagCategoryId,
                                         String displayWord, String matchingWords) {
        TagWord tagWord = new TagWord();
        tagWord.tagCategory = tagCategory;
        tagWord.tagCategoryId = tagCategoryId;
        tagWord.displayWord = displayWord;
        tagWord.matchingWords = matchingWords;
        tagWord.save();

        return tagWord;
    }
}