
//
// Flag to check if browser tab is active
// To control $interval
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

$(window).scroll(function() {
    //console.log("main-app scroll");
    if ($('#main-top').length > 0 && $('#main-top').visible(true)) {
        //console.log("header no glow");
        $('#header').removeClass('header-glow');
        toggleToNotVisible = true;
    } else if (toggleToNotVisible) {
        //console.log("header glow");
        $('#header').addClass('header-glow');
        toggleToNotVisible = false;
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
