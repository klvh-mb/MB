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

var MB_MAG_1x1                  = 337876666690;
var MB_MAG_300x250              = 336814706671;
var MB_MAG_728x90               = 0;
var MB_MY_MAG_1x1               = 714880026672;
var MB_MY_MAG_300x250           = 785814486673;
var MB_MY_MAG_728x90            = 0;
var MB_HOME_1x1                 = 510902096674;
var MB_HOME_300x250             = 550424006675;
var MB_HOME_728x90              = 0;
var MB_ARTICLE_1x1              = 985037346676;
var MB_ARTICLE_300x250          = 244781106677;
var MB_ARTICLE_728x90           = 0;
var MB_ARTICLE_LANDING_1x1      = 691373536678;
var MB_ARTICLE_LANDING_300x250  = 247130236679;
var MB_ARTICLE_LANDING_728x90   = 0;
var MB_COMM_1x1                 = 512344316680;
var MB_COMM_300x250             = 586656756681;
var MB_COMM_728x90              = 0;
var MB_COMM_LANDING_1x1         = 516615626682;
var MB_COMM_LANDING_300x250     = 982948656683;
var MB_COMM_LANDING_728x90      = 0;
var MB_BCOMM_1x1                = 886585016684;
var MB_BCOMM_300x250            = 652876066685;
var MB_BCOMM_728x90             = 0;
var MB_BCOMM_LANDING_1x1        = 975430386686;
var MB_BCOMM_LANDING_300x250    = 382773116687;
var MB_BCOMM_LANDING_728x90     = 0;
var MB_OTHER_1x1                = 887185786688;
var MB_OTHER_300x250            = 263403256689;
var MB_OTHER_728x90             = 0;

var MB_COMM_P1_1x1              = 940313186737;
var MB_COMM_P1_300x250          = 995251896691;
var MB_COMM_P1_728x90           = 0;
var MB_COMM_LANDING_P1_1x1      = 469337956699;
var MB_COMM_LANDING_P1_300x250  = 772388026700;
var MB_COMM_LANDING_P1_728x90   = 0;
var MB_COMM_P2_1x1              = 376893136692;     // comm discovery
var MB_COMM_P2_300x250          = 894219356670;     // comm discovery
var MB_COMM_P2_728x90           = 0;
var MB_COMM_LANDING_P2_1x1      = 633668986701;
var MB_COMM_LANDING_P2_300x250  = 602161336702;
var MB_COMM_LANDING_P2_728x90   = 0;
var MB_COMM_P3_1x1              = 689189426693;
var MB_COMM_P3_300x250          = 418858546694;
var MB_COMM_P3_728x90           = 0;
var MB_COMM_LANDING_P3_1x1      = 339054986703;
var MB_COMM_LANDING_P3_300x250  = 487874436704;
var MB_COMM_LANDING_P3_728x90   = 0;
var MB_COMM_P4_1x1              = 722211466695;
var MB_COMM_P4_300x250          = 925733136696;
var MB_COMM_P4_728x90           = 0;
var MB_COMM_LANDING_P4_1x1      = 589191186705;
var MB_COMM_LANDING_P4_300x250  = 150519856706;
var MB_COMM_LANDING_P4_728x90   = 0;
var MB_COMM_P5_1x1              = 247393906697;
var MB_COMM_P5_300x250          = 842233366698;
var MB_COMM_P5_728x90           = 0;
var MB_COMM_LANDING_P5_1x1      = 216750496707;
var MB_COMM_LANDING_P5_300x250  = 252220566708;
var MB_COMM_LANDING_P5_728x90   = 0;
