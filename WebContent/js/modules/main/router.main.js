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
		this.on('route:name_device', this.name_device_popup);
		this.on('route:show_my_device', this.show_my_device);
		this.on('route:list_members', this.list_members);
		this.on('route:mobile_provider', this.mobile_provider);
		this.on('route:show_user_profile', this.show_user_profile);
		this.on('route:sso_application', this.sso_application);

	},
	routes: {
		'name_device': 'name_device',
		'show_my_device': 'show_my_device',
		'mobile_provider' : 'mobile_provider',
		'show_user_profile' : 'show_user_profile',
		'sso_application' : 'sso_application',
		'list_members' : 'list_members'
	},
	name_device_popup:function(){
		logger.debug('click name device popup');
		this.navigate(this.path + '/name_device', {replace: true});
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
		this.navigate(this.path + '/show_my_device', {replace: true});
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
		this.navigate(this.path + '/list_members', {replace: true});
		var _this = this;
		require(['modules/main/view.list_members'],function(ListMemberView){
			new ListMemberView({el: _this.$el.find('#register_right')});
		});

	},
	mobile_provider:function(){
		logger.debug('click mobile_provider');
		this.navigate(this.path + '/mobile_provider', {replace: true});

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
		this.navigate(this.path + '/show_user_profile', {replace: true});
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
		this.navigate(this.path + '/sso_application', {replace: true});
		var _this = this;
		require(['modules/main/view.sso'],function(ShowSSOTokenView){
			new ShowSSOTokenView({
				el : _this.$el.find('#register_right'),
				userModel: _this.model
			});					
		});
	}	
});
return MainRouter;
});