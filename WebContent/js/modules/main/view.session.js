define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/user/tmpl.devices.phtml',
  '../user/collection.session'
  ], function($, _, Backbone, Mustache, RENT, logger,template, SessionCollection) {
var ShowSessionsView,$template = $('<div>').append(template);
	//
	//session list
	//
ShowSessionsView = Backbone.View.extend({
		initialize : function() {
			logger.debug('initialize show sessions view');
			this.tmpl = $template.find('#tmpl_show_sessions').html();
			_.bindAll(this, 'render');
			if (this.collection == null) {
				this.collection = new SessionCollection();
			}
			var limit = 10;
			var offset = 0;
			var deviceId = this.model.get('deviceId');
			options = {
				data: $.param({ deviceId:deviceId,limit: limit, offset:offset}),
				error:function(model,resp){
					RENT.simpleErrorDialog(resp);
				}
			};
			this.collection.fetch(options);
			this.collection.on('reset',this.render);
			this.collection.on('remove',this.render);
		},
		render:function(){
			this.model.trigger('change_view','show_sessions');
			logger.debug("render sessions");
			var sessions = this.collection.toJSON();
			$.each(sessions,function(index, row){
	     	//logger.debug('created is '+row.created);
	     	var date = new Date(row.created * 1000);
	     	var then = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
	             then += ' '+date.getHours()+':'+date.getMinutes();
	             row['createdDate'] = then;
			});
			this.$el.html(Mustache.to_html(this.tmpl,{sessions:sessions} ));
			this.i18n();
		},
		i18n:function(){
		     //
		     // i18n
		     //
		     this.$el.find('.i18n_created').text(
		 				$.i18n.prop('general.created'));
		     this.$el.find('#i18n_last_login_ip').text(
		 				$.i18n.prop('user.register.last_login_ip'));
		     this.$el.find('#i18n_sessions').text(
		 				$.i18n.prop('user.register.show_sessions'));
		     this.$el.find('#i18n_city').text(
		 				$.i18n.prop('general.city'));
		     this.$el.find('#i18n_country').text(
		 				$.i18n.prop('general.country'));
		}	
});
return ShowSessionsView;
});