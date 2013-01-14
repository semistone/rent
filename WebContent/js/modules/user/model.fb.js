define([
  'Facebook',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function(FB, _, Backbone, RENT, logger) {
<<<<<<< HEAD
RENT.user.model.FBModel = Backbone.Model.extend({
	setResponse:function() {
		var _this = this;
		FB.api('/me', function(response) {
			_this.set({matchUser:_this.get('id') == response.id},{silent:true});
			logger.debug('set fb response');
			_this.set(response);				

		});
	},
	login: function(options) {
		var _this = this;
		FB.login(function(response) {
			if (_this.get('name') != null) {
				logger.debug('already logined');
				_this.trigger('login_success');				
=======
var FBModel = null;
FBModel = Backbone.Model.extend({
	is_init : false,
	set_response:function() {
		logger.debug('set fb response');
		var _this = this;
		FB.api('/me', function(response) {
			_this.set({matchUser:_this.get('id') == response.id},{silent:true});
			logger.debug('set fb response is match:' + _this.get('matchUser'));
			_this.set(response);
		});
	},
	logout:function(callback){
		logger.debug('logout fb');
		if (this.get('connected')) {
			FB.logout(function(response) {
				if (callback != undefined) callback();
			});			
		}else{
			logger.debug('logout but not connected');
		}
	},
	login: function(options) {
		var _this = this;
		logger.debug('fb login');
		FB.login(function(response) {
			if (_this.get('name') != null) {
				logger.debug('already logined');
				_this.set({connected:true},{silent:true});
				_this.trigger('login_success');	
>>>>>>> master
				if (options != null) options.success();
				return;
			}
			if (response.authResponse) {
				logger.debug('login success');
<<<<<<< HEAD
				if (options != null) options.success();
				_this.setResponse();
=======
				_this.set({connected:true},{silent:true});
				if (options != null) options.success();
				_this.set_response();
>>>>>>> master
				_this.trigger('login_success');
				// connected
			} else {
				// cancelled
			}
		});
	},

	check_status:function(){
		var _this = this;
<<<<<<< HEAD
		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				// connected
				_this.setResponse();
			}else {
				logger.debug('fb not connected');
				_this.trigger('change');
=======
		logger.debug('fb check status');
		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				// connected
				_this.set({connected:true},{silent:true});
				_this.set_response();
				logger.debug('trigger connected');
				_this.trigger('connected');
			}else {
				logger.debug('trigger not connected');
				_this.trigger('not_connected');
>>>>>>> master
			}
		});
	},
	initialize : function() {
		var _this = this;
<<<<<<< HEAD
		_.bindAll(this, 'login','setResponse','check_status');
		window.fbAsyncInit = function() {
			FB.init({
				appId : RENT.CONSTANTS.FACEBOOK_APP, // App ID
				channelUrl : RENT.CONSTANTS.FACEBOOK_CHANNEL,
				status : true, // check login status
				cookie : true, // enable cookies to allow the server to access the session
				xfbml : true
			// parse XFBML
			});
			_this.check_status();
		}
		this.check_status();
	}
});
});
=======
		_.bindAll(this, 'login','set_response','check_status');
		window.fbAsyncInit = function() {
			FBModel.is_init = true;
			_this.init();
			_this.check_status();
		};
		//
		// if fb already init, then must call check_status by ourself.
		//
		if (FBModel.is_init == true) {
			this.check_status();
		}
	},
	init: function(){
		FB.init({
			appId : RENT.CONSTANTS.FACEBOOK_APP, // App ID
			channelUrl : RENT.CONSTANTS.FACEBOOK_CHANNEL,
			status : true, // check login status
			cookie : true, // enable cookies to allow the server to access the session
			xfbml : true
		// parse XFBML
		});
	}
});
return FBModel;
});
>>>>>>> master
