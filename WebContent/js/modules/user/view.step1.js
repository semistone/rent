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
  'text!../../../html/user/tmpl.step1.html'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);
//
// step1
//
RENT.user.view.RegisterStep1View = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render', 'new_device_event' , 'new_device', 
				'mobile_auth_request', 'success', 'error' ,'validate');
		this.tmpl = $template.find('#tmpl_register_step1').html();
	},
	render: function(){
		this.model.trigger('change_view','step1');
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		RENT.generateCountryOptions(this.$el.find('#country_code'));
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#register_form").validate();			
			_this.$el.find('#mobile_phone').rules('add', {
				regex : /^\+?\d{10,15}$/
			});
		});
		//
		// l10n translate
		//
		this.$el.find('#i18n_register_button').text($.i18n.prop('user.register'));
		this.$el.find('#i18n_mobile_phone').text(
				$.i18n.prop('user.register.mobile_phone'));
		this.$el.find('#i18n_country_code').text(
				$.i18n.prop('user.register.country_code'));	


	},
	events : {
		"click #register_button" : "new_device_event"
	},
	new_device :function(){
		if (this.model.get('responseId') == null) {
			this.new_device_event();
		} else {
			this.mobile_auth_request();
		}
	},
	mobile_auth_request:function(){
		logger.debug('do mobile_auth_request');
		var mobileAuthRequestForm = RENT.user.mobileAuthRequestForm;
		if (!this.validate()) return;
		var _this = this;
		this.model.mobile_auth_request(mobileAuthRequestForm,{
			success:function(model,response){
				logger.debug('success');					
				_this.model.set(model);
			},
			error:function(resp){
				if (resp != null && resp.status == 409) {
					_this.model.set({status:1});
				} else {
					_this.error(_this.model,resp);						
				}
			}
		});
	},
	success : function(model, response) {
		logger.debug('render register view step2');
		this.undelegateEvents();
		if (model.status == 2) {
			logger.debug('device has authed');
			this.model.set(model,{silent:true});
			this.model.trigger('verify_success');
			return;
		}
		require(['modules/user/view.step2'], function(){
			new RENT.user.view.RegisterStep2View({
				el : this.el,
				model : this.model
			}).render();				
		});
	},
	error :function(resp) {
		logger.error('step1 error response:' + resp.status);
		RENT.simpleErrorDialog(resp);
	},
	validate :function(){
        var formvalidate = this.$el.find("#register_form").valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return false;
        }
		var country_code = this.$el.find('#country_code').val();
		var mobile_phone = this.$el.find('#mobile_phone').val();
		// simple verify
		if (mobile_phone.substring(0, 1) == '0') {
			mobile_phone = country_code + mobile_phone.substring(1);
		}else if (mobile_phone.substring(0, 1) == '+'){			
			mobile_phone = mobile_phone.substring(1);
		}else{
			mobile_phone = country_code + mobile_phone;
		}
		this.model.set({
			countryCode : country_code,
			mobilePhone : mobile_phone			
		}, {silent:true});
	},
	new_device_event : function() {
		logger.debug('click new device button');
		if (!this.validate()) return;
		logger.debug('do save');
		this.model.new_device({
			success : this.success,
			error : this.error
		});
	}
});
});