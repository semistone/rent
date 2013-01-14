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
  'text!template/user/tmpl.register.phtml',
  './namespace.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);
	
NavBarView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render', 'initFBModel','login_facebook','logout_facebook');
		var _this = this;
		this.model.on('logoff_success',function(){
			logger.debug('navbar sign off');
			_this.$el.find('#user_info').empty();
			if (_this.fbModel != null) {
				_this.fbModel.logout();
				_this.fbModel = null;
			}
			
		});
	},
	events : {
		"click #login_fb_link" : "login_facebook",
		'click #logout_fb_link' : 'logout_facebook'
	},
	initFBModel:function(model){
		this.fbModel = model;
		var _this = this;


		this.fbModel.on('change',function(){
			_this.render();
		});
		

	},
	render:function(){
		if (this.fbModel != null) {
			var fbData  = this.fbModel.toJSON();
			var tmpl = $template.find('#tmpl_fb_info').html();
			logger.debug('change navi bar id is '+fbData.name);
			this.$el.find('#user_info').html(Mustache.to_html(tmpl,fbData));
			
			//
			// i18n
			// 
			this.$el.find('#i18n_login_to_fb').text(
					$.i18n.prop('user.register.login_to_fb'));	
			this.$el.find('#i18n_user_not_match').text(
					$.i18n.prop('user.register.user_not_match'));	

			
		}
	},
	login_facebook:function(){
		var _this = this;
		_this.fbModel.login();			
	},
	logout_facebook:function(){
		this.fbModel.logout(function(){
			logger.debug('logout success');
		});		
	}
});

return NavBarView;
});
