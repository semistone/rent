define([
  'jQuery',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './model.user'
  ], function($, _, Backbone, RENT, logger, UserModel) {
var DeviceCollection;
DeviceCollection = Backbone.Collection.extend({
	model: UserModel,
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/list_devices';
	}
	 
});
return DeviceCollection;
});