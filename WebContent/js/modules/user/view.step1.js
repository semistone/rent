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
		_.bindAll(this, 'render', 'new_device_event' , 'new_device_event', 
				'success', 'error' ,'validate');
		this.tmpl = $template.find('#tmpl_register_step1').html();
		if (this.model == null) {
			this.model = new RENT.user.model.UserModel();
		}
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

	success : function(model, response) {
		logger.debug('render register view step2');
		this.undelegateEvents();
		if (model.status == 2) {
			logger.debug('device has authed');
			this.model.set(model,{silent:true});
			this.model.trigger('verify_success');
			return;
		}
		var _this = this;
		require(['modules/user/view.step2'], function(){
			new RENT.user.view.RegisterStep2View({
				el : _this.el,
				model : _this.model
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
			mobile_phone = '+'+country_code + mobile_phone.substring(1);
		}else if (mobile_phone.substring(0, 1) == '+'){			
			// do nothing
		}else{
			mobile_phone = '+'+country_code + mobile_phone;
		}
		this.model.set({
			countryCode : country_code,
			mobilePhone : mobile_phone			
		}, {silent:true});
		return true;
	},
	new_device_event : function() {
		logger.debug('click new device button');
		if (!this.validate()) return;
		// change locale by country code.
		var countryCode = this.model.get('countryCode');
		var locale = RENT.CONSTANTS.COUNTRIES[countryCode].locale;
		var _this = this;
		RENT.setLangRes(locale, ['rent_user'],'', function(){
			logger.debug('do save');
			_this.model.new_device({
				success : _this.success,
				error : _this.error
			});			
		});
	}
});
});