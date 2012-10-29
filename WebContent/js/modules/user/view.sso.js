define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.register.html',
  './namespace.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);

RENT.user.view.ShowSSOTokenView = Backbone.View.extend({
	events:{
		'click #sign_button':'fill_form',
		'click #go_button':'do_mobile_auth_request'
	},
	GUID :function(){
		var S4 = function ()
		{
	        return Math.floor(
	                Math.random() * 0x10000 /* 65536 */
	            ).toString(16);
	    };
	    return (
	            S4() + S4() + "-" +
	            S4() 
	        );
	},
	initialize:function(){
		logger.debug('initialize show sso token view');
		_.bindAll(this, 'render');
		this.tmpl = $template.find('#tmpl_show_sso_token').html();
		//
		// i18n
		//
		var _this = this;
		this.model.on('change',this.render);
		this.model.get_sso_application_token({
			success:function(model,resp){
				logger.debug('get sso application token success');
				_this.model.set({token:model.token});
			},
			error:function(model,resp){
				logger.error('get sso application token fail');
				RENT.simpleErrorDialog(resp,'');
			}
		});
		this.model.set({
			requestId:this.GUID(),
			requestTime:Math.ceil(new Date().getTime()/1000),
			done:window.location.href,
			requestFrom:this.model.get('userId')
		});
	},
	render:function(){
		logger.debug('render get sso application token');
		var _this = this;
		this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		RENT.generateCountryOptions(this.$el.find('#countryCode'));

		RENT.initValidator(function(){
			_this.$el.find("#mobile_auth_request_form").validate();
		});

		//
		// i18n
		//
		this.$el.find('#i18n_show_token_msg').text($.i18n.prop('user.main.show_token_msg'));
	},
	fill_form:function(){
		logger.debug('click do sign');
		var formvalidate = this.$el.find("#mobile_auth_request_form").valid();
		if (!formvalidate) {
			logger.error('form validate fail');
			return;
		}
		var formArray=this.$el.find("#mobile_auth_request_form").serializeArray();
		var formObj = {};
		$.each(formArray,function(i, item){
			formObj[item.name] = item.value;
		});
		var _this = this;
		this.model.get_signature_of_mobile_auth_request(formObj,{
			success:function(model, resp){
				logger.debug('success');
				_this.$el.find('#sign').val(model.sign);
			},
			error:function(model,resp){
				logger.debug('error');
				
			}
		});
	},
	do_mobile_auth_request:function(){
		logger.debug("do submit");
		this.$el.find("#mobile_auth_request_form").submit();
	}
});
});