define([
  'jQuery',
  'Backbone',
  'RentCommon',
  './namespace.user'
], function($, Backbone, RENT) {
RENT.user.model.UserModel = Backbone.Model.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/';
	},
	new_device:function(data,options){
		this.set(data,{silent:true});
		Backbone.sync("create",this, options);	
	},
	//
	// verify mobile auth code
	//
	verify_mobile_auth_code : function(auth_code, options) {
		options = $.extend(options, {
			url : this.url + 'verify_mobile_auth_code'
		});
		this.set('authCode', auth_code, {silent:true});
		Backbone.sync("update",this, options);
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
	},
	
	delete_device : function(id, options){
		var url = this.url;
		if (id != null) {
			url = url+'?deviceId='+id;
		}
		options = $.extend(options, {
			url : url});
		Backbone.sync("delete",this, options);	
	}
	
});

RENT.user.collection.UserCollection = Backbone.Collection.extend({
	model: RENT.user.model.UserModel,
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/list_devices';
	}
	 
});

return RENT.user.model.UserModel;
});