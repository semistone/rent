// Filename: user.js
define([
  'jQuery',
  'logger',
  'RentCommon',
  '../user/view.user'
], function($, logger, RENT, User){
	var main_view, initialize, user_view, is_initialize = false;

	RENT.setLangRes(RENT.getLang(), ['rent_user']);
	user_view = new User({el:'#main'});
	
	main_view=function(DeviceModel, subroute){
		logger.debug('show main view');
		require(['modules/main/view.main', 'modules/user/view.navbar'],
			function(RegisterMainView, NavBarView){
				var nav_bar, user, main_view;
				nav_bar = new NavBarView({el:'#navbar',model:DeviceModel});
				main_view = new RegisterMainView({
					el : '#main',
					model: DeviceModel,
					subroute: subroute
				});
				main_view.on('logoff',function(){
					user_view.logoff();
				});
				//
				// add fb module
				//
				user = user_model.get('user');
				if (user != undefined && user.loginType == 'FB') {
					var id = null;
					id = user_model.get('user').loginId;
					logger.debug('loginType is FB and user.loginId is '+id);
					require(['modules/user/model.fb'],function(FBModel){
						nav_bar.initFBModel(new FBModel({id:id}));
					});
				};				
				
			}
		);
	};
	initialize  = function(subroute){
		if (is_initialize == false) {
			user_view.on('login_success', function(model){
				logger.debug('new register view');
				main_view(model,subroute);		
			});
			is_initialize = true;
		}
		user_view.login();			
	};
	return {
		initialize: initialize,
	};
});
