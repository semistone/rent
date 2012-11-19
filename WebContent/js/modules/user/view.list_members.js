define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.member.html',
  '../general/view.pagination',
  './collection.member'
  ], function($, _, Backbone, Mustache, RENT, logger,template,Pagniation,MemberCollection) {
var $template = $('<div>').append(template);	
ListMembersView = Backbone.View.extend({
	events:{
		'click #search_link': 'search_link'
	},
	initialize : function() {
		this.searchModel = new Backbone.Model();
		_.bindAll(this, 'render', 'i18n', 'search_link', 'change_page');
		this.collection = new MemberCollection();
		this.collection.on('reset add remove', this.render);
		this.collection.on('error', RENT.simpleErrorDialogForCollectionError);
		this.tmpl = $template.find('#tmpl_list_members').html();
		this.collection.search('%', '10', '0');
		this.paginationModel = new Backbone.Model();
		this.pagniation =new Pagniation({
			tagName:'div',
			model:this.paginationModel});
		this.pagniation.on('change_page',this.change_page);

	},
	render:function(){
		logger.debug('render member');
		var array = {
			'members': this.collection.toJSON(),
			'member_search_input' : this.searchModel.get('member_search_input')
		};		
		this.$el.html(Mustache.to_html(this.tmpl ,array));
		this.$el.find('#member_pagniation').html(this.pagniation.$el);
		this.pagniation.delegateEvents();
		this.i18n();
		this.paginationModel.set({total:this.collection.total});
		
	},
	i18n:function(){
		this.$el.find('#i18n_show_members').text(
				$.i18n.prop('user.main.list_members'));
	},
	change_page:function(){
		logger.debug('change page');
		var search = this.searchModel.get('member_search_input');
		var pageSize = this.paginationModel.get('pageSize');
		var setPage = this.paginationModel.get('currentPage');
		var start = (setPage - 1) * pageSize;
		this.collection.search('%'+search+'%', pageSize, start);		
	},
	search_link:function(){
		logger.debug('search '+search);
		var search = this.$el.find('#member_search_input').val();
		this.searchModel.set({'member_search_input':search}, {silent:true});
		this.paginationModel.set({currentPage:1},{silent:true});
		this.change_page();
	}
});

return ListMembersView;
});