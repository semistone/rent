// Filename: user.js
define([
  'jQuery',
  'logger',
  'RentCommon',
  './view.main',
], function($, logger, RENT, MainView){


	var initialize = function(subroute){
		RENT.setLangRes(RENT.getLang(), ['rent_user']);
		$(function(){
			logger.debug('new home main view');
			var mainView = new MainView({el:'#main',subroute:subroute});
			mainView.navibar('#navbar');
		});
  };
  return {
    initialize: initialize
  };
});