define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger',
  './model.user',
  './namespace.user'
], function($, Backbone, RENT,logger, UserModel) {
var DeviceModel;
DeviceModel = Backbone.Model.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/device/';
		this.userModel = new UserModel();
	},
	new_device:function(options){
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

	verify_mobile_auth_request_code:function(auth_code,options){
		options = $.extend(options, {
			url : this.url + 'verify_mobile_auth_request_code'
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
	},
	mobile_auth_request:function(data,options){
		var field = ['requestId','mobilePhone','sign', 'forceReauth', 'authUserId',
		             'requestFrom', 'requsetTime', 'callback',
		             'requestTime', 'done', 'countryCode'];
		$.each(data,function(key,value){
			if ($.inArray(key,field) <0 ){
				logger.debug('delete key '+key);
				delete data[key];
			}
		});
		this.set(data,{silent:true});
		options = $.extend(options, {
			url : this.url + 'mobile_auth_request'
		});
		Backbone.sync("create",this, options);	
	},
	get_user:function(){
		this.userModel.set(this.get('user'), {silent:true});
		return this.userModel;
	},
	sign_off:function(options){
		options = $.extend(options, {
			url : this.url + 'sign_off'
		});
		Backbone.sync("fetch",this, options);	
	},
	get_roles:function(options){
		logger.debug('get user roles');
		var _this = this;
		var _options  = {
				url : this.url + 'get_roles',
				success:function(model,resp){
					logger.debug('get roles success');
					var user = _this.get('user');
					var roles = [];
					var i = 0;
					$.each(model,function(index, row){
						roles[i] = row.roleId;
						i++;//todo: change to push array.
					});
					logger.debug('add role '+roles);
					user.roles = roles;
					options['success'](model,resp);
				},
				error:function(model,resp){
					options['error'](model,resp);
				}	
		};
		Backbone.sync('fetch', this, _options);
	}
});
return DeviceModel;
});
