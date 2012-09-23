RENT.user.view.RegisterView = Backbone.View.extend({
    initialize: function() {
    	this.tmpl = $('#tmpl_register_form').html();
    	 _.bindAll(this, 'render','new_device');
    	 this.$el=$(this.el);
    },
    render: function() { 
    	logger.debug('render register view');
    	this.$el.html(this.tmpl);
      	this.$el.find('#register_button').val($.i18n.prop('user.register'));
    },
    events: {
        "click #register_button": "new_device"
    },
    new_device:function(){
    	logger.debug('click new device button');
    	var country_code = this.$el.find('#country_code').val();
    	var mobile_phone = this.$el.find('#mobile_phone').val();
    	if (mobile_phone.substring(0,1) == '0') {
    		mobile_phone = country_code + mobile_phone.substring(1);
    	}
        var _this = this;
    	this.model.save({
            country_code:country_code,
            mobile_phone:mobile_phone
            },
            {
            success:function(model, response){
                logger.debug('step1 success');
                var step2 = new RENT.user.view.RegisterStep2View({el:_this.el ,model:this.model}); 
                step2.render();
            },
            error:function(model, response){
                logger.error('step1 error response:'+response);
            },
        });
    },

});
RENT.user.view.RegisterStep2View = Backbone.View.extend({
    events: {
        "click #verify_button": "do_verify"
    },
    initialize: function() {
    	this.tmpl = $('#tmpl_register_step2').html();
    	this.$el=$(this.el);
    	 _.bindAll(this, 'render');
    },
    render: function() { 
    	logger.debug('render register step2');
    	this.$el.html(this.tmpl);
    },
    do_verify:function(){
    	logger.debug('click do verify button');
    }
});


