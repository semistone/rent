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