// Filename: user.js
define([
  'jQuery',
  'logger',
  './view.user',
  'RentCommon',
  './view.navbar'
], function($, logger, RegisterView, RENT, NavBarView){
	RENT.user.mainView, RENT.user.navBar = null;
	var initialize,main_view;
	$(function(){
		RENT.bindLoadingPage("#supersized-loader");
	});	
	main_view=function(model, subroute){
		logger.debug('show main view');
		var _this = this;
		require(['modules/main/view.main'],function(RegisterMainView){
			var view = new RegisterMainView({
				el : '#register_content',
				model: model,
				subroute: subroute
			});			
		});
	};
	initialize  = function(subroute){
		RENT.setLangRes(RENT.getLang(), ['rent_user']);
		var model = new RENT.user.model.UserModel();
		$(function(){
			logger.debug('new register view');
			RENT.user.navBar = new NavBarView({el:'#navbar',model:model});
			RENT.user.mainView = new RegisterView({el:'#main', model:model});
			RENT.user.mainView.on('success',function(){
				main_view(model,subroute);
			});
		});
		

	};
  return {
    initialize: initialize
  };
});