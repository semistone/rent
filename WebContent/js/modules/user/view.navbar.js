//
// step1 view
//
define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.register.html',
  './namespace.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

RENT.user.view.NavBarView = Backbone.View.extend({
	initialize : function() {
		var _this = this;
		this.model.on('sign_off',function(){
			logger.debug('navbar sign off');
			_this.$el.find('#user_info').empty();
		});
	}
});
});