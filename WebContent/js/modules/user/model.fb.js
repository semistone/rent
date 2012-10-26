define([
  'Facebook',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function(FB, _, Backbone, RENT, logger) {
RENT.user.model.FBModel = Backbone.Model.extend({
	testAPI:function() {
		var _this = this;
		FB.api('/me', function(response) {
			if (_this.get('id') != response.id) {
				logger.error('id not match');
			} else {
				logger.debug('set fb response');
				_this.set(response);				
			}
		});
	},
	login: function() {
		var _this = this;
		FB.login(function(response) {
			if (response.authResponse) {
				_this.link_user_to_facebook();
				_this.testAPI();
				// connected
			} else {
				// cancelled
			}
		});
	},
	link_user_to_facebook: function(){
		logger.debug('link user to facebook');
	},
	check_status:function(){
		var _this = this;
		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				// connected
				_this.testAPI();
			}else {
				logger.debug('fb not connected');
			}
		});
	},
	initialize : function() {
		var _this = this;
		_.bindAll(this, 'login','testAPI','check_status');
		window.fbAsyncInit = function() {
			FB.init({
				appId : '362616447158349', // App ID
				channelUrl : 'http://angus-ec2.siraya.net/facebook.html', // Channel File
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