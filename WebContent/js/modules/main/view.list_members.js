define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/user/tmpl.member.phtml',
  '../general/view.pagination',
  './collection.member',
  './model.member',
  './view.edit_member',
  'Bootstrap'
  ], function($, _, Backbone, Mustache, RENT, logger,template,Pagniation,MemberCollection, MemberModel, EditMemberView) {
var $template = $('<div>').append(template);	
ListMembersView = Backbone.View.extend({
	model: MemberModel,
	events:{
		'click #search_link': 'search_link',
		'click .edit_member_link' : 'edit_member',
		'click .delete_member_link' : 'delete_member',
		'click #add_member_link': 'add_member',
		'click #save_delete_member_link' : 'save_delete_member'
	},
	initialize : function() {
		this.searchModel = new Backbone.Model();
		_.bindAll(this, 'render', 'i18n', 'search_link', 'change_page',
				'edit_member', 'delete_member', 
				'add_member', 'save_delete_member');
		this.collection = new MemberCollection();
		this.collection.on('error', RENT.simpleErrorDialogForCollectionError);
		this.collection.on('reset add remove error', this.render);
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
		this.$el.find('#i18n_mobile_phone').text(
				$.i18n.prop('user.register.mobile_phone'));
		this.$el.find('#i18n_email').text(
				$.i18n.prop('general.email'));
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#i18n_search').text(
				$.i18n.prop('general.search'));
	},
	change_page:function(){
		logger.debug('change page');
		var search = this.searchModel.get('member_search_input'),
            pageSize = this.paginationModel.get('pageSize'),
            setPage = this.paginationModel.get('currentPage'),
            start = (setPage - 1) * pageSize;
		this.collection.search('%'+search+'%', pageSize, start);		
	},
	search_link:function(){
		var search = this.$el.find('#member_search_input').val();
		logger.debug('search '+search);
		this.searchModel.set({'member_search_input':search}, {silent:true});
		this.paginationModel.set({currentPage:1},{silent:true});
		this.change_page();
	},
	edit_member:function(ev){
		var id = $(ev.target).parent().parent().parent().parent().attr('id'),
            model = this.collection.get(id), view, _this = this;
		logger.debug('edit member '+id);
        view = new EditMemberView({model:model, el:this.el});
        this.undelegateEvents();
        view.on('close', function(){
            _this.change_page();        
            _this.delegateEvents();
        });
	},
	delete_member:function(ev){
		var id = $(ev.target).parent().parent().parent().parent().attr('id'), model,
            _this = this, tmpl;
		logger.debug('delete member '+id);
		model = this.collection.get(id);
		tmpl = $template.find(template_id).html(),
		this.$el.find('#edit_member').html(Mustache.to_html(tmpl ,model.toJSON()));
        this.i18n();
		this.$el.find('#myModal').modal('show');
	},
	save_delete_member:function(){
		var id = this.$el.find('#id').val(), model;
		logger.debug("delete member "+id);
		model = this.collection.get(id);
		model.destroy({wait: true});
		this.$el.find('#myModal').modal('hide');
	},
	add_member:function(){
        var model = new MemberModel, view, _this = this;
        model.on('change', function(){
            logger.debug('add model into collection');
            _this.collection.add(this, {silent:true}); 
        });
        this.undelegateEvents();
        view = new EditMemberView({model:model, el:this.el});
        view.on('close', function(){
            _this.change_page();        
            _this.delegateEvents();
        });
	}
});

return ListMembersView;
});
