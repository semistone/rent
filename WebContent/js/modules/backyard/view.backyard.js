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
  'modules/backyard/namespace.backyard'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

$('body').append(template);	
RENT.bindLoadingPage("#supersized-loader");

RENT.backyard.view.MainView = Backbone.View.extend({
	events : {
		"click #show_token_link" : "show_token"
	},
	show_token:function(){
		logger.debug('click show token');
		this.model.show_token({
			success:function(model,resp){
				RENT.simpleDialog('','your token is '+model.token);			
			},
			error:function(){
				alert('error');
			}
		});
	}
});
return RENT.backyard.view.MainView;
});