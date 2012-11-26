define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger'
], function($, Backbone, RENT,logger) {
	
var MobileProviderModel = Backbone.Model.extend({
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
return MobileProviderModel;
});