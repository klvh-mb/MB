// header bottom glow
var toggleToNotVisible = true;

$(window).scroll(function() {
    //console.log("main-app scroll");
    if ($('#main-top').visible(true)) {
        //console.log("header no glow");
        $('#header').removeClass('header-glow');
        toggleToNotVisible = true;
    } else if (toggleToNotVisible) {
        //console.log("header glow");
        $('#header').addClass('header-glow');
        toggleToNotVisible = false;
    }
});

// http://stackoverflow.com/questions/22964767/how-to-format-angular-moments-am-time-ago-directive
// http://jsbin.com/qeweyalu/1/edit
// am-time-ago date format
moment.lang('en', {
    relativeTime : {
        future: "在 %s",
        past:   "%s",
        s:  "%d秒",
        m:  "1分鐘",
        mm: "%d分鐘",
        h:  "1小時",
        hh: "%d小時",
        d:  "昨天",
        dd: "%d日",
        M:  "1個月",
        MM: "%d個月",
        y:  "1年前",
        yy: "%d年前"
    }
});