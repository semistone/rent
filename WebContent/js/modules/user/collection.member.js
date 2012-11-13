define([
  'jQuery',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
], function($, _, Backbone,RENT, logger) {
MemberCollection = Backbone.Collection.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/member/';
	},
	search:function(name,limit, offset){
		logger.debug('search name:'+name+' limit:'+limit+' offset:'+offset);
		var _this = this;
		options = {
			url : this.url + 'search?name='+name+'&limit='+limit+'&offset='+offset,
			success:function(model,resp){
				_this.reset(model.members);
			}
			
		};
		Backbone.sync("fetch",this, options);	
	}
});
return MemberCollection;
});