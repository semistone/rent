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
  'text!template/user/tmpl.profile.phtml',
  '../user/model.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template, UserModel) {
var $template = $('<div>').append(template);
	
var UserProfileView = Backbone.View.extend({
    events:{
        "click #save_profile_link" : "save_profile",	
    },
    initialize : function() {
        _.bindAll(this, 'render' , 'i18n');
        if (this.model == null) {
            this.model = new UserModel();
        }
        this.model.on('change',this.render);
    },
    render : function() {
        var _this = this, tmpl, user;
        logger.debug('render profile');
        user = this.model.toJSON();
        user = $.extend({}, this.model.toJSON());

        if (this.model.get('loginType') == 'FB') {
            user.is_fb = true;
        }
        tmpl = $template.find('#tmpl_profile_form').html();
        this.$el.html(Mustache.to_html(tmpl, user));
        this.generateCountryOptions(this.$el.find('#lang'), this.model.get('lang')+'-'+ this.model.get('cc'));
        this.i18n();
		
        RENT.initValidator(function(){
			_this.$el.find("#profile_form").validate();			
			_this.$el.find('#email').rules('add', {
				regex : /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
			});
        });			
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
    },
    save_profile:function(){
        var formvalidate = this.$el.find('#profile_form').valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
		this.model.set({
			name: this.$el.find('#name').val(),
			email:  this.$el.find('#email').val(),
			lang:  this.$el.find('#lang').val()
		},{slient:true});
		this.model.save();
    }
});	
return UserProfileView;
});
