RENT.user.view.RegisterView = Backbone.View.extend({
	initialize : function() {
		_.bindAll(this, 'render', 'new_device', 'step1','step2');
		this.$el = $(this.el);
		this.model.bind('change',this.render);
		this.model.fetch({
			error: this.render
		});
	},
	render : function() {
		var status =this.model.get('status')
		logger.debug("render user status:"+status);
		if (status == undefined) {
			this.step1();
		} else if (status == 0){
			this.step2();
		}
		
	},
	step1: function(){
		this.tmpl = $('#tmpl_register_form').html();
		logger.debug('render register view');
		this.$el.html(this.tmpl);
		this.$el.find('#register_button').val($.i18n.prop('user.register'));	
	},
	step2: function(){
		this.model.unbind('change');
		var step2 = new RENT.user.view.RegisterStep2View({
			el : this.el,
			model : this.model
		});
		step2.render();
	},
	events : {
		"click #register_button" : "new_device"
	},
	new_device : function() {
		logger.debug('click new device button');
		var country_code = this.$el.find('#country_code').val();
		var mobile_phone = this.$el.find('#mobile_phone').val();
		if (mobile_phone.substring(0, 1) == '0') {
			mobile_phone = country_code + mobile_phone.substring(1);
		}else if (mobile_phone.substring(0, 1) == '+'){			
			// do nothing
		}else{
			mobile_phone = country_code + mobile_phone;
		}
		var _this = this;
		this.model.save({
			country_code : country_code,
			mobile_phone : mobile_phone
		}, {
			success : function(model, response) {
				logger.debug('step1 success');
				_this.step2();
			},
			error : function(model, response) {
				logger.error('step1 error response:' + response);
			},
		});
	},

});
RENT.user.view.RegisterStep2View = Backbone.View.extend({
	events : {
		"click #verify_button" : "do_verify"
	},
	initialize : function() {
		this.tmpl = $('#tmpl_register_step2').html();
		this.$el = $(this.el);
		_.bindAll(this, 'render');
	},
	render : function() {
		logger.debug('render register step2');
		this.$el.html(this.tmpl);
	},
	do_verify : function() {
		var auth_code = this.$el.find('#auth_code').val();
		logger.debug('click do verify button auth code is '+auth_code);
		this.model.verify_mobile_auth_code(auth_code);
	}
});


