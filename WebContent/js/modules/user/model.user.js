RENT.user.model.UserModel = Backbone.Model.extend({
	url : '/rest/user/',
	verify_mobile_auth_code:function(auth_code,options){
		options = $.extend(options,{
			url: this.url+'verify_mobile_auth_code',
			silent: true
		});
		this.sync({
			'authCode' : auth_code
		}, options);
	}
});