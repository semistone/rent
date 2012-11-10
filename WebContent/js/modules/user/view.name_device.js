define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.name_device.html'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);
//
// name device form
//
RENT.user.view.NameDeviceView = Backbone.View.extend({
	events : {
		"click #save_name_link" : 'save_name'
	},
	initialize : function() {
		this.tmpl = $template.find('#tmpl_name_device_form').html();
		_.bindAll(this, 'render','save_name');
	},
	save_name:function(){
		logger.debug('click name device popup save');
        var formvalidate = this.$el.find('#name_device_form').valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
		var name = $('#device_name').val();
		logger.debug('name is '+name);
		var _this = this;
		this.model.name_device(name, {
			success : function() {
				logger.debug('click name device popup save success');
				_this.trigger("success");
			},
			error : function() {
				logger.debug('click name device popup save error');
				alert('error');
			}
		});
	},
	render:function(){
		this.model.trigger('change_view','name_device');
		this.$el.html(Mustache.to_html(this.tmpl,this.model.toJSON()));
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#name_device_form").validate();
			_this.$el.find('#device_name').rules('add', {
				regex : /^[^\<\(\)]*$/
			});
		});	
		this.i18n();
	},
	i18n:function(){
		//
		// i18n
		//
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#dialog_form_block').attr('title',
				$.i18n.prop('user.register.name_device_title'));
		this.$el.find('#save_name_link').text(
				$.i18n.prop('general.save'));		
	}
	
});
});