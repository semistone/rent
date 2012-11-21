define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.member.html',
  '../general/view.pagination',
  './collection.member',
  './model.member',
  'Bootstrap'
  ], function($, _, Backbone, Mustache, RENT, logger,template,Pagniation,MemberCollection, MemberModel) {
var $template = $('<div>').append(template);	
ListMembersView = Backbone.View.extend({
	model: MemberModel,
	events:{
		'click #search_link': 'search_link',
		'click .edit_member_link' : 'edit_member',
		'click .delete_member_link' : 'delete_member',
		'click #save_member_link': 'save_edit_member',
		'click #add_member_link': 'add_member',
		'click #save_add_member_link' : 'save_add_member',
		'click #save_delete_member_link' : 'save_delete_member'
	},
	initialize : function() {
		this.searchModel = new Backbone.Model();
		_.bindAll(this, 'render', 'i18n', 'search_link', 'change_page',
				'edit_member', 'delete_member', 'save_edit_member',
				'add_member', 'save_add_member','save_delete_member');
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
		var search = this.$el.find('#member_search_input').val();
		logger.debug('search '+search);
		this.searchModel.set({'member_search_input':search}, {silent:true});
		this.paginationModel.set({currentPage:1},{silent:true});
		this.change_page();
	},
	show_member_form:function(model, template_id, is_need_validate){
		var tmpl = $template.find(template_id).html();
		this.$el.find('#edit_member').html(Mustache.to_html(tmpl ,model.toJSON()));
		this.$el.find('#myModal').modal('show');
		var _this = this;
		if (is_need_validate == false) {
			return;
		}
		RENT.initValidator(function(){
			_this.$el.find("#edit_member_form").validate();			
			_this.$el.find('#mobile-phone').rules('add', {
				regex : /^\+?\d{10,15}$/
			});
			_this.$el.find('#email').rules('add', {
				regex : /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
			});
		});		
	},
	edit_member:function(ev){
		var id = $(ev.target).parent().parent().parent().parent().attr('id');
		logger.debug('edit member '+id);
		var model = this.collection.get(id);
		this.show_member_form(model,'#tmpl_edit_member');
	},
	save_edit_member:function(){
        var formvalidate = this.$el.find("#edit_member_form").valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return false;
        }
		var id = this.$el.find('#id').val();
		logger.debug('save id '+id);
		var model = this.collection.get(id);
		model.set({
			name: this.$el.find('#name').val(),
			email:  this.$el.find('#email').val(),
			mobilePhone:  this.$el.find('#mobile-phone').val()
		},{slient:true});
		model.save();
		this.$el.find('#myModal').modal('hide');
	},
	delete_member:function(ev){
		var id = $(ev.target).parent().parent().parent().parent().attr('id');
		logger.debug('delete member '+id);
		var model = this.collection.get(id);
		this.show_member_form(model,'#tmpl_delete_member', false);
	},
	save_delete_member:function(){
		var id = this.$el.find('#id').val();
		logger.debug("delete member "+id);
		var model = this.collection.get(id);
		model.destroy({wait: true});
		this.$el.find('#myModal').modal('hide');
	},
	add_member:function(){
		this.show_member_form(new MemberModel(),'#tmpl_new_member');	
	},
	save_add_member:function(){
        var formvalidate = this.$el.find("#edit_member_form").valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return false;
        }	
		logger.debug('save id '+id);
		var model = new MemberModel();
		model.set({
			name: this.$el.find('#name').val(),
			email:  this.$el.find('#email').val(),
			mobilePhone:  this.$el.find('#mobile-phone').val()
		},{slient:true});
		model.save();
		this.$el.find('#myModal').modal('hide');        
	}
});

return ListMembersView;
});