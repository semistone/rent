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
		'mobile_provider' : 'mobile_provider'
	}
});
});