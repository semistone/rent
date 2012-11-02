define([
  'Facebook',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger',
  './namespace.user'
], function(FB, _, Backbone, RENT, logger) {
RENT.user.collection.FriendCollection = Backbone.Collection.extend({
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
	}
});
});