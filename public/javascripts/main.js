var cookies = (function(){
    var setCookie = function setCookie(cname, cvalue, exminites) {
        var d = new Date();
        d.setTime(d.getTime() + (exminites*60*1000));
        var expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

    var getCookie = function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for(var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }

    return{
        setCookie: setCookie,
        getCookie: getCookie
    }
})();

