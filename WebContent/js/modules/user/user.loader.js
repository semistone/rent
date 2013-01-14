<<<<<<< HEAD
// Filename: user.js
define([
  'jQuery',
  'logger',
  './view.user',
  'RentCommon',
  './view.navbar'
], function($, logger, RegisterView, RENT){
	RENT.user.mainView, RENT.user.navBar = null;
	$(function(){
		RENT.bindLoadingPage("#supersized-loader");
	});	
	var initialize = function(){
		RENT.setLangRes(RENT.getLang(), ['rent_user']);
		var model = new RENT.user.model.UserModel();
		$(function(){
			logger.debug('new register view');
			RENT.user.navBar = new RENT.user.view.NavBarView({el:'#navbar',model:model});
			RENT.user.mainView = new RegisterView({el:'#main', model:model});
		});
	};
  return {
    initialize: initialize
  };
});
=======
// Filename: user.js
define([
  'jQuery',
  'logger',
  'RentCommon',
  './model.device'
], function($, logger, RENT, DeviceModel){
	var initialize,
		model = new DeviceModel();
	$(function(){
		RENT.bindLoadingPage("#supersized-loader");
	});
	RENT.setLangRes(RENT.getLang(), ['rent_user']);

	initialize  = function(subroute){
		if (subroute == 'sso') {
			logger.debug('subroute is sso');
			require(['modules/user/view.sso'], function(SSOView){
				var sso_view = new SSOView({el:'#main', model:model});
				sso_view.handle_mobile_auth_request_form();				
			});
		}		
	};
	return {
		initialize:initialize
	};
});
>>>>>>> master
