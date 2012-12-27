define([
  'jQuery',
  'Underscore',
  'Backbone',
  'RentCommon',
  'logger'
], function($, _, Backbone, RENT,logger) {

var MainRouter = Backbone.Router.extend({
	initialize :function(options){
		this.$el = options['$el'];
		this.model = options['model'];
		this.path = options['path'];

		this.on('route:name_device', this._name_device);
		this.on('route:show_my_device', this._show_my_device);
		this.on('route:mobile_provider', this._mobile_provider);
		this.on('route:show_user_profile', this._show_user_profile);
		this.on('route:sso_application', this._sso_application);
		this.on('route:list_members', this._list_members);
		this.on('route:whereami', this._whereami);
		this.on('route:link_fb', this._link_fb);
	},
	routes: {
		'main/name_device': 'name_device',
		'main/show_my_device': 'show_my_device',
		'main/mobile_provider' : 'mobile_provider',
		'main/show_user_profile' : 'show_user_profile',
		'main/sso_application' : 'sso_application',
		'main/list_members' : 'list_members',
		'main/whereami': 'whereami',
		'main/link_fb': 'link_fb'
	},
	_name_device:function(){
		logger.debug('click name device popup');
		var _this = this;
		require(['modules/main/view.name_device'],function(NameDeviceView){
			var view = new NameDeviceView({
				tag : 'div',
				model : _this.model});
			view.render();
			view.on('success',function(){
				_this.navigate('main/show_my_device');
			});
			_this.$el.find('#register_right').html(view.$el);
		});
	},
	_show_my_device:function(){
		logger.debug('click show my devies'); 
		var _this = this;
		require(['modules/main/view.devices'],function(ShowDevicesView){
			var view = new ShowDevicesView({
				tag : 'div',
				model : _this.model
			});	
			_this.$el.find('#register_right').html(view.$el);
		});
	},
	_list_members:function(){
		logger.debug('click list_members');
		var _this = this;
		require(['modules/main/view.list_members'],function(ListMemberView){
			var view = new ListMemberView({tag : 'div'});
			_this.$el.find('#register_right').html(view.$el);
		});

	},
	_mobile_provider:function(){
		logger.debug('click mobile_provider');

		var _this = this;
		require(['modules/main/view.mobile_provider'],function(MobileProviderView){
			var view = new MobileProviderView({
				tag: 'div'
			});
			view.render();
			_this.$el.find('#register_right').html(view.$el);
		});		
		
	},
	_show_user_profile:function(){
		logger.debug('click show_user_profile');
		var _this = this;
		require(['modules/main/view.profile'],function(UserProfileView){
			var view = new UserProfileView({
				tag : 'div',
				model:_this.model.get_user()
			});
			view.render();
			_this.$el.find('#register_right').html(view.$el);
		});		
	},

	_sso_application:function(){
		logger.debug('click show sso application'); 
		var _this = this;
		require(['modules/main/view.sso'],function(ShowSSOTokenView){
			var view = new ShowSSOTokenView({
				tag : 'div',
				userModel: _this.model
			});
			_this.$el.find('#register_right').html(view.$el);
		});
	},
	_whereami:function(){
		logger.debug('click where am i'); 
		var _this = this;
		require(['modules/google/view.map'],function(Map){
			var map = new Map({
				tag : 'div',
			});
			map.whereami();
			_this.$el.find('#register_right').html(map.$el);
		});	
	},
    _link_fb : function(){
        var _this = this;
        require(['modules/main/view.link_fb'], function(LinkFbView){
            var view = new LinkFbView({
                tag : 'div',
                model : _this.model
            });	
            _this.$el.find('#register_right').html(view.$el);
        });
    }
});
return MainRouter;
});
