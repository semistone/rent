define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.register.html',
  './namespace.user',
  './model.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);
//
// Register main view
//
RENT.user.view.RegisterMainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render','sign_off','show_my_device','link_fb');
		this.tmpl = $template.find('#tmpl_register_step3').html();
		this.rightView = new Backbone.View();
		var _this = this;		
		//
		// add fb module
		//
		if (this.model.get('user').loginType == 'FB') {
			var id = null;
			id = this.model.get('user').loginId;
			logger.debug('login id is '+id);
			require(['modules/user/model.fb'],function(){
				var fb = new RENT.user.model.FBModel({id:id});
				_this.fb = fb;
				RENT.user.navBar.initFBModel(fb);
			});
		}
		//
		// get roles
		//
		this.model.get_roles({
			success:function(model,resp){
				logger.debug('get roles success');
				var user = _this.model.get('user');
				var roles = [];
				var i = 0;
				$.each(model,function(index, row){
					roles[i] = row.roleId;
					i++;//todo: change to push array.
				});
				logger.debug('add role '+roles)
				user.roles = roles;
				_this.render();
			},
			error:function(model,resp){
				logger.debug('get roles error');
				RENT.simpleErrorDialog(resp,'');
			}
		});
	},
	events : {
		"click #named_my_devices_link" : 'name_device_popup',
		'click #sign_off_link' : 'sign_off',
		'click #show_my_devices_link' : 'show_my_device',
		'click #link_to_fb_link' : 'link_fb',
		'click #show_sso_token_link':'show_sso_token'
	},
	render:function(){
		this.model.trigger('change_view','main');
		var device = this.model.toJSON();
		if (device.user.loginType == 'FB') {
			logger.debug('login type is fb');
			device.user.is_fb = true;			
		}
		if (device.user.roles && _.contains(device.user.roles,5)) {
			logger.debug('sso role exist');
			device.user.is_sso = true;
		}
		this.$el.html(Mustache.to_html(this.tmpl, device));
		//
		// i18n
		//
		this.$el.find('#i18n_auth_success').text(
				$.i18n.prop('user.register.auth_success'));
		this.$el.find('#i18n_show_my_devices').text(
				$.i18n.prop('user.register.show_my_devices'));		
		this.$el.find('#i18n_named_my_devices').text(
				$.i18n.prop('user.register.named_my_devices'));	
		this.$el.find('#i18n_delete_device').text(
				$.i18n.prop('user.register.delete_device'));
		this.$el.find('#i18n_link_fb').text(
				$.i18n.prop('user.register.link_fb'));
		this.$el.find('#i18n_show_sso_token').text(
				$.i18n.prop('user.main.show_sso_token'));
		

	},
	name_device_popup:function(){
		logger.debug('click name device popup');
		this.rightView.undelegateEvents();
		this.rightView = new RENT.user.view.NameDeviceView({
			el : this.$el.find('#register_right'),
			model : this.model});
		this.rightView.render();
		var _this = this;
		this.rightView.on('success',function(){
			_this.show_my_device();
		});
	},
	link_fb : function(){
		logger.debug('link to fb');
		var _this = this;
		if (this.fb == undefined) {
			logger.error('fb object not exist');
			RENT.simpleDialog($.i18n.prop('user.main.fb_load_fail'), '');
			return;
		}
		var do_link_fb = function(){
			_this.model.link_facebook(_this.fb.get('id'), {
				success:function(){
					logger.debug('link fb success');	
					_this.$el.find('#link_to_fb_link').hide();
				},
				error:function(){
					logger.debug('link fb fail');
				}
			});
		};
		//
		// do login 
		//
		_this.fb.login({
			success: do_link_fb
		});
	},
	sign_off:function(){
		logger.debug('click signoff device');
		var _this = this;
		this.model.sign_off({
			success:function(model,resp){
				_this.rightView.undelegateEvents();
				_this.undelegateEvents();
				new RENT.user.view.RegisterStep1View({el:_this.el,model:_this.model}).render();
				logger.debug('trigger sign off event on '+_this.model.cid);
				_this.model.trigger('sign_off');
			},
			error:function(model,resp){
				RENT.simpleErrorDialog(resp,'');
			}
		});	
	},
	show_my_device:function(){
		logger.debug('click show my devies'); 
		this.rightView.undelegateEvents();
		this.rightView = new RENT.user.view.ShowDevicesView({
			el : this.$el.find('#register_right'),
			model : this.model
		});
	},
	show_sso_token:function(){
		logger.debug('click show my token'); 
		this.rightView.undelegateEvents();
		var _this = this;
		require(['modules/user/view.sso'],function(){
			_this.rightView = new RENT.user.view.ShowSSOTokenView({
				el : _this.$el.find('#register_right'),
				model : _this.model
			});					
		});
	}
});
//
//device list
//
RENT.user.view.ShowDevicesView = Backbone.View.extend({
	events:{
		'click .delete_device_link': 'delete_device',
		'click .show_sessions_link': 'list_sessions'
	},
	initialize : function() {
		logger.debug('initialize show devices view');
		this.tmpl = $template.find('#tmpl_show_devices').html();
		_.bindAll(this, 'render','delete_device','list_sessions');
		if (this.collection == null) {
			this.collection =new RENT.user.collection.UserCollection();
			this.collection.fetch();
		}
		this.collection.on('reset',this.render);
		this.collection.on('remove',this.render);
	},
	render:function(){
		this.model.trigger('change_view','show_devices');
		logger.debug("render devices");
		var obj = {devices:this.collection.toJSON() };
		this.$el.html(Mustache.to_html(this.tmpl,obj ));
		//
		// i18n
		//
		this.$el.find('#i18n_devices').text(
				$.i18n.prop('user.register.show_devices'));
		this.$el.find('.i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('.i18n_id').text(
				$.i18n.prop('general.id'));
		this.$el.find('#i18n_last_login_ip').text(
				$.i18n.prop('user.register.last_login_ip'));
		this.$el.find('#i18n_last_login_time').text(
				$.i18n.prop('user.register.last_login_time'));
		
		
	},
	list_sessions:function(ev){
		var deviceId = $(ev.target).parent().attr('id');
		logger.debug("list sessions device id "+deviceId); 
		this.undelegateEvents();
		this.model.set({deviceId:deviceId},{silent:true});
		new RENT.user.view.ShowSessionsView({
			el : this.$el,
			model : this.model
		});
	},
	delete_device:function(ev){
		var id = $(ev.target).parent().parent().attr('id');
		logger.debug("delete device id "+id);
		var _this = this;
		var success =function(model){
			logger.debug("delete success");
			var model = _this.collection.get(id);
			_this.collection.remove(model);
			
		};
		var error = function(model,resp){
			RENT.simpleErrorDialog(resp);
		};
		this.model.delete_device(id,{
			success:success,
			error:error
		});
	}
});
//
//session list
//
RENT.user.view.ShowSessionsView = Backbone.View.extend({
	initialize : function() {
		logger.debug('initialize show sessions view');
		this.tmpl = $template.find('#tmpl_show_sessions').html();
		_.bindAll(this, 'render');
		if (this.collection == null) {
			this.collection =new RENT.user.collection.SessionCollection();
			var limit = 10;
			var offset = 0;
			var deviceId = this.model.get('deviceId');
			options = {
				data: $.param({ deviceId:deviceId,limit: limit, offset:offset}),
				error:function(model,resp){
					RENT.simpleErrorDialog(resp);
				}
			};
			this.collection.fetch(options);
		}
		this.collection.on('reset',this.render);
		this.collection.on('remove',this.render);
	},
	render:function(){
		this.model.trigger('change_view','show_sessions');
		logger.debug("render sessions");
		var sessions = this.collection.toJSON();
		$.each(sessions,function(index, row){
     	logger.debug('created is '+row.created);
     	var date = new Date(row.created * 1000);
     	var then = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDay();
             then += ' '+date.getHours()+':'+date.getMinutes();
         row['createdDate'] = then;
     });    
     this.$el.html(Mustache.to_html(this.tmpl,{sessions:sessions} ));
     //
     // i18n
 	//
     this.$el.find('.i18n_created').text(
 				$.i18n.prop('general.created'));
     this.$el.find('#i18n_last_login_ip').text(
 				$.i18n.prop('user.register.last_login_ip'));
     this.$el.find('#i18n_sessions').text(
 				$.i18n.prop('user.register.show_sessions'));
	}
});

});