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
