define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/user/tmpl.main.phtml',
  './router.main'
  ], function($, _, Backbone, Mustache, RENT, logger,template, MainRouter) {

var $template = $('<div>').append(template);
//
// Register main view
//
var RegisterMainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render','sign_off');
		this.path = this.options['parent_route'] || 'main';
		this.tmpl = $template.find('#tmpl_register_step3').html();
		this.rightView = new Backbone.View();
		this.router = new MainRouter({'$el' : this.$el, model: this.model, path: this.path});
		this.render();
		var subroute = this.options['subroute'];
		if (subroute == null || subroute == '' ) {
			this.router.trigger('route:name_device');		
		} else {
			this.router.trigger('route:'+subroute);		
		};

	},

	events : {
		'click #sign_off_link' : 'sign_off'
	},
	render:function(){
        this.model.trigger('change_view','main');
        var device= $.extend(true, {}, this.model.toJSON());
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
	sign_off:function(){
		logger.debug('click signoff device');
		this.trigger('logoff');
	}

});
return RegisterMainView;
});
