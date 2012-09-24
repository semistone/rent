$LAB.setOptions({BasePath:"../js/"})
    .script(function(){
		var debug = /debug=1/;
		if (debug.exec(window.location.href)) {
			return "libs/log4javascript.js";
		}
    })
    .script(
        "libs/jquery-1.5.1.min.js",
        "libs/jquery.i18n.properties-min.js",
        "libs/rent.common.js",
        "modules/user/namespace.user.js"
    ).wait(function(){
    		RENT.setDebugLogger();
        	RENT.setLangRes('en-US', ['rent_user']);
    		$(document).ready(function(){
            });
        }
    ).script(
        "libs/mustache-min.js",
        "libs/underscore-min.js",
    	"libs/backbone.js",
    	"modules/user/model.user.js",
		"modules/user/view.user.js"
    ).wait(function(){ 
        $(document).ready(function(){
            RENT.loadTemplate("../user/tmpl.register.html",
                function(){
            	    logger.debug('new register view');
            	    var model = new RENT.user.model.UserModel({});
                    var register = new RENT.user.view.RegisterView({el:'#main',model:model}); 
            });
        });
    });
