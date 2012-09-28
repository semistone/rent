RENT.user.model.UserModel = Backbone.Model.extend({
	url : 'rest/user/',
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
	}
});