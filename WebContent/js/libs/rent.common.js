var logger = {
		debug: function(msg) {

		},
		error: function(msg) {

		},
		warn: function(msg) {

		},
		info: function(msg) {
			
		},
		fatal: function(msg) {
		
		},
		trace: function(msg){
		
		}
}; 
var RENT = {
    loadTemplate: function(templ_file_path, callback){
	    $.get(templ_file_path, function(data) {
	        $('body').append(data);
	            if(callback){
	                callback(); 
	            }
	    });
	},
	// called in LAB.js loader
	setDebugLogger:function(){	
		if (typeof log4javascript == "undefined") {			
			return;
		} else {
			logger = log4javascript.getDefaultLogger();
			// enabled or disabled all logging
			log4javascript.setEnabled(true);		
		}
	},
    // set language and resource file and css for locale
    setLangRes: function (lang, resFiles, extraPath,callback) { // extraPath maybe '../'
    	var extra_path = (extraPath ? extraPath : '');
    	if (callback == undefined) callback=function(){};
        $.i18n.properties({
            name: resFiles, //['xxx','yy']
            path: extra_path + 'i18n/',
            mode:'map', // "map" option is mandatory if your bundle keys contain Javascript Reserved Words, such as ' in string resource
            cache:true,
            language:lang,
            encoding: 'UTF-8', // Property file resource bundles are specified to be in ISO-8859-1 format. Defaults to UTF-8 for backward compatibility.
            callback: callback
        });     
    },
    bindLoadingPage:function(){
    	logger.debug('bind loading page to #supersized-loader');
    	$("#supersized-loader")
    	.ajaxStart(function(){
    		logger.debug("loading page show");
    		$(this).show();
    	})
    	.ajaxStop(function(){
    		logger.debug("loading page hide");
    	    $(this).hide();
    	});  
    },

	simpleDialog : function(title, msg) {
		var dialog = $('#error_dialog');
		dialog.text(msg);
		$('#error_dialog').dialog({
			title : title,
			buttons : [ {
				text : $.i18n.prop('general.OK'),
				click : function() {
					$(this).dialog("close");
				}
			} ],
			resize : false,
			modal : true
		});
	},
    simpleErrorDialog:function(resp,msg){
		var resp = $.parseJSON(resp.responseText);
		var title = $.i18n.prop('rent.error_msg.' + resp.errorCode);
		logger.error('ajax response error message:' + resp.errorMsg);
		this.simpleDialog(title, msg);
    },	
}
// initialize 
$(function(){
	RENT.bindLoadingPage();
});
