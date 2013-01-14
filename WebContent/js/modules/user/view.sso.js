define([
<<<<<<< HEAD
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!../../../html/user/tmpl.sso.html',
  './namespace.user',
  './model.sso'
  ], function($, _, Backbone, Mustache, RENT, logger,template) {
var $template = $('<div>').append(template);

RENT.user.view.ShowSSOTokenView = Backbone.View.extend({
	hasRole : false,
	events:{
		'click #sign_button':'fill_form',
		'click #go_button':'do_mobile_auth_request',
		'click #apply_token_link': 'apply_token'
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
		this.userModel = this.options.userModel;
		delete this.options.userModel;
		if (this.userModel == null ||this.userModel == undefined) {
			logger.error('user model not defined');
			return;
		}
		this.model = new RENT.user.model.RequestModel();
		_.bindAll(this, 'render', 'show_token','apply_token');
		//
		// i18n
		//
		var _this = this;
		var user = this.userModel.get('user');
		if (user.roles == undefined) {
			this.userModel.get_roles({
				success:function(){
					if (user.roles && _.contains(user.roles,5)) {
						logger.debug('sso role exist');
						_this.hasRole = true;
						_this.show_token();
					} else {
						_this.render();
					}
				},
				error:function(model, resp){
					logger.error('get roles fail');
					RENT.simpleErrorDialog(resp,'');
				}
			});
		} else {
			if (user.roles && _.contains(user.roles,5)) {
				this.hasRole = true;
				this.show_token();
			}
		}
		this.model.on('change',this.render);
		this.model.set({
			requestId:this.GUID(),
			requestTime:Math.ceil(new Date().getTime()/1000),
			done:window.location.href,
			requestFrom:this.userModel.get('userId')
		});
	},
	show_token:function(){
		var _this = this;		
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
	},

	render:function(){
		logger.debug('render get sso application token');
		var _this = this;
		if (this.hasRole) {
			this.tmpl = $template.find('#tmpl_show_sso_token').html();
			this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
			RENT.generateCountryOptions(this.$el.find('#countryCode'));

			RENT.initValidator(function(){
				_this.$el.find("#mobile_auth_request_form").validate();
			});

			//
			// i18n
			//
			this.$el.find('#i18n_show_token_msg').text($.i18n.prop('user.main.show_token_msg'));			
		} else {
			logger.debug('no role');
			this.tmpl = $template.find('#tmpl_apply_token').html();
			this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));
		}
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
		this.model.get_signature_of_mobile_auth_request({
			success:function(model, resp){
				logger.debug('success');
				_this.$el.find('#sign').val(model.sign);
			},
			error:function(model,resp){
				logger.error('get_signature_of_mobile_auth_request error');
				RENT.simpleErrorDialog(resp,'');
			}
		});
	},
	do_mobile_auth_request:function(){
		logger.debug("do submit");
		this.$el.find("#mobile_auth_request_form").submit();
	},
	apply_token:function(){
		logger.debug("apply token");
		var _this = this;
		this.model.apply_sso_application({
			success:function(model,resp){
				logger.debug('apply sso success');
				var user = _this.model.get('user');
				user.roles.push('5'); // push role 5 is sso application
				_this.hasRole = true;
				_this.show_token();
			},
			error:function(model,resp){
				logger.error('apply_sso_application error');
				RENT.simpleErrorDialog(resp,'');

			}
		});
	}
});
=======
	'Underscore',
	'./view.user',
	'RentCommon',
	'logger'
  ], function(_, UserView, RENT, logger) {
var SSOView;
SSOView = UserView.extend({ 
	initialize:function(){
		logger.debug('SSOView initialize');
		this.constructor.__super__.initialize.apply(this);
		_.bindAll(this, 'dot_done', 'render', 'handle_mobile_auth_request_form');
		this.on('login_success', this.dot_done);
	},
	handle_mobile_auth_request_form:function(){
		this.mobileAuthRequestForm = RENT.getQueryVariables();
		var _this = this;
		if (this.mobileAuthRequestForm['requestId'] != null) {
			logger.debug('deal with request');
			this.model.mobile_auth_request(this.mobileAuthRequestForm,{
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
			this.mobileAuthRequestForm = null; //reset to null
			return false;			
		}
	},
	dot_done:function(model){
		//
		// redirect back to .done
		//		  
		if (this.mobileAuthRequestForm != null && this.mobileAuthRequestForm['done'] != undefined) {
			logger.info('done exist '+this.mobileAuthRequestForm['done']);
			var field = ['requestId','responseTime','status','sign'];
			var params = '?';
			$.each(model.toJSON(), function(key,value){
				if ($.inArray(key,field) >=0 ){
					params += key+'='+encodeURIComponent(value)+'&';
				}
			});
			window.location.replace(this.mobileAuthRequestForm['done']+ params);
			return;
		}		
	},
	render:function(){
		logger.debug('sso view render');
		var status =this.model.get('status');
		if (this.mobileAuthRequestForm != null && 
				(status == undefined || status == 0) &&
				this.model.get('userId') != null
		) {
			logger.debug('set status = 1 to prevent step1');
			this.model.set({status:1}, {silent:true});
		}
		this.constructor.__super__.render.apply(this);
	}
	
});
return SSOView;
>>>>>>> master
});