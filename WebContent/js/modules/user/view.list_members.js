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
	initialize : function() {
		_.bindAll(this, 'render', 'i18n');
		this.collection = new MemberCollection();
		this.collection.on('reset add remove', this.render);
		this.tmpl = $template.find('#tmpl_list_members').html();
		this.collection.search('%', '10', '0');
	},
	render:function(){
		logger.debug('render member');
		var array = {
			'members': this.collection.toJSON()
		};
		this.$el.html(Mustache.to_html(this.tmpl ,array));
		this.i18n();
	},
	i18n:function(){
		this.$el.find('#i18n_show_members').text(
				$.i18n.prop('user.main.list_members'));
	}
});

return ListMembersView;
});