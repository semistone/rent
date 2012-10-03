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
  'modules/user/namespace.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {

$('body').append(template);	
RENT.bindLoadingPage("#supersized-loader");

RENT.user.view.RegisterView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render', 'new_device_event', 'step1', 'step2','error');
		this.$el = $(this.el);
		this.model.bind('change',this.render);
		this.model.bind('error',this.error);
		if (!this.model.get("from_step2")) {
			this.model.fetch();			
		}
		$.validator.addMethod("regex", function(value, element, re) {
			return re.test(value);
		}, $.i18n.prop('rent.error.validate_format'));

	},
	error :function(model,resp){
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
		var status =this.model.get('status')
		logger.debug("render user status:"+status);
		switch (status) {
		case undefined:
			logger.debug('render register view step1');
			this.step1();
			break;
		case 0:
		case 1:
			this.step2();
			break;
		case 2:
			logger.debug('render register view step3');
			this.model.unbind();
			new RENT.user.view.RegisterStep3View({
				el : this.el,
				model: this.model
			}).render();
			break;
		default: // show ooop
			logger.error('user status is removed or suspend');
			new RENT.user.view.ErrorView({
				el : this.el
			}).render();
		}
	},
	step2: function(){
		logger.debug('render register view step2');
		this.model.unbind(); // before change view must unbind all event
		new RENT.user.view.RegisterStep2View({
			el : this.el,
			model : this.model
		}).render();
	},
	step1: function(){
		this.tmpl = $('#tmpl_register_form').html();
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		this.$el.find("#register_form").validate();
		this.$el.find('#mobile_phone').rules('add', {
			regex : /^\+?\d{10,15}$/
		});
		//
		// l10n translate
		//
		this.$el.find('#register_button').val($.i18n.prop('user.register'));
		this.$el.find('#i18n_mobile_phone').text(
				$.i18n.prop('user.register.mobile_phone'));
		this.$el.find('#i18n_country_code').text(
				$.i18n.prop('user.register.country_code'));	
		this.$el.find('#i18n_step1').text(
				$.i18n.prop('user.register.step1'));
		this.model.unbind(); // finish render 
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
			logger.debug('step1 success');
			_this.model.set({from_step1:true},{slient:true});
			_this.step2();
		};
		var error = function(model,resp) {
			logger.error('step1 error response:' + resp.status);
			RENT.simpleErrorDialog(resp);
		};
		this.model.save({
			countryCode : country_code,
			mobilePhone : mobile_phone
		}, {
			success : success,
			error : error
		});
	},

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
		this.tmpl = $('#tmpl_register_step2').html();
		this.$el = $(this.el);
		_.bindAll(this, 'render','do_verify','go_back_step1');
		this.model.bind('error',this.error);
	},
	render : function() {
		logger.debug('render register step2');
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));

		// validate setting
		this.$el.find("#register_form_step2").validate();
		this.$el.find('#auth_code').rules('add', {
			regex : /^\d{6}$/
		});
		//
		// i18n
		//
		this.$el.find('#i18n_step2').text(
				$.i18n.prop('user.register.step2'));
		this.$el.find('#i18n_enter_auth_code').text(
				$.i18n.prop('user.register.enter_auth_code'));
		this.$el.find('#verify_button').val(
				$.i18n.prop('user.register.verify'));
		this.$el.find('#before_button').val(
				$.i18n.prop('general.before'));
		
		this.$el.find('#send_mobile_auth_message').val(
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
			logger.debug("verify success " + textStatus);
			_this.model.unbind();
			new RENT.user.view.RegisterStep3View({
				el : _this.el,
				model: _this.model
			}).render();
		};
		var auth_code = this.$el.find('#auth_code').val();
		logger.debug('click do verify button auth code is '+auth_code);
		this.model.verify_mobile_auth_code(auth_code,{success:success});
	},
	error :function(model,resp){
		logger.error("verify fail ");
		RENT.simpleErrorDialog(resp, '');
	},
	go_back_step1 : function() {
		this.model.set({from_step2:true, status:undefined},{silent:true});
		new RENT.user.view.RegisterView({
			el : this.el,
			model : this.model
		}).render();
	}
});

//
// step3 view
//
RENT.user.view.RegisterStep3View = Backbone.View.extend({
	
	events : {
		"click #name_device_button" : 'name_device_popup'
	},
	render:function(){
		var step3_template = $('#tmpl_register_step3').html();
		this.$el.html(step3_template);
		//
		// i18n
		//
		this.$el.find('#i18n_step3').text(
				$.i18n.prop('user.register.step3'));
		this.$el.find('#i18n_auth_success').text(
				$.i18n.prop('user.register.auth_success'));
		this.$el.find('#i18n_show_my_devices').text(
				$.i18n.prop('user.register.show_my_devices'));		
		this.$el.find('#i18n_register_manage_tool').text(
				$.i18n.prop('user.register.register_manage_tool'));	
		this.$el.find('#i18n_named_my_devices').text(
				$.i18n.prop('user.register.named_my_devices'));	
		$('.menuItem').hover(function(){
			$(this).addClass('focus');
		},function(){
			$(this).removeClass('focus');
		})
		//
		// animation affect.
		//
		this.$el.find('#i18n_auth_success').fadeOut(3000);
	},
	name_device_popup:function(){
		logger.debug('click name device popup');
		var template = $('#tmpl_dialog_form').html();
		this.$el.append(template);
		//
		// i18n
		//
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
		this.$el.find('#dialog-form').attr('title',
				$.i18n.prop('user.register.name_device_title'));
		
		var _this = this;
		var myButtons = {};
		myButtons[$.i18n.prop('general.save')] = function(){
			logger.debug('click name device popup save');
			var name = $('#name').val();
			logger.debug('name is '+name);
			var _dialog = this;
			_this.model.name_device(name, {
				success : function() {
					logger.debug('click name device popup save success');
					$(_dialog).dialog("close");
				},
				error : function() {
					logger.debug('click name device popup save error');
					alert('error');
				}
			});
		};
		myButtons[$.i18n.prop('general.cancel')] = function(){
			$( this ).dialog( "close" );
		}
		$('#dialog-form').dialog({
			height: 300,
			width: 350,
			modal: true,
			buttons: myButtons
		});
	}
});
		

RENT.user.view.ErrorView = Backbone.View.extend({
	render:function(){
		var error_template = $('#tmpl_register_error').html();
		this.$el.html(error_template);
		//
		// i18n
		//
		this.$el.find('#i18n_error').text(
				$.i18n.prop('user.register.error'));
	}
});

return RENT.user.view.RegisterView;
});