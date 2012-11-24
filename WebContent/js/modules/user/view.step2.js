//
// step2 view 
//
define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!'+RENT.CONSTANTS.TEMPLATE_PATH+'/html/user/tmpl.step2.html'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);

RENT.user.view.RegisterStep2View = Backbone.View.extend({
	events : {
		"click #verify_button" : 'do_verify',
		"click #send_mobile_auth_message" : 'send_mobile_auth_message',
		'click #before_button' : 'go_back_step1'
	},
	initialize : function() {
		this.tmpl = $template.find('#tmpl_register_step2').html();
		_.bindAll(this, 'render','do_verify','go_back_step1');
		this.model.bind('error',this.error);
	},
	render : function() {
		this.model.trigger('change_view','step2');
		logger.debug('render register step2');
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));

		// validate setting
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#register_form_step2").validate();
			_this.$el.find('#auth_code').rules('add', {
				regex : /^\d{6}$/
			});
		});
		//
		// i18n
		//
		$('#register_title').text($.i18n.prop('user.register.step2'));
		
		this.$el.find('#i18n_enter_auth_code').text(
				$.i18n.prop('user.register.enter_auth_code'));
		this.$el.find('#i18n_verify_button').text(
				$.i18n.prop('user.register.verify'));
		this.$el.find('#i18n_before_button').text(
				$.i18n.prop('general.before'));
		
		this.$el.find('#i18n_send_mobile_auth_message').text(
				$.i18n.prop('user.register.send_mobile_auth_message'));
	},
	send_mobile_auth_message :function(){
		logger.debug('click send mobile message');
		this.model.send_mobile_auth_message({
			success : function(model, resp) {
				logger.debug("send success");
				RENT.simpleDialog('',$.i18n.prop('user.register.send_success'));
			},
			error : function(resp, text) {
				RENT.simpleErrorDialog(resp,'');
			}
		});
	},
	do_verify : function() {
		var success, error,_this;
		_this = this;
        var formvalidate = this.$el.find("#register_form_step2").valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
		success = function(model, resp) {
			logger.debug("verify success ");
			_this.undelegateEvents();
			logger.debug('verify success, set model and trigger success');
			if (_this.model.get('user') == null) {
				logger.debug('fetch user again');
				_this.model.fetch({
					success:function(){
						_this.model.trigger('verify_success');
					},
					error:function(model,resp){
						logger.error('fetch user error');
						RENT.simpleErrorDialog(resp,'');
					}
				});
			} else {
				_this.model.set({status:3},{silent:true});
				_this.model.trigger('verify_success');
			}
		};
		var auth_code = this.$el.find('#auth_code').val();
		logger.debug('click do verify button auth code is '+auth_code);
		if (RENT.user.mobileAuthRequestForm == undefined || RENT.user.mobileAuthRequestForm == null) {
			this.model.verify_mobile_auth_code(auth_code,{success:success});			
		} else {
			this.model.verify_mobile_auth_request_code(auth_code,{success:success});
		}
	},
	error :function(model,resp){
		logger.error("verify fail ");
		RENT.simpleErrorDialog(resp, '');
	},
	go_back_step1 : function() {
		this.undelegateEvents();
		var _this = this;
		require(['modules/user/view.step1'],function(){
			new RENT.user.view.RegisterStep1View({
				el : _this.el,
				model : _this.model
			}).render();			
		});
	}
});
});