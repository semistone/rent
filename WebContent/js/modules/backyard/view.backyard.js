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
  'text!../../../html/backyard/tmpl.backyard.html',
  './namespace.backyard'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {


RENT.backyard.view.MainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render');
	},
	events : {
		'click #show_token_link' : 'show_token'
	},
	show_token:function(){
		logger.debug('click show token');
		this.model.show_token({
			success:function(model,resp){
				RENT.simpleDialog('','your token is '+model.token);			
			},
			error:function(){
				RENT.simpleDialog('','some error happen');
			}
		});
	},

	render:function(){
		this.tmpl = $('div').append(template).find('#tmpl_backyard_menu').html();
		this.$el.html(this.tmpl);
	}
});
return RENT.backyard.view.MainView;
});