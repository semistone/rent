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
    	this.model.set({
            country_code:country_code,
            mobile_phone:mobile_phone
        });

    	this.model.save();
    },

});