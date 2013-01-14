define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/user/tmpl.link_fb.phtml',
  '../user/model.fb'
  ], function($, _, Backbone, Mustache, RENT, logger,template, FBModel) {
var LinkFbView, $template = $('<div>').append(template);
LinkFbView = Backbone.View.extend({
	events:{
		'click #link_fb_ok' : 'link_fb_ok'
	},
    initialize : function() {
		this.userModel = this.options.userModel;
		this.model = new FBModel();
        logger.debug('initialize link fb view');
        _.bindAll(this, 'render', 'link_fb_ok');
		this.tmpl = $template.find('#tmpl_link_fb').html();
		var _this = this;
        //
        // do login 
        //
        this.model.on('change', this.render);
        this.model.on('not_connected', function(){
            _this.model.login();
        });
        //
        // prevent model.on after change event trigger.
        //
        if (this.model.get('connected') == true) {
            this.render();
        }
    }, 
    render : function(){
        logger.debug('render link fb');
        this.$el.html(Mustache.to_html(this.tmpl, this.model.toJSON()));	
        this.i18n();
        this.is_render = true;
    },
    i18n: function(){
        this.$el.find('#link_fb_ok').text($.i18n.prop('general.OK'));
        this.$el.find('#i18n_link_fb_msg2').text($.i18n.prop('user.link_fb.msg2'));
        this.$el.find('#i18n_link_fb_msg1').text($.i18n.prop('user.link_fb.msg1'));
        this.$el.find('#i18n_warning').text($.i18n.prop('general.warning'));        
    },
    link_fb_ok : function(){
        this.userModel.get_user().link_facebook(this.model.get('id'), this.model.get('name'), {
            success : function(model, resp) {
                logger.debug('link fb success');	
                $('#link_to_fb_link').hide();
                RENT.simpleDialog($.i18n.prop('user.main.fb_link_success'), '');
            },
            error : function(model, resp) {
                logger.debug('link fb fail');
                RENT.simpleErrorDialog(resp,'');
            }
        });
    }
});
return LinkFbView;
});

