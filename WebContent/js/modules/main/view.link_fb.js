define([
  'jQuery',
  'Underscore',
  'Backbone',
  'Mustache',
  'RentCommon',
  'logger',
  'text!template/main/tmpl.link_fb.phtml',
  '../user/model.fb'
  ], function($, _, Backbone, Mustache, RENT, logger,template, FBModel) {
var LinkFbView, $template = $('<div>').append(template);
LinkFbView = Backbone.View.extend({
    initialize : function() {
		this.userModel = this.options.userModel;
		this.model = new FBModel();
        logger.debug('initialize link fb view');
        _.bindAll(this, 'do_link_fb');
		logger.debug('link to fb');
        var _this = this;
        //
        // do login 
        //
        this.model.on('change', this.do_link_fb);
        this.model.on('not_connected', function(){
            _this.model.login();
        });
    }, 
    do_link_fb : function(){
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

