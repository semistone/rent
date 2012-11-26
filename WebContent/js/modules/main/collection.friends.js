define([
  'jQuery',
  'Facebook',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger'
  ], function($,FB, _, Backbone, RENT, logger) {
var FriendCollection = Backbone.Collection.extend({
	initialize:function(){
		this.url = RENT.CONSTANTS.APIs_BASE_DIR + 'rest/user/';
	},
	fetch:function(){
		if (this.length != 0) {
			logger.debug('friends already exist');
			this.trigger('reset');
			return;
		}
		var _this = this;
		FB.api('/me/friends', function(response) {
			logger.debug('get friends');
			if (response.error != undefined) {
				logger.error('fetch friend error');
				_this.trigger('error');
				return;
			}
			_this.reset(response.data);
		});				
	},
	create_members: function(options){		
		options = $.extend(options, {
			url : this.url + 'create_members_from_fb'
		});
		Backbone.sync("create",this, options);		
	}
});
return FriendCollection;
});