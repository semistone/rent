define([
  'jQuery',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger'
  ], function($, _, Backbone, RENT, logger) {
var SessionCollection;
SessionCollection = Backbone.Collection.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/list_sessions';
	}
	 
});
return SessionCollection;
});
