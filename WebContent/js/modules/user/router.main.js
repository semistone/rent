define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function($, Backbone, RENT,logger) {

RENT.user.MainRouter = Backbone.Router.extend({
	routes: {
		'name_device': 'name_device',
		'show_my_device': 'show_my_device',
		'import_fb_friends' : 'import_fb_friends',
		'mobile_provider' : 'mobile_provider',
		'show_user_profile' : 'show_user_profile',
		'sso_application' : 'sso_application',
		'list_members' : 'list_members'
	}
});
});