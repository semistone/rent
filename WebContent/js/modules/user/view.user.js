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

RENT.user.view.RegisterView = Backbone.View.extend({

	initialize : function() {
		_.bindAll(this, 'render', 'error','change_view','verify_success','main_view');
		if (this.model == null) {
			this.model = new RENT.user.model.UserModel();
		}
		this.model.on('change',this.render);
		this.model.on('change_view',this.change_view);
		this.model.on('verify_success',this.verify_success);
		if (!this.handleMobileAuthRequestForm()){
			//only no mobile auth request need to do fetch.
			this.model.fetch({error:this.error});			
		}
	},
	handleMobileAuthRequestForm:function(){
		RENT.user.mobileAuthRequestForm = RENT.getQueryVariables();
		var mobileAuthRequestForm = RENT.user.mobileAuthRequestForm;
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
			RENT.user.mobileAuthRequestForm = null; //reset to null
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
		if (RENT.user.mobileAuthRequestForm != null && 
			(status == undefined || status == 0) &&
			this.model.get('userId') != null
		) {
			status = 1;
		}
		var _this = this;
		switch (status) {
		case undefined:
			logger.debug('render register view step1');
			this.model.unbind('change'); // before change view must unbind all event
			require(['modules/user/view.step1'],function(){
				new RENT.user.view.RegisterStep1View({
					el : '#register_content',
					model : _this.model
				}).render();				
			});
			break;
		case 0:
		case 1:
			logger.debug('render register view step2');
			this.model.unbind('change'); 
			require(['modules/user/view.step2'],function(){
				new RENT.user.view.RegisterStep2View({
					el : '#register_content',
					model : _this.model
				}).render();
			});
			break;
		case 2:
			logger.debug('render register view step3');
			this.model.unbind('change'); 
			RENT.user.dotDone(RENT.user.mobileAuthRequestForm, this.model.toJSON()); // if redirect to dot done page.
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
		RENT.user.dotDone(RENT.user.mobileAuthRequestForm, this.model.toJSON()); // if redirect to dot done page.
		if (this.model.get('name') == null) {
			var _this = this;
			require(['modules/user/view.name_device'],function(){
				var view = new RENT.user.view.NameDeviceView({
					el: '#register_content',
					model: _this.model
				});
				view.render();
				_this.model.trigger('change_view','step3');
				view.on('success',function(){
					view.undelegateEvents();
					_this.main_view();
					view.off('success');
				});			
			});
		} else {
			this.main_view();
		}
	},
	main_view:function(){
		logger.debug('show main view');
		var _this = this;
		require(['modules/user/view.main'],function(){
			var view = new RENT.user.view.RegisterMainView({
				el : '#register_content',
				model: _this.model,
				subroute: _this.options['subroute']
			});
			view.render();			
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

return RENT.user.view.RegisterView;
});