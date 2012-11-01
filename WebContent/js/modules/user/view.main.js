define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.main.html'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);
//
// Register main view
//
RENT.user.view.RegisterMainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render','sign_off','show_my_device','link_fb','sso_application');
		this.tmpl = $template.find('#tmpl_register_step3').html();
		this.rightView = new Backbone.View();
		var _this = this;		
		//
		// add fb module
		//
		var user = this.model.get('user');
		if (user != undefined && user.loginType == 'FB') {
			var id = null;
			id = this.model.get('user').loginId;
			logger.debug('loginType is FB and user.loginId is '+id);
			require(['modules/user/model.fb'],function(){
				var fb = new RENT.user.model.FBModel({id:id});
				_this.fb = fb;
				RENT.user.navBar.initFBModel(fb);
			});
		}
	},
	events : {
		"click #named_my_devices_link" : 'name_device_popup',
		'click #sign_off_link' : 'sign_off',
		'click #show_my_devices_link' : 'show_my_device',
		'click #link_to_fb_link' : 'link_fb',
		'click #sso_application_link':'sso_application',
		'click #import_fb_friends_link' : 'import_fb_friends'
	},
	render:function(){
		this.model.trigger('change_view','main');
		var device = this.model.toJSON();
		if (device.user.loginType == 'FB') {
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
		this.$el.find('#i18n_sso_application').text(
				$.i18n.prop('user.main.sso_application'));
		this.$el.find('#i18n_import_fb_friends').text(
				$.i18n.prop('user.main.import_fb_friends'));
		
	},
	name_device_popup:function(){
		logger.debug('click name device popup');
		this.rightView.undelegateEvents();
		var _this = this;
		require(['modules/user/view.name_device'],function(){
			_this.rightView = new RENT.user.view.NameDeviceView({
				el : _this.$el.find('#register_right'),
				model : _this.model});
			_this.rightView.render();
			_this.rightView.on('success',function(){
				_this.show_my_device();
			});			
		});
	},
	link_fb : function(){
		logger.debug('link to fb');
		var _this = this;

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
		if (this.fb == undefined) {
			require(['modules/user/model.fb'],function(){
				var fb = new RENT.user.model.FBModel({id:id});
				_this.fb = fb;
				//
				// do login 
				//
				_this.fb.login({
					success: do_link_fb
				});
			});
		}

	},
	sign_off:function(){
		logger.debug('click signoff device');
		var _this = this;
		this.model.sign_off({
			success:function(model,resp){
				_this.rightView.undelegateEvents();
				_this.undelegateEvents();
				require(['modules/user/view.step1'],function(){
					new RENT.user.view.RegisterStep1View({el:_this.el,model:_this.model}).render();
					logger.debug('trigger sign off event on '+_this.model.cid);
					_this.model.trigger('sign_off');					
				});
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
	sso_application:function(){
		logger.debug('click show my token'); 
		this.rightView.undelegateEvents();
		var _this = this;
		require(['modules/user/view.sso'],function(){
			_this.rightView = new RENT.user.view.ShowSSOTokenView({
				el : _this.$el.find('#register_right'),
				userModel: _this.model
			});					
		});
	},
	import_fb_friends:function(){
		logger.debug('click import_fb_friends');
		var _this = this;
		_this.rightView.undelegateEvents();
		require(['modules/user/view.import_fb_friends'],function(){
			new RENT.user.view.ImportFbFriendsView({el: _this.$el.find('#register_right')});
		});

	}
});

});