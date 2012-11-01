define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.import_fb_friends.html',
  './model.fb'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);	
RENT.user.view.ImportFbFriendsView = Backbone.View.extend({
	initialize : function() {
		this.model = new RENT.user.model.FBModel();
		_.bindAll(this, 'render');
		this.model.on('change', this.render);
		this.tmpl = $template.find('#tmpl_show_fb_friends').html();
		this.model.friends();
	},
	render:function(){
		logger.debug('render friends');
		this.$el.html(Mustache.to_html(this.tmpl ,this.model.toJSON()));
		//
		// i18n
		//
		this.$el.find('#i18n_import_fb_friends').text(
				$.i18n.prop('user.main.import_fb_friends'));
	}
});

});