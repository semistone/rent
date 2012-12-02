define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/user/tmpl.main.html',
  './router.main'
  ], function($, _, Backbone, Mustache, RENT, logger,template, MainRouter) {

var $template = $('<div>').append(template);
//
// Register main view
//
var RegisterMainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render','sign_off','link_fb');
		this.path = 'main';
		this.tmpl = $template.find('#tmpl_register_step3').html();
		this.rightView = new Backbone.View();
		this.router = new MainRouter({'$el' : this.$el, model: this.model, path: this.path});
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
		};
		this.render();
		var subroute = this.options['subroute'];
		if (subroute == null || subroute == '' ) {
			this.router.navigate('name_device', {trigger: true});			
		} else {
			this.router.navigate( subroute, {trigger:true});						
		};

	},

	events : {
		'click #sign_off_link' : 'sign_off'
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
		this.i18n();
	},
	i18n:function(){
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
		this.$el.find('#i18n_list_members').text(
				$.i18n.prop('user.main.list_members'));
		this.$el.find('#i18n_user_profile').text(
				$.i18n.prop('user.main.user_profile'));		
		this.$el.find('#i18n_mobile_provider').text(
				$.i18n.prop('user.main.mobile_provider'));

	},

	link_fb : function(){
		logger.debug('link to fb');
		var _this = this;

		var do_link_fb = function(){
			_this.model.link_facebook(_this.fb.get('id'), _this.fb.get('name'),{
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
				var fb = new RENT.user.model.FBModel();
				fb.init();
				_this.fb = fb;
				//
				// do login 
				//
				_this.fb.login();
				_this.fb.on('change', do_link_fb);
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
				require(['modules/user/view.user'],function(RegisterView){
					new RegisterView({el:_this.el});
					logger.debug('trigger sign off event on '+_this.model.cid);
					_this.model.trigger('sign_off');					
				});
			},
			error:function(model,resp){
				RENT.simpleErrorDialog(resp,'');
			}
		});	
	}

});
return RegisterMainView;
});
