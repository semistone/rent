RENT.user.model.UserModel = Backbone.Model.extend({
	url : '/rest/user/',
	verify_mobile_auth_code:function(auth_code){
		this.set('auth_code',auth_code,{silent: true});
		Backbone.sync('update',this,{url: this.url+'verify_mobile_auth_code'});
	}
});