define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.register.html'
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
		var _this = this;
		requre(['modules/user/view.name_device'],function(){
			this.rightView = new RENT.user.view.NameDeviceView({
				el : _this.$el.find('#register_right'),
				model : _this.model});
			_this.rightView.render();
			this.rightView.on('success',function(){
				_this.show_my_device();
			});			
		});
	},
	link_fb : function(){
		logger.debug('link to fb');
		var _this = this;
		if (this.fb == undefined) {
			require(['modules/user/model.fb'],function(){
				var fb = new RENT.user.model.FBModel({id:id});
				_this.fb = fb;
			});
		}
		var do_link_fb = function(){
			_this.model.link_facebook(_this.fb.get('id'), {
				success:function(model,resp){
					logger.debug('link fb success');	
					_this.$el.find('#link_to_fb_link').hide();
				},
				error:function(model,resp){
					logger.debug('link fb fail');
					RENT.simpleErrorDialog(resp,'');
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
		var _this = this;
		require(['modules/user/view.devices'],function(){
			_this.rightView = new RENT.user.view.ShowDevicesView({
				el : _this.$el.find('#register_right'),
				model : _this.model
			});			
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

});