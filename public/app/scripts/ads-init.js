if (!window.af) {
    var af = (function() {
        return {
            getcookie:function(n){
                var m=n+'=';
                var ca=document.cookie.split(';');
                for(var i=0;i < ca.length;i++) {
                    var c=ca[i];
                    while (c.charAt(0)==' ')
                        c = c.substring(1,c.length);
                    if (c.indexOf(m) == 0)
                        return c.substring(m.length,c.length);
                }
                return null;
            },
            existcookie:function(n){
                var m=af.getcookie(n);
                if (m!=null&&m!=''){
                    return true;
                }else{
                    return false;
                }
            }
        };
    })();
}

window.registeredAds = {};
window.registeredAds[300250] = []; // Here you need to define AdsFactor size
window.registeredAds[72890] = [];
window.registeredAds[11] = [];
document.write = function(node) {
    console.log('AD RESPONSE: '+node);
    if(typeof node !== 'undefined') {
        var elem;
        if (is300x250(node)) {
            elem = registeredAds[300250].pop();
            console.error('poped 300x250');
        } else if (is728x90(node)) {
            elem = registeredAds[72890].pop();
            console.error('poped 728x90');
        } else if (is1x1(node)) {
            elem = registeredAds[11].pop();
            console.error('poped 1x1');
        } else {
            console.error('NOT AN AD ?! no pop');
        }
        
        if(typeof elem !== 'undefined') {
            $(elem).after(node);
            console.error('added after '+elem);
        }
        
        /*
        var n = $(node)[2].innerHTML.lastIndexOf("swfobject.embedSWF");
        if(n == -1){
            var d = $(node)[2].innerHTML.slice(n,100000000);
            var data = d.split("/");
            var chk = data[5].split("_");
            if(typeof chk[1] !== 'undefined') {
                var elem = registeredAds[chk[1]].pop();
                if(typeof elem !== 'undefined') {
                    $(elem).after(node);
                }
            }
        }
        */
    }
}

var is1x1 = function(node) {
    if (node.lastIndexOf("width='1'") != -1 && node.lastIndexOf("height='1'") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=\"1\"") != -1 && node.lastIndexOf("height=\"1\"") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=1") != -1 && node.lastIndexOf("height=1") != -1) {
        return true;
    }
    if (node.lastIndexOf("1x1") != -1) {
        return true;
    }
    return false;
}

var is300x250 = function(node) {
    if (node.lastIndexOf("'300'") != -1 && node.lastIndexOf("'250'") != -1) {
        return true;
    }
    if (node.lastIndexOf("\"300\"") != -1 && node.lastIndexOf("\"250\"") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=300") != -1 && node.lastIndexOf("height=250") != -1) {
        return true;
    }
    if (node.lastIndexOf("300x250") != -1) {
        return true;
    }
    return false;
}

var is728x90 = function(node) {
    if (node.lastIndexOf("'728'") != -1 && node.lastIndexOf("'90'") != -1) {
        return true;
    }
    if (node.lastIndexOf("\"728\"") != -1 && node.lastIndexOf("\"90\"") != -1) {
        return true;
    }
    if (node.lastIndexOf("width=728") != -1 && node.lastIndexOf("height=90") != -1) {
        return true;
    }
    if (node.lastIndexOf("728x90") != -1) {
        return true;
    }
    return false;
}

var MB_MAG_1x1                  = 337876666690;
var MB_MAG_300x250              = 336814706671;
var MB_MAG_728x90               = 643242756821;
var MB_MY_MAG_1x1               = 714880026672;
var MB_MY_MAG_300x250           = 785814486673;
var MB_MY_MAG_728x90            = 717503756822;
var MB_HOME_1x1                 = 510902096674;
var MB_HOME_300x250             = 550424006675;
var MB_HOME_728x90              = 852850346820;
var MB_ARTICLE_1x1              = 985037346676;
var MB_ARTICLE_300x250          = 244781106677;
var MB_ARTICLE_728x90           = 942495036804;
var MB_ARTICLE_LANDING_1x1      = 691373536678;
var MB_ARTICLE_LANDING_300x250  = 247130236679;
var MB_ARTICLE_LANDING_728x90   = 630501056805;
var MB_COMM_1x1                 = 512344316680;
var MB_COMM_300x250             = 586656756681;
var MB_COMM_728x90              = 688071966808;
var MB_COMM_LANDING_1x1         = 516615626682;
var MB_COMM_LANDING_300x250     = 982948656683;
var MB_COMM_LANDING_728x90      = 576698686809;
var MB_BCOMM_1x1                = 886585016684;
var MB_BCOMM_300x250            = 652876066685;
var MB_BCOMM_728x90             = 565242836806;
var MB_BCOMM_LANDING_1x1        = 975430386686;
var MB_BCOMM_LANDING_300x250    = 382773116687;
var MB_BCOMM_LANDING_728x90     = 619687626807;
var MB_OTHER_1x1                = 887185786688;     // profile
var MB_OTHER_300x250            = 263403256689;     // profile
var MB_OTHER_728x90             = 477069296823;     // message

var MB_COMM_P1_1x1              = 940313186737;
var MB_COMM_P1_300x250          = 995251896691;
var MB_COMM_P1_728x90           = 395216266815;
var MB_COMM_LANDING_P1_1x1      = 469337956699;     // comm discovery
var MB_COMM_LANDING_P1_300x250  = 772388026700;     // comm discovery
var MB_COMM_LANDING_P1_728x90   = 415742666810;
var MB_COMM_P2_1x1              = 376893136692;
var MB_COMM_P2_300x250          = 894219356670;
var MB_COMM_P2_728x90           = 136559036816;
var MB_COMM_LANDING_P2_1x1      = 633668986701;
var MB_COMM_LANDING_P2_300x250  = 602161336702;
var MB_COMM_LANDING_P2_728x90   = 435015746811;
var MB_COMM_P3_1x1              = 689189426693;
var MB_COMM_P3_300x250          = 418858546694;
var MB_COMM_P3_728x90           = 203743246817;
var MB_COMM_LANDING_P3_1x1      = 339054986703;
var MB_COMM_LANDING_P3_300x250  = 487874436704;
var MB_COMM_LANDING_P3_728x90   = 984442326812;
var MB_COMM_P4_1x1              = 722211466695;
var MB_COMM_P4_300x250          = 925733136696;
var MB_COMM_P4_728x90           = 502038016818;
var MB_COMM_LANDING_P4_1x1      = 589191186705;
var MB_COMM_LANDING_P4_300x250  = 150519856706;
var MB_COMM_LANDING_P4_728x90   = 230571536813;
var MB_COMM_P5_1x1              = 247393906697;
var MB_COMM_P5_300x250          = 842233366698;
var MB_COMM_P5_728x90           = 684802916819;
var MB_COMM_LANDING_P5_1x1      = 216750496707;
var MB_COMM_LANDING_P5_300x250  = 252220566708;
var MB_COMM_LANDING_P5_728x90   = 742473156814;
