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

var $template = $('<div>').html(template);
RENT.backyard.view.MainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render','sso_request_form');
		this.tmpl = $template.find('#tmpl_backyard_menu').html();
	},
	events : {
		'click #show_token_link' : 'show_token',
		'change #set_sms_gateway_debug_mode' : 'set_sms_gateway_debug_mode',
		'click #list_users_link': 'list_users_link',
		'click #sso_request_form_link':'sso_request_form'
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
			el : '#backyard_right',
			collection : collection
		});
		collection.fetch();
	},
	sso_request_form:function(){
		logger.debug('click sso request form');
		new RENT.backyard.view.MobileAuthRequestView({
			el : '#backyard_right',
			model:this.model
		}).render();
	},
	render:function(){
		this.$el.html(this.tmpl);
	}
});

RENT.backyard.view.ShowUsersView= Backbone.View.extend({
	initialize : function() {
		logger.debug('initialize show users view');
		this.tmpl = $template.find('#tmpl_show_uses').html();
		_.bindAll(this, 'render');
		this.collection.on('reset',this.render);
	},
	render:function(){
		logger.debug("render users");
		var obj = {users:this.collection.toJSON() };
		this.$el.html(Mustache.to_html(this.tmpl,obj ));
	}
});
RENT.backyard.view.MobileAuthRequestView= Backbone.View.extend({
	events:{
		'click #sign_button':'fill_form',
		'click #go_button':'do_mobile_auth_request'
	},
	initialize : function() {
		logger.debug('initialize mobile auth request view');
		this.tmpl = $template.find('#tmpl_mobile_auth_request_form').html();
		_.bindAll(this, 'render');
		this.model.set({
			requestId:this.GUID(),
			requestTime:Math.ceil(new Date().getTime()/1000),
			done:window.location.href
		});
	},
	GUID :function(){
		var S4 = function ()
		{
	        return Math.floor(
	                Math.random() * 0x10000 /* 65536 */
	            ).toString(16);
	    };
	    return (
	            S4() + S4() + "-" +
	            S4() 
	        );
	},
	render:function(){
		logger.debug("render sso form");
		this.$el.html(Mustache.to_html(this.tmpl,this.model.toJSON()));	
		RENT.generateCountryOptions(this.$el.find('#countryCode'));
		var _this = this;
		this.model.list_sso_devices({
			success:function(model,response){
				logger.debug("success");
				var options = _this.$el.find('#requestFrom').prop('options');
				$.each(model, function(i, obj) {
					logger.debug("i is "+i+" val is "+obj.userId);
					options[options.length] = new Option(obj.userId, obj.userId);
				});
			},
			error:function(){
				logger.debug("success");				
			}
		});

		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#mobile_auth_request_form").validate();
		});
	},
	fill_form:function(){
		logger.debug('click do sign');
		var formvalidate = this.$el.find("#mobile_auth_request_form").valid();
		if (!formvalidate) {
			logger.error('form validate fail');
			return;
		}
		var formArray=this.$el.find("#mobile_auth_request_form").serializeArray();
		var formObj = {};
		$.each(formArray,function(i, item){
			formObj[item.name] = item.value;
		});
		var _this = this;
		this.model.get_signature_of_mobile_auth_request(formObj,{
			success:function(model, resp){
				logger.debug('success');
				_this.$el.find('#sign').val(model.sign);
			},
			error:function(model,resp){
				logger.debug('error');
				
			}
		});
	},
	do_mobile_auth_request:function(){
		logger.debug("do submit");
		this.$el.find("#mobile_auth_request_form").submit();
	}
});

return RENT.backyard.view.MainView;
});