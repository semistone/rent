define([
  'Facebook',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function(FB, _, Backbone, RENT, logger) {
RENT.user.model.FBModel = Backbone.Model.extend({
	setResponse:function() {
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
		FB.login(function(response) {
			if (_this.get('name') != null) {
				logger.debug('already logined');
				_this.set({connected:true},{silent:true});
				_this.trigger('login_success');	
				if (options != null) options.success();
				return;
			}
			if (response.authResponse) {
				logger.debug('login success');
				_this.set({connected:true},{silent:true});
				if (options != null) options.success();
				_this.setResponse();
				_this.trigger('login_success');
				// connected
			} else {
				// cancelled
			}
		});
	},

	check_status:function(){
		var _this = this;
		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				// connected
				logger.debug('fb connected');
				_this.set({connected:true},{silent:true});
				_this.setResponse();
			}else {
				logger.debug('fb not connected');
				_this.trigger('change');
			}
		});
	},
	initialize : function() {
		var _this = this;
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