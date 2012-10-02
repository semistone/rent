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
        "modules/backyard/namespace.backyard.js"
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
    	"modules/backyard/model.backyard.js",
		"modules/backyard/view.backyard.js"
    ).wait(function(){ 
        $(document).ready(function(){
            RENT.loadTemplate("html/backyard/tmpl.backyard.html",
                function(){
            	    logger.debug('new backyard view');
            	    var model = new RENT.backyard.model.MainModel({});
                    var register = new RENT.backyard.view.MainView({el:'#main',model:model}); 
            });
        });
    }).script(
        "//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"
    );
