define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.mobile_provider.html',
  './model.mobile_provider'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);	
	
RENT.user.view.MobileProviderView = Backbone.View.extend({
	events : {
		"click #save_provider_link" : 'save_provider'
	},	
	initialize : function() {
		this.tmpl = $template.find('#tmpl_mobile_provider_form').html();
		_.bindAll(this, 'render','save_provider', 'success', 'error');
		this.model = new RENT.user.model.MobileProviderModel();
	},	
	save_provider:function(){
		logger.debug('save provider');
        var formvalidate = this.$el.find('#sently_moble_provider_form').valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
        var _this = this;
		var user = $('#sently_user').val();
		logger.debug('name is '+name);
		var password = $('#sently_password').val();
		logger.debug('password is '+password);
        this.model.new_mobile_provider(user, password,{
        	success:_this.success,
        	error:_this.error
        });
	},
	success:function(){
		
	},
	error:function(){
		
	},
	render:function(){
		this.$el.html(this.tmpl);
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find('#sently_moble_provider_form').validate();
		});				
		//
		// i18n
		//
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#i18n_password').text(
				$.i18n.prop('general.password'));
		this.$el.find('#save_provider_link').text(
				$.i18n.prop('general.save'));
	}	
});

});