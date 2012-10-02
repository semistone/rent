$LAB.setOptions({BasePath:"js/"})
    .script(function(){
		var debug = /debug=1/;
		if (debug.exec(window.location.href)) {
			return "libs/log4javascript.js";
		}
    })
    .script(
        "//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js",
        "libs/jquery.i18n.properties-min.js",
        "libs/rent.common.js",
        "modules/user/namespace.user.js"
    ).wait(function(){
    		RENT.setDebugLogger();
        	RENT.setLangRes(RENT.getLang(), ['rent_user']);
    		$(document).ready(function(){
            });
        }
    ).script(
        "libs/mustache-min.js",
        "libs/underscore.js",
    	"libs/backbone.js",
    	'libs/jquery.validate.js',
    	"modules/user/model.user.js",
		"modules/user/view.user.js"
    ).wait(function(){ 
        $(document).ready(function(){
            RENT.loadTemplate("html/user/tmpl.register.html",
                function(){
            	    logger.debug('new register view');
            	    var model = new RENT.user.model.UserModel({});
                    var register = new RENT.user.view.RegisterView({el:'#main',model:model}); 
            });
        });
    }).script(
        "//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"
    );
