define([
  'Facebook',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function(FB, Backbone, RENT, logger) {
RENT.user.model.FBModel = Backbone.Model.extend({
	testAPI:function() {
		console.log('Welcome!  Fetching your information.... ');
		FB.api('/me', function(response) {
			console.log('Good to see you, ' + response.name + '.');
		});
	},
	login: function() {
		FB.login(function(response) {
			if (response.authResponse) {
				testAPI();
				// connected
			} else {
				// cancelled
			}
		});
	},
	initialize : function() {
		var _this = this;
		_.bindAll(this, 'login','testAPI');
		window.fbAsyncInit = function() {
			FB.init({
				appId : '362616447158349', // App ID
				channelUrl : 'http://angus-ec2.siraya.net/facebook.html', // Channel File
				status : true, // check login status
				cookie : true, // enable cookies to allow the server to access the session
				xfbml : true
			// parse XFBML
			});
			FB.getLoginStatus(function(response) {
				if (response.status === 'connected') {
					// connected
					alert('connected');
					_this.testAPI();
				} else if (response.status === 'not_authorized') {
					// not_authorized
					_this.login();
				} else {
					// not_logged_in
					_this.login();
				}
			});
		}
	}
});
});