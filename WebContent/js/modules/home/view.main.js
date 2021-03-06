define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/home/tmpl.main.phtml',
  './model.page'
  ], function($, _, Backbone, Mustache, RENT, logger,template, PageModel) {
var $template = $('<div>').append(template);	
var Menu = Backbone.View.extend({
	events :{
		'click #edit_mode': 'edit_mode',
		'click #preview' : 'preview',
		'click #save' : 'save'
	},
	initialize:function(){
		this.render();
	},
	render:function(){
		logger.debug('render menu');
		var tmpl = $template.find('#tmpl_navi').html();
		this.$el.find('#nav-menu').html(tmpl);
	},
	edit_mode:function(){
		this.trigger('edit_mode');
	},
	preview:function(){
		this.trigger('preview');
	},
	save:function(){
		this.trigger('save');		
	},
	append_menu:function(){
		var _this = this;
		require(['Bootstrap'],function(){
			var tmpl = $template.find('#tmpl_edit_menu').html();
			_this.$el.find('#nav-menu').append(tmpl);
		});	
	}
});
var MainView = Backbone.View.extend({

	initialize : function() {
		_.bindAll(this, 'render', 'init_router', 'edit', 'edit_mode', 'navibar',
				'preview', 'save', 'show_page');
		this.path = this.options['parent_route'] || 'home';
		this.init_router();
		var subroute = this.options['subroute'];
		if (subroute == null || subroute == '') {
			subroute = 'index';
		}
		if (subroute.indexOf('edit') > 0) {
			this.router.trigger('route:edit', subroute);						 
		} else{
			this.router.trigger('route:show_page', subroute);			
		}
	},
	render:function(){
		logger.debug('render');
		var  _this = this, s = this.model.toJSON();
		//logger.debug(s);
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		$.each(s, function(key, value){
			_this.$el.find('#'+key).addClass('editable');
		});		
		this.navibar();
		this.preview_mode = true;
	},
	show_page:function(subview){
		logger.debug('subview is '+subview);
		this.tmpl = $template.find('#tmpl_'+subview).html();
		this.model = new PageModel();
		this.model.on('change', this.render);
		this.model.set({id:subview+'.json'}, {silent:true});
		this.model.fetch();		
	},
	navibar:function(){
		if (this.menu != undefined){
			return;
		}
		var el = this.options['menu_el'];
		logger.debug('render navibar el '+el);
		this.menu = new Menu({el:el});
		this.menu.on('edit_mode',  this.edit_mode);
		this.menu.on('preview',  this.preview);
		this.menu.on('save',  this.save);
	},
	init_router:function(){
		this.router = new Backbone.Router();
		this.router.route(this.path+'/:name/edit', 'edit');
		this.router.route(this.path+'/:name', 'show_page');
		this.router.on('route:edit', this.edit);
		this.router.on('route:show_page', this.show_page);
	},
	edit:function(){
		logger.debug('add edit menu');
		this.navibar();
		this.menu.append_menu();
	},
	edit_mode:function(){
		this.preview_mode =false;
		logger.debug('edit mode');
		this.$el.find('.editable').each(function(index){
			var height, width,textarea, html, element = $(this);
			width = element.width();
			height = element.height();
			html = element.html().trim();
			textarea = $('<textarea>');
			textarea.val(html);
			element.html(textarea);
			//logger.debug('width is '+width);
			textarea.width(width);
			textarea.height(height);
		});
	},
	preview:function(){
		this.preview_mode =true;
		logger.debug('preview');
		this.$el.find('.editable').each(function(index){
			var html, element = $(this);
			html = element.find('textarea').val();
			element.html(html);
		});
	},
	save:function(){
		var obj = {};
		if (this.preview_mode == false) {
			this.preview();
		}
		logger.debug('save');
		this.$el.find('.editable').each(function(index){
			var id,element = $(this);
			id = element.attr('id');
			obj[id] = element.html();
		});
		this.model.set(obj,{silent:true});
		this.model.save();
	}
});
return MainView;
});
