// Filename: user.js
define([
  'jQuery',
  'logger',
  './view.user',
  'RentCommon',
  './view.navbar'
], function($, logger, RegisterView, RENT){
	$(function(){
		RENT.bindLoadingPage("#supersized-loader");
	});	
	var initialize = function(){
		RENT.setLangRes(RENT.getLang(), ['rent_user']);

		$(function(){
			logger.debug('new register view');
			var register = new RegisterView({el:'#main'});
			new RENT.user.view.NavBarView({el:'#navbar',model:register.model});
		});
  }
  return {
    initialize: initialize
  };
});