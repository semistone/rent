define([
  'jQuery',
  'Backbone',
  'RentCommon',
  './namespace.user'
], function($, Backbone, RENT) {
RENT.user.model.UserModel = Backbone.Model.extend({
	url : RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/',
	//
	// verify mobile auth code
	//
	verify_mobile_auth_code : function(auth_code, options) {
		options = $.extend(options, {
			url : this.url + 'verify_mobile_auth_code'
		});
		this.save('authCode', auth_code, options);
	},

	send_mobile_auth_message : function(options) {
		options = $.extend(options, {
			url : this.url + 'send_mobile_auth_message'
		});
		Backbone.sync("update",this, options);
	},
	name_device : function(name,options){
		this.set('name',name,{silent:true});
		options = $.extend(options, {
			url : this.url + 'name_device'
		});
		Backbone.sync("update",this, options);	
	}
});
return RENT.user.model.UserModel;
});