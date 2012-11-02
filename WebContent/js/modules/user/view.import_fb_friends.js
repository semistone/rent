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
	events : {
		'click .page_index' : 'page_index',
		'click .delete_friend_link' : 'delete_friend'
	},
	initialize : function() {
		this.pageSize = 15;
		this.start = 0;
		this.end= this.pageSize;
		_.bindAll(this, 'render', 'page_index', 'delete_friend');
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
		array.sliceFriends = array.friends.slice(this.start, this.end);
		array.totalPage = Math.ceil(array.friends.length / this.pageSize);
		logger.debug('total is '+array.friends.length+' total page is '+array.totalPage+' start:'+this.start+' end '+this.end);
		array.pages = _.range(1, array.totalPage);
		this.$el.html(Mustache.to_html(this.tmpl ,array));
		
		//
		// i18n
		//
		this.$el.find('#i18n_import_fb_friends').text(
				$.i18n.prop('user.main.import_fb_friends'));
	},
	page_index:function(ev){
		var setPage = parseInt($(ev.target).text());
		logger.debug('set page '+setPage);
		this.start = setPage * this.pageSize;
		this.end = (setPage + 1) * this.pageSize;
		this.render();
	},
	delete_friend:function(ev){
		var id = parseInt($(ev.target).parent().parent().attr('id'));
		logger.debug("delete friend id "+id);
		var model = this.collection.get(id);
		this.collection.remove(model);
	}
});

});