define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!'+RENT.CONSTANTS.DOCUMENT_ROOT+'/html/home/tmpl.main.html',
  './model.page'
  ], function($, _, Backbone, Mustache, RENT, logger,template, PageModel) {
var $template = $('<div>').append(template);	

var MainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render', 'init_router', 'edit');
		this.tmpl = $template.find('#tmpl_home').html();
		this.model = new PageModel();
		this.model.on('change', this.render);
		this.model.set({id:'home.json'}, {silent:true});
		this.model.fetch();
		this.init_router();
		var subroute = this.options['subroute'];
		if (subroute != null && subroute != '' ) {
			this.router.navigate(subroute, {trigger: true});						
		};
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
	},
	init_router:function(){
		this.router = new Backbone.Router();
		this.router.route('edit', 'edit');	
		this.router.on('route:edit', this.edit);
	},
	edit:function(){
		logger.debug('edit mode');
		this.router.navigate('home/edit', {replace: true});
	}
});
return MainView;
});
