
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
