define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!'+RENT.CONSTANTS.DOCUMENT_ROOT+'/html/user/tmpl.devices.html',
  './namespace.user',
  './model.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);
//
//device list
//
RENT.user.view.ShowDevicesView = Backbone.View.extend({
	events:{
		'click .delete_device_link': 'delete_device',
		'click .show_sessions_link': 'list_sessions'
	},
	initialize : function() {
		logger.debug('initialize show devices view');
		this.tmpl = $template.find('#tmpl_show_devices').html();
		_.bindAll(this, 'render','delete_device','list_sessions');
		if (this.collection == null) {
			this.collection =new RENT.user.collection.UserCollection();
			this.collection.fetch();
		}
		this.collection.on('reset',this.render);
		this.collection.on('remove',this.render);
	},
	render:function(){
		this.model.trigger('change_view','show_devices');
		logger.debug("render devices");
		var obj = {devices:this.collection.toJSON() };
		$.each(obj.devices ,function(index, row){
	     	//logger.debug('created is '+row.created);
	     	var date = new Date(row.lastLoginTime * 1000);
	     	var then = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate();
	             then += ' '+date.getHours()+':'+date.getMinutes();
	        row['lastLoginTime'] = then;
		});

		this.$el.html(Mustache.to_html(this.tmpl,obj ));
		this.i18n();
		
	},
	i18n:function(){
		//
		// i18n
		//
		this.$el.find('#i18n_devices').text(
				$.i18n.prop('user.register.show_devices'));
		this.$el.find('.i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#i18n_last_login_ip').text(
				$.i18n.prop('user.register.last_login_ip'));
		this.$el.find('#i18n_last_login_time').text(
				$.i18n.prop('user.register.last_login_time'));
	},
	list_sessions:function(ev){
		var deviceId = $(ev.target).parent().attr('id');
		logger.debug("list sessions device id "+deviceId); 
		this.model.set({deviceId:deviceId},{silent:true});
		var collection = new RENT.user.collection.SessionCollection();
		var _this = this;
		collection.on('reset',function(){
			logger.debug('undelegate reset event');
			_this.undelegateEvents();
			_this.collection.off('reset', arguments.callee.caller);			
		});
		new RENT.user.view.ShowSessionsView({
			el : this.$el,
			model : this.model,
			collection: collection
		});
	},
	delete_device:function(ev){
		var id = $(ev.target).parent().parent().attr('id');
		logger.debug("delete device id "+id);
		var _this = this;
		var success =function(model){
			logger.debug("delete success");
			var model = _this.collection.get(id);
			_this.collection.remove(model);
			
		};
		var error = function(model,resp){
			RENT.simpleErrorDialog(resp);
		};
		this.model.delete_device(id,{
			success:success,
			error:error
		});
	}
});
//
//session list
//
RENT.user.view.ShowSessionsView = Backbone.View.extend({
	initialize : function() {
		logger.debug('initialize show sessions view');
		this.tmpl = $template.find('#tmpl_show_sessions').html();
		_.bindAll(this, 'render');
		if (this.collection == null) {
			this.collection = new RENT.user.collection.SessionCollection();
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
	}
});
});
