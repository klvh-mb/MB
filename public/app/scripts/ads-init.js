if (!window.af) {
    var af = (
        function() {
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