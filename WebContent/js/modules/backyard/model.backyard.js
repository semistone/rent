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
	},
	set_sms_gateway_debug_mode:function(mode, options){	
		if (mode == '') return; 
		options = $.extend(options, {
			url : this.url + 'sms_gateway_debug_mode/'+mode
		});
		Backbone.sync("update",this, options);
	}
});
RENT.backyard.collection.UserCollection = Backbone.Collection.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/admin/list_users';
	}	 
});

return RENT.backyard.model.MainModel;
});