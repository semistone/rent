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

var template = $('<div>').html(template);
RENT.backyard.view.MainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render');
	},
	events : {
		'click #show_token_link' : 'show_token',
		'change #set_sms_gateway_debug_mode' : 'set_sms_gateway_debug_mode',
		'click #list_users_link': 'list_users_link'
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

	set_sms_gateway_debug_mode:function(){
		var mode = this.$el.find('#set_sms_gateway_debug_mode').val();
		logger.debug('set sms gateway debug mode:' +mode);
		this.model.set_sms_gateway_debug_mode(mode,
		{
			success:function(model,resp){
				RENT.simpleDialog('','your debug mode is '+model.mode);			
			},
			error:function(){
				RENT.simpleDialog('','some error happen');
			}
		});		
	},
	list_users_link:function(){
		logger.debug('click list users link');
		var collection = new RENT.backyard.collection.UserCollection();
		new RENT.backyard.view.ShowUsersView({
			el : this.el,
			collection : collection
		});
		collection.fetch();
	},
	render:function(){
		this.tmpl = template.find('#tmpl_backyard_menu').html();
		this.$el.html(this.tmpl);
	}
});

RENT.backyard.view.ShowUsersView= Backbone.View.extend({
	initialize : function() {
		logger.debug('initialize show users view');
		this.tmpl = $('#tmpl_show_uses').html();
		_.bindAll(this, 'render');
		this.collection.on('reset',this.render);
	},
	render:function(){
		logger.debug("render users");
		var obj = {users:this.collection.toJSON() };
		this.$el.html(Mustache.to_html(this.tmpl,obj ));
	}
});
return RENT.backyard.view.MainView;
});