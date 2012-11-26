define([
  'order!jQuery',
  'order!logger',
  'order!Mustache',
  'order!i18N'
], function($, logger,Mustache) {
var RENT = {
	CONSTANTS:{
		APIs_BASE_DIR: './',
		AJAX_TIMEOUT : 3000,
		COUNTRIES: {
			"886": {name:"general.taiwan",locale:'zh-TW', country:"TW"},
			"86": {name:"general.china", locale:'zh-CN', country:"CN"},
			"81" : {name:"general.japan",locale:'ja', country:"JA"},
			"1"  : {name:"general.unit_state", locale:'en-US', country:"US"}
		},
		DOCUMENT_ROOT : '',
		FACEBOOK_APP: '362616447158349',
		FACEBOOK_CHANNEL:'http://angus-ec2.siraya.net/facebook.html', // Channel File
	},

    // set language and resource file and css for locale
	setLangRes: function (lang, resFiles, extraPath,callback) { // extraPath maybe '../'
		var extra_path = RENT.CONSTANTS.DOCUMENT_ROOT;
    	if (callback == undefined) callback=function(){};
    	$.i18n.properties({
            name: resFiles, //['xxx','yy']
            path: extra_path + '/i18n/',
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
    	
    	$.ajaxSetup({timeout: RENT.CONSTANTS.AJAX_TIMEOUT});
    },

    simpleDialog : function(title, msg) {
		require(['Mustache','text!../html/general/tmpl.general.html','Bootstrap'],function(Mustache, template){
			var $template = $('<div>').html(template);
			var dialog= Mustache.to_html($template.find('#tmpl_simple_dialog').html(), {
				title:title,msg:msg});
			$('#dialog').html(dialog);
			$('#error_dialog').modal('show');			
		});
	},
	simpleErrorDialog:function(resp,msg){
		var title;
		if (resp == null || resp == 'error') {
			title = $.i18n.prop("rent.error_msg.server");
		} else {
			try{
				resp = $.parseJSON(resp.responseText);	
				title = $.i18n.prop('rent.error_msg.' + resp.errorCode);
				logger.error('ajax response error message:' + resp.errorMsg);
			}catch(e){
				title = $.i18n.prop("rent.error_msg.server");				
			}				
		}
		RENT.simpleDialog(title, msg);
	},
	simpleErrorDialogForCollectionError:function(collection, resp, options){
		RENT.simpleErrorDialog(resp,'');
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
		var i = language.indexOf('-');
		language=language.substring(0,i)+'-'+language.substring(i+1).toUpperCase();
		logger.debug('get lang from browser default setting, lang is '+language);
		return language;
    },
    
    getQueryVariables:function(){
        var query = window.location.search.substring(1);
        var vars = query.split('&');
        var form = {};
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split('=');
            form[decodeURIComponent(pair[0])] = decodeURIComponent((pair[1]+'').replace(/\+/g, '%20'));
        }
        return form;
    },
    initValidator:function(callback){
    	require([ 'Validator'],function(){    		
        	$.validator.defaults.errorClass="alert";
        	$.validator.addMethod("regex", function(value, element, re) {
        		return re.test(value);
        	}, $.i18n.prop('rent.error.validate_format'));
    		callback();
    	});
    },
    generateCountryOptions:function(selectElement){
    	var options = selectElement.prop('options');
    	if (options.length > 0) {
    		return;
    	}
		$.each(this.CONSTANTS.COUNTRIES , function(key, value) {
			options[options.length] = new Option($.i18n.prop(value.name),key);
		});
    }
    
};
window.RENT = RENT;
return RENT;
});
