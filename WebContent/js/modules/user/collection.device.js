define([
  'jQuery',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './model.device'
  ], function($, _, Backbone, RENT, logger, DeviceModel) {
var DeviceCollection;
DeviceCollection = Backbone.Collection.extend({
	model: DeviceModel,
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/device/list_devices';
	}
	 
});
return DeviceCollection;
});
