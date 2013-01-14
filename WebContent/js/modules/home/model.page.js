define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger'
], function($, Backbone, RENT, logger) {
var PageModel = Backbone.Model.extend({
	initialize:function(){
		this.urlRoot  = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/page/';
	}
});
return PageModel;
});