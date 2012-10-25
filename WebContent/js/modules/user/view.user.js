//
// step1 view
//
define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.register.html',
  './namespace.user',
  './model.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

var $template = $('<div>').append(template);
var mobileAuthRequestForm = null;
RENT.user.view.RegisterView = Backbone.View.extend({

	initialize : function() {
		_.bindAll(this, 'render', 'error','change_view','verify_success','main_view');
		if (this.model == null) {
			this.model = new RENT.user.model.UserModel();
		}
		this.model.bind('change',this.render);
		this.model.bind('change_view',this.change_view);
		this.model.bind('verify_success',this.verify_success);
		if (!this.handleMobileAuthRequestForm()){
			//only no mobile auth request need to do fetch.
			this.model.fetch({error:this.error});			
		}
	},
	handleMobileAuthRequestForm:function(){
		mobileAuthRequestForm = RENT.getQueryVariables();
		var _this = this;
		if (mobileAuthRequestForm['requestId'] != null) {
			logger.debug('deal with request');
			this.model.mobile_auth_request(mobileAuthRequestForm,{
				success:function(model,response){
					logger.debug('success');					
					_this.model.set(model);
				},
				error:function(resp){
					if (resp != null && resp.status == 409) {
						_this.model.set({status:1});
					} else {
						_this.error(_this.model,resp);						
					}
				}
			});
			return true;
		} else {
			mobileAuthRequestForm = null; //reset to null
			return false;			
		}
	},
	error :function(model,resp){
		logger.debug("fetch error");
		var status = resp.status;
		if (status == 404) { // no such user, goto step1
			this.render();			
			return;
		} else {
			new RENT.user.view.ErrorView({
				el : this.el
			}).render();
		}
	},
	render : function() {
		this.$el.html($template.find('#tmpl_register_form').html());
		var status =this.model.get('status');
		logger.debug("render user status:"+status);
		if (mobileAuthRequestForm != null && 
			(status == undefined || status == 0) &&
			this.model.get('userId') != null
		) {
			status = 1;
		}
		switch (status) {
		case undefined:
			logger.debug('render register view step1');
			this.model.unbind('change'); // before change view must unbind all event
			new RENT.user.view.RegisterStep1View({
				el : '#register_content',
				model : this.model
			}).render();
			break;
		case 0:
		case 1:
			logger.debug('render register view step2');
			this.model.unbind('change'); 
			new RENT.user.view.RegisterStep2View({
				el : '#register_content',
				model : this.model
			}).render();
			break;
		case 2:
			logger.debug('render register view step3');
			this.model.unbind('change'); 
			RENT.user.dotDone(mobileAuthRequestForm, this.model.toJSON()); // if redirect to dot done page.
			this.main_view();
			break;
		default: // show ooop
			logger.error('user status is removed or suspend');
			new RENT.user.view.ErrorView({
				el : this.el
			}).render();
		}
	},
	change_view:function(view_name){
		logger.debug("change view event "+view_name);
		switch (view_name) {
		case 'step1':
			this.$el.find('#register_title').text($.i18n.prop('user.register.step1'));
			break;
		case 'step2':
			this.$el.find('#register_title').text($.i18n.prop('user.register.step2'));
			break;
		case 'step3':
			this.$el.find('#register_title').text($.i18n.prop('user.register.step3'));
			break;
		case 'main':
			this.$el.find('#register_title').text($.i18n.prop('user.register.register_manage_tool'));
			break;			

		}
	},
	verify_success:function(){
		this.$el.find('#register_title').text($.i18n.prop('user.register.step3'));
		if (this.model.get('name') == null) {
			RENT.user.dotDone(mobileAuthRequestForm, this.model.toJSON()); // if redirect to dot done page.
			var view = new RENT.user.view.NameDeviceView({
				el: '#register_content',
				model: this.model
			});
			view.render();
			this.model.trigger('change_view','step3');
			var _this = this;
			view.on('success',function(){
				_this.main_view();
				view.off('success');
			});			
		} else {
			this.main_view();
		}
	},
	main_view:function(){
		logger.debug('show main view');
		var view = new RENT.user.view.RegisterMainView({
			el : '#register_content',
			model: this.model
		});
		view.render();
	}
});
//
// step1
//
RENT.user.view.RegisterStep1View = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render', 'new_device_event');
		this.tmpl = $template.find('#tmpl_register_step1').html();
	},
	render: function(){
		this.model.trigger('change_view','step1');
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		RENT.generateCountryOptions(this.$el.find('#country_code'));
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#register_form").validate();			
			_this.$el.find('#mobile_phone').rules('add', {
				regex : /^\+?\d{10,15}$/
			});
		});
		//
		// l10n translate
		//
		this.$el.find('#i18n_register_button').text($.i18n.prop('user.register'));
		this.$el.find('#i18n_mobile_phone').text(
				$.i18n.prop('user.register.mobile_phone'));
		this.$el.find('#i18n_country_code').text(
				$.i18n.prop('user.register.country_code'));	


	},

	events : {
		"click #register_button" : "new_device_event"
	},
	
	new_device_event : function() {
		logger.debug('click new device button');
        var formvalidate = this.$el.find("#register_form").valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
		var country_code = this.$el.find('#country_code').val();
		var mobile_phone = this.$el.find('#mobile_phone').val();
		// simple verify
		if (mobile_phone.substring(0, 1) == '0') {
			mobile_phone = country_code + mobile_phone.substring(1);
		}else if (mobile_phone.substring(0, 1) == '+'){			
			mobile_phone = mobile_phone.substring(1);
		}else{
			mobile_phone = country_code + mobile_phone;
		}
		var _this = this;

		logger.debug('do save');
		var success = function(model, response) {
			logger.debug('render register view step2');
			_this.undelegateEvents();
			if (model.status == 2) {
				logger.debug('device has authed');
				_this.model.trigger('verify_success');
				return;
			}
			new RENT.user.view.RegisterStep2View({
				el : _this.el,
				model : _this.model
			}).render();
		};
		var error = function(resp) {
			logger.error('step1 error response:' + resp.status);
			RENT.simpleErrorDialog(resp);
		};
		this.model.new_device({
			countryCode : country_code,
			mobilePhone : mobile_phone
		}, {
			success : success,
			error : error
		});
	}
});
//
// step2 view 
//
RENT.user.view.RegisterStep2View = Backbone.View.extend({
	events : {
		"click #verify_button" : 'do_verify',
		"click #send_mobile_auth_message" : 'send_mobile_auth_message',
		'click #before_button' : 'go_back_step1'
	},
	initialize : function() {
		this.tmpl = $template.find('#tmpl_register_step2').html();
		_.bindAll(this, 'render','do_verify','go_back_step1');
		this.model.bind('error',this.error);
	},
	render : function() {
		this.model.trigger('change_view','step2');
		logger.debug('render register step2');
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));

		// validate setting
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#register_form_step2").validate();
			_this.$el.find('#auth_code').rules('add', {
				regex : /^\d{6}$/
			});
		});
		//
		// i18n
		//
		$('#register_title').text($.i18n.prop('user.register.step2'));
		
		this.$el.find('#i18n_enter_auth_code').text(
				$.i18n.prop('user.register.enter_auth_code'));
		this.$el.find('#i18n_verify_button').text(
				$.i18n.prop('user.register.verify'));
		this.$el.find('#i18n_before_button').text(
				$.i18n.prop('general.before'));
		
		this.$el.find('#i18n_send_mobile_auth_message').text(
				$.i18n.prop('user.register.send_mobile_auth_message'));
	},
	send_mobile_auth_message :function(){
		logger.debug('click send mobile message');
		this.model.send_mobile_auth_message({
			success : function(model, resp) {
				logger.debug("send success");
				RENT.simpleDialog('',$.i18n.prop('user.register.send_success'));
			},
			error : function(resp, text) {
				RENT.simpleErrorDialog(resp,'');
			}
		});
	},
	do_verify : function() {
		var success, error,_this;
		_this = this;
        var formvalidate = this.$el.find("#register_form_step2").valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
		success = function(data, textStatus, jqXHR) {
			logger.debug("verify success ");
			_this.undelegateEvents();
			_this.model.trigger('verify_success');
		};
		var auth_code = this.$el.find('#auth_code').val();
		logger.debug('click do verify button auth code is '+auth_code);
		if (mobileAuthRequestForm == undefined || mobileAuthRequestForm == null) {
			this.model.verify_mobile_auth_code(auth_code,{success:success});			
		} else {
			this.model.verify_mobile_auth_request_code(auth_code,{success:success});
		}
	},
	error :function(model,resp){
		logger.error("verify fail ");
		RENT.simpleErrorDialog(resp, '');
	},
	go_back_step1 : function() {
		this.undelegateEvents();
		new RENT.user.view.RegisterStep1View({
			el : this.el,
			model : this.model
		}).render();
	}
});
//
// Register main view
//
RENT.user.view.RegisterMainView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render','sign_off','show_my_device');
		this.tmpl = $template.find('#tmpl_register_step3').html();
		this.rightView = new Backbone.View();
		var _this = this;
		this.on('sign_off',function(){
			new RENT.user.view.RegisterStep1View({el:_this.el,model:_this.model}).render();
			_this.off('sign_off');
		});
		//
		// add fb module
		//
		require(['modules/user/model.fb'],function(){
			var fb = new RENT.user.model.FBModel();
			fb.on('change',function(){
				var tmpl = $template.find('#tmpl_fb_info').html();
				$('#user_info').html(Mustache.to_html(tmpl,fb.toJSON() ));
			});
		});
	},
	events : {
		"click #named_my_devices_link" : 'name_device_popup',
		'click #sign_off_link' : 'sign_off',
		'click #show_my_devices_link' : 'show_my_device'
	},
	render:function(){
		this.model.trigger('change_view','main');
		this.$el.html(this.tmpl);
		//
		// i18n
		//
		this.$el.find('#i18n_auth_success').text(
				$.i18n.prop('user.register.auth_success'));
		this.$el.find('#i18n_show_my_devices').text(
				$.i18n.prop('user.register.show_my_devices'));		
		this.$el.find('#i18n_named_my_devices').text(
				$.i18n.prop('user.register.named_my_devices'));	
		this.$el.find('#i18n_delete_device').text(
				$.i18n.prop('user.register.delete_device'));
		
		$('.menuItem').hover(function(){
			$(this).addClass('focus');
		},function(){
			$(this).removeClass('focus');
		});

	},
	name_device_popup:function(){
		logger.debug('click name device popup');
		this.rightView.undelegateEvents();
		this.rightView = new RENT.user.view.NameDeviceView({
			el : this.$el.find('#register_right'),
			model : this.model});
		this.rightView.render();
		var _this = this;
		this.rightView.on('success',function(){
			_this.show_my_device();
		});
	},
	sign_off:function(){
		logger.debug('click signoff device');
		var _this = this;
		this.model.sign_off({
			success:function(model,resp){
				_this.rightView.undelegateEvents();
				_this.undelegateEvents();
				_this.trigger('sign_off');
			},
			error:function(model,resp){
				RENT.simpleErrorDialog(resp,'');
			}
		});	
	},
	show_my_device:function(){
		logger.debug('click show my devies'); 
		this.rightView.undelegateEvents();
		this.rightView = new RENT.user.view.ShowDevicesView({
			el : this.$el.find('#register_right'),
			model : this.model
		});
	}
});

RENT.user.view.ErrorView = Backbone.View.extend({
	initialize : function() {
		this.tmpl = $template.find('#tmpl_register_error').html();
	},
	render:function(){
		this.$el.html(this.tmpl);
		//
		// i18n
		//
		this.$el.find('#i18n_error').text(
				$.i18n.prop('user.register.error'));
	}
});
//
// name device form
//
RENT.user.view.NameDeviceView = Backbone.View.extend({
	events : {
		"click #save_name_link" : 'save_name'
	},
	initialize : function() {
		this.tmpl = $template.find('#tmpl_name_device_form').html();
		_.bindAll(this, 'render','save_name');
	},
	save_name:function(){
		logger.debug('click name device popup save');
        var formvalidate = this.$el.find('#name_device_form').valid();
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return;
        }
		var name = $('#device_name').val();
		logger.debug('name is '+name);
		var _this = this;
		this.model.name_device(name, {
			success : function() {
				logger.debug('click name device popup save success');
				_this.trigger("success");
			},
			error : function() {
				logger.debug('click name device popup save error');
				alert('error');
			}
		})	
	},
	render:function(){
		this.model.trigger('change_view','name_device');
		this.$el.html(this.tmpl);
		var _this = this;
		RENT.initValidator(function(){
			_this.$el.find("#name_device_form").validate();
			_this.$el.find('#device_name').rules('add', {
				regex : /^[^\<\(\)]*$/
			});
		});				
		//
		// i18n
		//
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#dialog_form_block').attr('title',
				$.i18n.prop('user.register.name_device_title'));
	}
	
});
//
// device list
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
		this.$el.html(Mustache.to_html(this.tmpl,obj ));
		//
		// i18n
		//
		this.$el.find('#i18n_devices').text(
				$.i18n.prop('user.register.show_devices'));
		this.$el.find('.i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('.i18n_id').text(
				$.i18n.prop('general.id'));
		this.$el.find('#i18n_last_login_ip').text(
				$.i18n.prop('user.register.last_login_ip'));
		this.$el.find('#i18n_last_login_time').text(
				$.i18n.prop('user.register.last_login_time'));
		
		
	},
	list_sessions:function(ev){
		var deviceId = $(ev.target).parent().attr('id');
		logger.debug("list sessions device id "+deviceId); 
		this.undelegateEvents();
		this.model.set({deviceId:deviceId},{silent:true});
		new RENT.user.view.ShowSessionsView({
			el : this.$el,
			model : this.model
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
			this.collection =new RENT.user.collection.SessionCollection();
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
		}
		this.collection.on('reset',this.render);
		this.collection.on('remove',this.render);
	},
	render:function(){
		this.model.trigger('change_view','show_sessions');
		logger.debug("render sessions");
		var sessions = this.collection.toJSON();
        $.each(sessions,function(index, row){
        	logger.debug('created is '+row.created);
        	var date = new Date(row.created * 1000);
        	var then = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDay();
                then += ' '+date.getHours()+':'+date.getMinutes();
            row['createdDate'] = then;
        });    
        this.$el.html(Mustache.to_html(this.tmpl,{sessions:sessions} ));
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
return RENT.user.view.RegisterView;
});