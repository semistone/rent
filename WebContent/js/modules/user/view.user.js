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
  'text!template/user/tmpl.register.phtml',
  './model.user',
  './namespace.user'
  ], function($, _, Backbone, Mustache, RENT, logger,template, UserModel) {

var $template = $('<div>').append(template),RegisterView;
RegisterView = Backbone.View.extend({

	initialize : function() {
		_.bindAll(this, 'render', 'error','change_view','verify_success','logoff', 'login');
		if (this.model == null) {
			this.model = new UserModel();
		}
		this.model.on('change',this.render);
		this.model.on('change_view',this.change_view);
		this.model.on('verify_success',this.verify_success);

	},
	login:function(){
		if (this.model.get('status') != null && this.model.get('status') != undefined) {
			this.render();
		} else {
			this.model.fetch({error:this.error});			
		}
	},
	logoff:function(){
		var _this = this;
		this.model.sign_off({
			success:function(model,resp){
				_this.model.clear();
				_this.render();
				_this.model.trigger('logoff_success');				
			},
			error:function(model,resp){
				RENT.simpleErrorDialog(resp,'');
			}
		});	
	},

	error :function(model,resp){
		logger.debug("fetch error");
		var status = resp.status;
		if (status == 404) { // no such user, goto step1
			this.render();			
			return;
		} else {
			var _this = this;
			require(['modules/general/view.oops'], function(ErrorView){
				new ErrorView({
					el : this.el
				}).render();				
			});
		}
	},
	render : function() {
		this.$el.html($template.find('#tmpl_register_form').html());
		var status =this.model.get('status');
		logger.debug("render user status:"+status);
		var _this = this;
		switch (status) {
		case undefined:
			logger.debug('render register view step1');
			this.model.off('change'); // before change view must unbind all event
			require(['modules/user/view.step1'],function(RegisterStep1View){
				new RegisterStep1View({
					el : '#register_content',
					model : _this.model
				}).render();				
			});
			break;
		case 0:
		case 1:
			logger.debug('render register view step2');
			this.model.off('change'); 
			require(['modules/user/view.step2'],function(RegisterStep2View){
				new RegisterStep2View({
					el : '#register_content',
					model : _this.model
				}).render();
			});
			break;
		case 2:
			logger.debug('render register view step3');
			this.model.off('change'); 
			this.trigger('login_success', this.model);
			break;
		default: // show ooop
			logger.error('user status is removed or suspend');
			require(['modules/general/view.oops'], ErrorView);
			new ErrorView({
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
		}
	},
	verify_success:function(){
		this.$el.find('#register_title').text($.i18n.prop('user.register.step3'));
		if (this.model.get('name') == null && this.mobileAuthRequestForm == null) {
			logger.debug('device name not exist');
			var _this = this;
			require(['modules/main/view.name_device'],function(NameDeviceView){
				var view = new NameDeviceView({
					el: '#register_content',
					model: _this.model
				});
				view.render();
				_this.model.trigger('change_view','step3');
				view.on('success',function(){
					logger.debug('name device success');
					view.undelegateEvents();
					_this.trigger('login_success', _this.model);
				});			
			});
		} else {
			logger.debug('device name  exist');
			this.trigger('login_success', this.model);
		}
	}

});
return RegisterView;
});
