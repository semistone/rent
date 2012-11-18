define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/home/tmpl.main.html',
  './model.page'
  ], function($, _, Backbone, Mustache, RENT, logger,template, PageModel) {
var $template = $('<div>').append(template);	

var MainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render');
		this.tmpl = $template.find('#tmpl_home').html();
		this.model = new PageModel();
		this.model.on('change', this.render);
		this.model.set({id:'home'}, {silent:true});
		this.model.fetch();
	},
	render:function(){
		logger.debug('render');
		var s = this.model.toJSON();
		logger.debug(s);
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
	},
	navibar:function(el){
		logger.debug('render navibar');
		var tmpl = $template.find('#tmpl_navi').html();
		$(el).find('#nav-menu').html(tmpl);
	}
});
return MainView;
});