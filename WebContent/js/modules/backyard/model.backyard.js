define([
  'jQuery',
  'Backbone',
  'RentCommon',
  './namespace.backyard'
], function($, Backbone, RENT) {
RENT.backyard.model.MainModel = Backbone.Model.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/admin/';
	},
	show_token:function(options){
		options = $.extend(options, {
			url : this.url + 'show_token'
		});
		Backbone.sync("fetch",this, options);
	}
});
return RENT.backyard.model.MainModel;
});