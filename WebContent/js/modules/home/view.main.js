define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/home/tmpl.main.html'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);	

var MainView = Backbone.View.extend({
	initialize : function() {
		this.tmpl = $template.find('#tmpl_home').html();
		this.render();
	},
	render:function(){
		this.$el.html(this.tmpl);
	},
	navibar:function(el){
		logger.debug('render navibar');
		var tmpl = $template.find('#tmpl_navi').html();
		$(el).find('#nav-menu').html(tmpl);
	}
});
return MainView;
});