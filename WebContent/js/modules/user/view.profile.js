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
  'text!'+RENT.CONSTANTS.TEMPLATE_PATH+'/html/user/tmpl.profile.html',
  './namespace.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);
	
RENT.user.view.UserProfileView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render' , 'i18n');
		if (this.model == null) {
			this.model = new RENT.user.model.UserModel();
		}
		this.model.on('change',this.render);
	},
	render : function() {
		logger.debug('render profile');
		var user = this.model.get('user');
		if (user.loginType == 'FB') {
			user.is_fb = true;			
		}
		var tmpl = $template.find('#tmpl_profile_form').html();
		this.$el.html(Mustache.to_html(tmpl, user));
		this.generateCountryOptions(this.$el.find('#lang'), user.lang+'-'+user.cc);
		this.i18n();
	},
	i18n:function(){
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#i18n_email').text(
				$.i18n.prop('general.email'));
		this.$el.find('#i18n_language').text(
				$.i18n.prop('general.language'));
		this.$el.find('#save_profile_link').text(
				$.i18n.prop('general.save'));
	
	},
    generateCountryOptions:function(selectElement, lang){
    	var options = selectElement.prop('options');
    	if (options.length > 0) {
    		return;
    	}
		$.each(RENT.CONSTANTS.COUNTRIES , function(key, value) {
			var option = new Option($.i18n.prop(value.name),value.locale);
			if (value.locale == lang) {
				option.selected = true;
			}
			options[options.length] = option;

		});
    }
});	
});