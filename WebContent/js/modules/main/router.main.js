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

		this.on('route:name_device', this.name_device);
		this.on('route:show_my_device', this.show_my_device);
		this.on('route:mobile_provider', this.mobile_provider);
		this.on('route:show_user_profile', this.show_user_profile);
		this.on('route:sso_application', this.sso_application);
		this.on('route:list_members', this.list_members);
		this.on('route:whereami', this.whereami);
	},
	routes: {
		'main/name_device': 'name_device',
		'main/show_my_device': 'show_my_device',
		'main/mobile_provider' : 'mobile_provider',
		'main/show_user_profile' : 'show_user_profile',
		'main/sso_application' : 'sso_application',
		'main/list_members' : 'list_members',
		'main/whereami': 'whereami'
	},
	name_device:function(){
		logger.debug('click name device popup');
		var _this = this;
		require(['modules/main/view.name_device'],function(NameDeviceView){
			var view = new NameDeviceView({
				el : _this.$el.find('#register_right'),
				model : _this.model});
			view.render();
			view.on('success',function(){
				_this.navigate('main/show_my_device');
			});			
		});
	},
	show_my_device:function(){
		logger.debug('click show my devies'); 
		var _this = this;
		require(['modules/main/view.devices'],function(){
			new RENT.user.view.ShowDevicesView({
				el : _this.$el.find('#register_right'),
				model : _this.model
			});			
		});
	},
	list_members:function(){
		logger.debug('click list_members');
		var _this = this;
		require(['modules/main/view.list_members'],function(ListMemberView){
			new ListMemberView({el: _this.$el.find('#register_right')});
		});

	},
	mobile_provider:function(){
		logger.debug('click mobile_provider');

		var _this = this;
		require(['modules/main/view.mobile_provider'],function(){
			var view = new RENT.user.view.MobileProviderView({
				el: _this.$el.find('#register_right')
			});
			view.render();
		});		
		
	},
	show_user_profile:function(){
		logger.debug('click show_user_profile');
		var _this = this;
		require(['modules/main/view.profile'],function(UserProfileView){
			var view = new UserProfileView({
				el: _this.$el.find('#register_right'),
				model:_this.model
			});
			view.render();
		});		
	},

	sso_application:function(){
		logger.debug('click show sso application'); 
		var _this = this;
		require(['modules/main/view.sso'],function(ShowSSOTokenView){
			new ShowSSOTokenView({
				el : _this.$el.find('#register_right'),
				userModel: _this.model
			});					
		});
	},
	whereami:function(){
		logger.debug('click where am i'); 
		var _this = this;
		require(['modules/google/view.map'],function(Map){
			var map = new Map({
				el : _this.$el.find('#register_right')
			});
			map.whereami();
		});	
	}
});
return MainRouter;
});
