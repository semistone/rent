define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!'+RENT.CONSTANTS.DOCUMENT_ROOT+'/html/user/tmpl.sso.html',
  './namespace.user',
  './model.sso'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);

RENT.user.view.ShowSSOTokenView = Backbone.View.extend({
	hasRole : false,
	events:{
		'click #sign_button':'fill_form',
		'click #go_button':'do_mobile_auth_request',
		'click #apply_token_link': 'apply_token'
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
	initialize:function(){
		logger.debug('initialize show sso token view');
		this.userModel = this.options.userModel;
		delete this.options.userModel;
		if (this.userModel == null ||this.userModel == undefined) {
			logger.error('user model not defined');
			return;
		}
		this.model = new RENT.user.model.RequestModel();
		_.bindAll(this, 'render', 'show_token','apply_token');

		var _this = this;
		var inRole = function() {
			logger.debug('sso role exist');
			_this.hasRole = true;
			_this.show_token();			
		};
		var noRole = function(){
			_this.render();
		};
		RENT.user.checkRole(this.userModel,5, inRole, noRole);
		this.model.on('change',this.render);
		this.model.set({
			requestId:this.GUID(),
			requestTime:Math.ceil(new Date().getTime()/1000),
			done:window.location.href,
			requestFrom:this.userModel.get('userId')
		});
	},
	show_token:function(){
		var _this = this;		
		this.model.get_sso_application_token({
			success:function(model,resp){
				logger.debug('get sso application token success');
				_this.model.set({token:model.token});
			},
			error:function(model,resp){
				logger.error('get sso application token fail');
				RENT.simpleErrorDialog(resp,'');
			}
		});
	},
	i18n:function(){
		this.$el.find('#i18n_request_id').text(
				$.i18n.prop('user.sso.request_id'));
		this.$el.find('#i18n_country_code').text(
				$.i18n.prop('user.sso.country_code'));
		this.$el.find('#i18n_mobile_phone').text(
				$.i18n.prop('user.register.mobile_phone'));
		this.$el.find('#i18n_auth_user_id').text(
				$.i18n.prop('user.sso.auth_user_id'));
		this.$el.find('#i18n_force_reauth').text(
				$.i18n.prop('user.sso.force_reauth'));
		this.$el.find('#i18n_down_url').text(
				$.i18n.prop('user.sso.done_url'));
		this.$el.find('#i18n_request_sso_user').text(
				$.i18n.prop('user.sso.request_sso_user'));
		this.$el.find('#i18n_request_time').text(
				$.i18n.prop('user.sso.request_time'));
		this.$el.find('#i18n_callback').text(
				$.i18n.prop('user.sso.callback'));
		this.$el.find('#i18n_signature').text(
				$.i18n.prop('user.sso.signature'));
		this.$el.find('#i18n_debug').text(
				$.i18n.prop('general.debug'));
		this.$el.find('#sign_button').text(
				$.i18n.prop('general.sign'));
		this.$el.find('#i18n_information').text(
				$.i18n.prop('general.information'));
		this.$el.find('#go_button').text(
				$.i18n.prop('general.execute'));
		
	},
	render:function(){
		logger.debug('render get sso application token');
		var _this = this;
		if (this.hasRole) {
			this.tmpl = $template.find('#tmpl_show_sso_token').html();
			this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
			this.i18n();
			RENT.generateCountryOptions(this.$el.find('#countryCode'));

			RENT.initValidator(function(){
				_this.$el.find("#mobile_auth_request_form").validate();
			});

			//
			// i18n
			//
			this.$el.find('#i18n_show_token_msg').text($.i18n.prop('user.main.show_token_msg'));			
		} else {
			logger.debug('no role');
			this.tmpl = $template.find('#tmpl_apply_token').html();
			this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		}
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
		this.model.get_signature_of_mobile_auth_request({
			success:function(model, resp){
				logger.debug('success');
				_this.$el.find('#sign').val(model.sign);
			},
			error:function(model,resp){
				logger.error('get_signature_of_mobile_auth_request error');
				RENT.simpleErrorDialog(resp,'');
			}
		});
	},
	do_mobile_auth_request:function(){
		logger.debug("do submit");
		this.$el.find("#mobile_auth_request_form").submit();
	},
	apply_token:function(){
		logger.debug("apply token");
		var _this = this;
		this.model.apply_sso_application({
			success:function(model,resp){
				logger.debug('apply sso success');
				var user = _this.userModel.get('user');
				user.roles.push('5'); // push role 5 is sso application
				_this.hasRole = true;
				_this.show_token();
			},
			error:function(model,resp){
				logger.error('apply_sso_application error');
				RENT.simpleErrorDialog(resp,'');

			}
		});
	}
});
});