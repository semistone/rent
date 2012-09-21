RENT.user.view.RegisterView = Backbone.View.extend({
    initialize: function() {
    	this.tmpl = $('#tmpl_register_form').html();
    	 _.bindAll(this, 'render');
    	 this.$el=$(this.el);
    },
    render: function() { 
    	logger.debug('render register view');
    	this.$el.html(this.tmpl);
      	this.$el.find('#register_button').val($.i18n.prop('user.register'));
    }
    
});