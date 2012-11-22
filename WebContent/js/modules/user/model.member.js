define([
  'jQuery',
  'Backbone',
  'RentCommon',
  'logger'
], function($, Backbone, RENT,logger) {
var	MemberModel = Backbone.Model.extend({
	initialize:function(){
		this.urlRoot  = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/member/';
	}		
});
return MemberModel;
});