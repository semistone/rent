define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'logger',
  'text!../../../html/general/tmpl.general.html'
  ], function($, _, Backbone, Mustache, logger,template) {
	
var $template = $('<div>').append(template);
//
// must set total
//
var PaginationView = Backbone.View.extend({
	events : {
		'click .page_index' : 'page_index'
	},
	initialize : function() {
		logger.debug('init pagination');
		if (this.model == null) {
			this.model = new Backbone.Model();
		};
		this.model.set({
			pageSize: 15,
			startPage: 1,
			endPage: 5,
			maxPagination: 5,
			currentPage: 1
		},{silent:true});
		_.bindAll(this, 'render', 'page_index');
		this.tmpl = $template.find('#tmpl_pagination').html();
		this.model.on('change',this.render);
	},
	render: function(){
		logger.debug('render pagination');
		var totalPage = Math.ceil(this.model.get('total')/ this.model.get('pageSize'));
		var currentPage = this.model.get('currentPage');
		var pages = [];
		var maxPagination = this.model.get('maxPagination');
		var startPage, endPage, isPrev = false, isNext = false;
		logger.debug('total page is '+totalPage+ 'max pagination is '+maxPagination);
		if (totalPage > maxPagination) {
			startPage = currentPage - (maxPagination-1)/2;
			endPage = currentPage + (maxPagination-1)/2;
			if (startPage < 1) { // shift
				endPage = endPage - startPage + 1;
				startPage = 1;
			}  else {
				isPrev = true;
			};
			
			if (endPage > totalPage) {
				startPage = totalPage - maxPagination,
				endPage = totalPage;
			} else {
				isNext  = true;
			};
		} else {
			startPage = 1;
			endPage = totalPage;
		};
		//logger.debug('start page is '+startPage+ ' end page is '+endPage);
		for (var i = 0; i <= endPage - startPage ; i++) {
			pages[i] = {};
			pages[i]['index'] = i + startPage;
			if (i + startPage == currentPage) {
				pages[i]['current'] = true;				
			} else {
				pages[i]['current'] = false;				
			};
		}
		this.model.set({startPage: startPage, endPage: endPage,pages:pages},{silent:true});			
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
	},
	page_index:function(ev){
		logger.debug('set page '+setPage);
		var setPage = parseInt($(ev.target).text());
		this.model.set({currentPage: setPage},{silent:true});
		this.trigger('change_page');
	}
});
return PaginationView;
});