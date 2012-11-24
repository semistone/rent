define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!'+RENT.CONSTANTS.TEMPLATE_PATH+'/html/user/tmpl.import_fb_friends.html',
  '../general/view.pagination',
  './collection.friends'
  ], function($, _, Backbone, Mustache, RENT, logger,template,Pagniation) {
var $template = $('<div>').append(template);	
RENT.user.view.ImportFbFriendsView = Backbone.View.extend({
	events : {
		'click .delete_friend_link' : 'delete_friend',
		'click #create_members_link' : 'create_members'
	},
	initialize : function() {
		this.paginationModel = new Backbone.Model();
		this.pagniation =new Pagniation({
			tagName: 'div',
			model:this.paginationModel});
		_.bindAll(this, 'render',  'delete_friend', 'undelegateEvents',
				'create_members');
		this.pagniation.on('change_page',this.render);
		this.collection = new RENT.user.collection.FriendCollection();
		this.collection.on('reset add remove', this.render);
		this.tmpl = $template.find('#tmpl_show_fb_friends').html();
		this.collection.fetch();
	},
	render:function(){
		logger.debug('render friends');
		var array = {};
		array.friends = this.collection.toJSON();
		if (array.friends == undefined) {
			return;
		}
		if (array.friends.length == 0) {
			logger.info('total friends size is 0');
			return;
		}
		this.paginationModel.set({total:array.friends.length});
		var pageSize = this.paginationModel.get('pageSize');
		var setPage = this.paginationModel.get('currentPage');
		var start = (setPage - 1) * pageSize;
		var end = setPage * pageSize;
		array.sliceFriends = array.friends.slice(start, end);
		this.$el.html(Mustache.to_html(this.tmpl ,array));
		this.$el.find('#friend_table').append(this.pagniation.$el);
		this.pagniation.delegateEvents();
		this.i18n();
	},
	i18n:function(){
		//
		// i18n
		//
		this.$el.find('#i18n_import_fb_friends').text(
				$.i18n.prop('user.main.import_fb_friends'));
		this.$el.find('#create_members_link').text(
				$.i18n.prop('general.save'));

	},
	delete_friend:function(ev){
		var id = parseInt($(ev.target).parent().parent().attr('id'));
		logger.debug("delete friend id "+id);
		var model = this.collection.get(id);
		this.collection.remove(model);
	},
	create_members: function(){
		this.collection.create_members({
			success:function(model,resp){
				logger.info('success');
			},
			error:function(model,resp){
				RENT.simpleErrorDialog(resp,'');
			}
		});
	}
});

});