define([
  'jQuery',
  'logger',
  'jQueryUI'
], function($, logger) {

var RENT = {
    CONSTANTS:{
    	APIs_BASE_DIR: './'
    },
    loadTemplate: function(templ_file_path, callback){
	    $.get(templ_file_path, function(data) {
	        $('body').append(data);
	            if(callback){
	                callback(); 
	            }
	    });
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
    bindLoadingPage:function(loading_id){
    	logger.debug('bind loading page to #supersized-loader');
    	$(loading_id)
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
    getLang:function(){
    	//
    	// from url, cookie, user preference,browser default.
    	//
		var pattern = /lang=([^&]*)/;
		var match=pattern.exec(window.location.href);
		if (match) {
			var language = match[1];
	    	logger.debug('get lang from href, lang is '+language);
			return language;
		}
		
    	var language = window.navigator.userLanguage || window.navigator.language;
    	logger.debug('get lang from browser default setting, lang is '+language);
    	return language;
    }
}
$(function(){
	RENT.bindLoadingPage("#supersized-loader");
});
return RENT;
});
