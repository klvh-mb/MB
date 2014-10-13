
//
// New user tour
//
var homeTour = new Tour({
  container: "body",
  template: "<div class='popover tour'>" + 
    "<div class='arrow'></div>" + 
      "<h3 class='popover-title'></h3>" + 
      "<div class='popover-content'></div>" + 
      "<div class='popover-navigation'>" + 
        "<button class='btn btn-default' data-role='prev'>« 上一步</button>" + 
        "<span data-role='separator'></span>" + 
        "<button class='btn btn-default' data-role='next'>下一步 »</button>" + 
        "<button class='btn btn-default pull-right' data-role='end'>完成!</button>" + 
      "</div>" + 
    "</div>", 
  steps: [
  {
    element: "#discover-communities",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_1.png\">" +
        "親子社群",
    content: 
        "尋找並關注更多親子社群。有不同主題，年份，地區社群讓媽媽分享心得"
  },
  {
    element: "#my-communities",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_2.png\">" + 
        "我關注的社群",
    content: 
        "您正在關注的社群會在這裏出現，方便隨時瀏覽或討論"
  },
  {
    element: "#my-newsfeed",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_3.png\">" + 
        "我的社群動向",
    content: 
        "讓您預覽您正在關注的社群中最新，最熱門話題。輕易知道所有關注社群的最新動向"
  },
  {
    element: "#main-uphoto",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_4.png\">" + 
        "我的個人主頁",
    content: 
        "您可以查看您所發佈或回覆的話題。還可以管理您關注的社群，朋友和喜愛的話題或文章"
  },
  {
    element: "#my-bookmarks",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_5.png\">" +
        "我的喜愛",
    content: 
        "在任何您有興趣的話題或文章，您可以按右方的" + 
        "<img style=\"width:18px;height:auto;margin:0 3px 3px 3px;\" src=\"/assets/app/images/general/icons/message_favorited.png\">" + 
        "收藏到我的喜愛，方便査看"
  }
]});

var mHomeTour = new Tour({
  container: "body",
  template: "<div class='popover tour'>" + 
    "<div class='arrow'></div>" + 
      "<h3 class='popover-title'></h3>" + 
      "<div class='popover-content'></div>" + 
      "<div class='popover-navigation'>" + 
        "<button class='btn btn-default' data-role='prev'>« 上一步</button>" + 
        "<span data-role='separator'></span>" + 
        "<button class='btn btn-default' data-role='next'>下一步 »</button>" + 
        "<button class='btn btn-default pull-right' data-role='end'>完成!</button>" + 
      "</div>" + 
    "</div>", 
  steps: [
  {
    element: "#site-tour-anchor",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_1.png\">" +
        "親子社群",
    content: 
        "尋找並關注更多親子社群。有不同主題，年份，地區社群讓媽媽分享心得"
  },
  {
    element: "#site-tour-anchor",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_2.png\">" + 
        "我關注的社群",
    content: 
        "您正在關注的社群會收藏到左方的選項表中，方便隨時瀏覽或討論"
  },
  {
    element: "#site-tour-anchor",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_3.png\">" + 
        "我的社群動向",
    content: 
        "讓您預覽您正在關注的社群中最新，最熱門話題。輕易知道所有關注社群的最新動向"
  },
  {
    element: "#site-tour-anchor",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_4.png\">" + 
        "我的個人主頁",
    content: 
        "您可以查看您所發佈或回覆的話題。還可以管理您關注的社群，朋友和喜愛的話題或文章"
  },
  {
    element: "#site-tour-anchor",
    title: 
        "<img style=\"width:32px;\" src=\"/assets/app/images/general/icons/ranking/rank_5.png\">" +
        "我的喜愛",
    content: 
        "在任何您有興趣的話題或文章，您可以按右方的" + 
        "<img style=\"width:18px;height:auto;margin:0 3px 3px 3px;\" src=\"/assets/app/images/general/icons/message_favorited.png\">" + 
        "收藏到我的喜愛，方便査看"
  }
]});

//
// Hooks to detect browser activeness 
// for header meta refresh
//

window.isBrowserTabActive = false;

$(window).focus(function () {
    window.isBrowserTabActive = true; 
}); 

$(window).blur(function () {
    window.isBrowserTabActive = false; 
});

$(window).on("blur focus", function(e) {
    var prevType = $(this).data("prevType"); // getting identifier to check by
    if (prevType != e.type) {   //  reduce double fire issues by checking identifier
        switch (e.type) {
            case "blur":
                window.isBrowserTabActive = false; 
                break;
            case "focus":
                window.isBrowserTabActive = true; 
                break;
        }
    }
    $(this).data("prevType", e.type); // reset identifier
})

//
// Common
//

var highlightLink = function(id) {
    $('#'+id).select();
}

//
// Simple log helper
//

var log = function(str) {
    if (this.console)
        console.log(str);
}

//
// header bottom glow
//

var toggleToNotVisible = true;
var mainLogoNotVisible = true;

$(window).scroll(function() {
    if ($('#main-top').length > 0 && $('#main-top').visible(true)) {
        //console.log("header no glow");
        $('#header-backdrop').removeClass('header-glow');
        toggleToNotVisible = true;
        
        // show main logo
        $('#main-logo').slideDown(300,'swing');
    } else if (toggleToNotVisible) {
        //console.log("header glow");
        $('#header-backdrop').addClass('header-glow');
        toggleToNotVisible = false;
    }
    
    if ($('#header-menu').length > 0 && $('#header-menu').visible(true)) {
        mainLogoNotVisible = true;
    } else if (mainLogoNotVisible) {
        // hide main logo
        $('#header-logo').hide();
        $('#header-logo').show(500);
        $('#main-logo').slideUp(300,'swing');
        mainLogoNotVisible = false;
    }
});

//
// http://stackoverflow.com/questions/22964767/how-to-format-angular-moments-am-time-ago-directive
// http://jsbin.com/qeweyalu/1/edit
// am-time-ago date format
//

moment.lang('en', {
    relativeTime : {
        future: "在 %s",
        past:   "%s",
        s:  "剛剛",   //"%d秒",
        m:  "1分鐘前",
        mm: "%d分鐘前",
        h:  "1小時前",
        hh: "%d小時前",
        d:  "昨天",
        dd: "%d日前",
        M:  "1個月前",
        MM: "%d個月前",
        y:  "1年前",
        yy: "%d年前"
    }
});

//
// Utility function to convert to real links
//

var convertText = function(text) {
    var escaped = escapeHtmlSpecialChars(text);
    return convertToLinks(convertEmoticons(escaped));
}

var escapeHtmlSpecialChars = function(text) {
    return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;');
}

var convertEmoticons = function(text) {
    // TODO
    return text;
}

var convertToLinks = function(text) {
    var replacedText, replacePattern1, replacePattern2;

    //"http(s)://"
    //replacePattern1 = /(\b(https?):\/\/[-A-Z0-9+&amp;@#\/%?=~_|!:,.;]*[-A-Z0-9+&amp;@#\/%=~_|])/ig;
    replacePattern1 = /(\b(https?):\/\/.*[-A-Z0-9+&amp;@#\/%=~_|])/ig;
    replacedText = text.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //"www."
    replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    return replacedText;
}

//
// bootbox
//

var prompt = function(message, className) {
    if (className == undefined) {
        className = "bootbox-default-prompt";
    }
    
    bootbox.dialog({
        message: message,
        title: "",
        className: className,
        buttons: {
            /*main: {
                label: "Copy",
                className: "btn-default",
                callback: function() {
                    $('.bootbox-body').select();
                }
            },*/
            success: {
                label: "OK",
                className: "btn-primary",
                callback: function() {
                }
            }
        }
    });
}