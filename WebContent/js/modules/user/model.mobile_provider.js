define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function($, Backbone, RENT,logger) {
	
RENT.user.model.MobileProviderModel = Backbone.Model.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/';
	},
	new_mobile_provider: function(user, password,options){
		this.set({user: user, password:password}, {silent:true});
		options = $.extend(options, {
			url : this.url + 'new_mobile_provider'
		});
		Backbone.sync("update",this, options);	
	}	
});
	
});