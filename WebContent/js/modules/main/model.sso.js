define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger'
], function($, Backbone, RENT, logger) {
	
var RequestModel = Backbone.Model.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/';
	},
	get_signature_of_mobile_auth_request:function(options){
		this.unset('debug',{silent:true});
		options = $.extend(options, {
			url : this.url + 'get_signature_of_mobile_auth_request'
		});
		Backbone.sync("create",this, options);		
	},
	
	get_sso_application_token:function(options){
		logger.debug('get user roles');
		options = $.extend(options, {
			url : this.url + 'get_sso_application_token'
		});
		Backbone.sync('fetch',this, options);		
	},
	apply_sso_application:function(options){
		logger.debug('link facebook');
		options = $.extend(options, {
			url : this.url + 'apply_sso_application'
		});
		Backbone.sync('update',this, options);		
	}
});
return RequestModel;
});