define([
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function(_, Backbone, RENT, logger) {
	
var UserModel;
UserModel = Backbone.Model.extend({	
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/';
	},
	link_facebook:function(id, name,options){
		logger.debug('link facebook');
		this.set({
			loginType: 'FB',
			loginId:id,
			name:name
		},{slient:true});
		options = $.extend(options, {
			url : this.url + 'link_facebook'
		});
		Backbone.sync("update",this, options);		
	}
});
return 	UserModel;
});