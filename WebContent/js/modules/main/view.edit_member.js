define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/user/tmpl.member.phtml',
  './model.member'
  ], function($, _, Backbone, Mustache, RENT, logger,template, MemberModel) {
var $template = $('<div>').append(template);	
EditMemberView = Backbone.View.extend({
	model: MemberModel,
	events:{
		'click #save_member_link': 'save_edit_member',
		'click #save_add_member_link' : 'save_add_member',
		'click #close' : 'close'
	},
	initialize : function() {
		_.bindAll(this, 'render', 'i18n', 
				  'save_edit_member', 'save_add_member', 'close');
        this.render();
	},
	i18n:function(){
		this.$el.find('#close').text(
				$.i18n.prop('general.close'));
		this.$el.find('#save_add_member_link').text(
				$.i18n.prop('general.save'));
		this.$el.find('#i18n_mobile_phone').text(
				$.i18n.prop('user.register.mobile_phone'));
		this.$el.find('#i18n_email').text(
				$.i18n.prop('general.email'));
		this.$el.find('#i18n_name').text(
				$.i18n.prop('general.name'));
	},
	render:function(){
        logger.debug('render edit member');
		var tmpl, _this = this, template_id;
        if (this.model.isNew()) {
            template_id = '#tmpl_new_member';
        } else {
		    template_id = '#tmpl_edit_member';
        }
		tmpl = $template.find(template_id).html(),
		this.$el.html(Mustache.to_html(tmpl ,this.model.toJSON()));
        this.i18n();
		RENT.initValidator(function(){
            _this.$el.find("#edit_member_form").validate();			
            _this.$el.find('#mobile-phone').rules('add', {
				regex : /^\+?\d{10,15}$/
			});
            _this.$el.find('#email').rules('add', {
				regex : /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
			});
		});		
	},
	save_edit_member:function(){
        logger.debug('save edit member');
        var formvalidate = this.$el.find("#edit_member_form").valid(), id;
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return false;
        }
		id = this.$el.find('#id').val();
		logger.debug('save id '+id);
		this.model.set({
			name: this.$el.find('#name').val(),
			email:  this.$el.find('#email').val(),
			mobilePhone:  this.$el.find('#mobile-phone').val()
		},{slient:true});
        this.model.on('change',function(){
            _this.close();
            _this.model.off('change');   
        });
		this.model.save();
	},
	save_add_member:function(){
        logger.debug('save add member');
        var formvalidate = this.$el.find("#edit_member_form").valid(), 
                _this = this;
        if (!formvalidate) {
        	logger.error('form validate fail');
        	return false;
        }	
		this.model.set({
			name: this.$el.find('#name').val(),
			email:  this.$el.find('#email').val(),
			mobilePhone:  this.$el.find('#mobile-phone').val()
		},{slient:true});
        this.model.on('change',function(){
            _this.close();     
            _this.model.off('change');   
        });
		this.model.save();
	},
    close: function(){
        logger.debug('close edit member view');
        this.undelegateEvents();
        this.trigger('close');
    }
});

return EditMemberView;
});
