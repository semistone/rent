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
			url: this.url+'search',
			success:function(model,resp){
				_this.total = model.count;
				_this.reset(model.members);
			},
			error:function(resp,msg){
				_this.trigger('error', this, resp);
			},
			data: { name:name, limit:limit,offset:offset},
			processData:true
		};
		Backbone.sync("fetch",this, options);

	}
});
return MemberCollection;
});