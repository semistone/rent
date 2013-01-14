define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'logger',
  'text!template/general/tmpl.general.phtml'
  ], function($, _, Backbone, Mustache, logger,template) {
var ErrorView, $template = $('<div>').append(template);
ErrorView = Backbone.View.extend({
		initialize : function() {
			this.tmpl = $template.find('#tmpl_oops_error').html();
		},
		render:function(){
			this.$el.html(this.tmpl);
			//
			// i18n
			//
			this.$el.find('#i18n_error').text(
					$.i18n.prop('user.register.error'));
		}
});	
return ErrorView;
});
