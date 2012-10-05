// Filename: user.js
define([
  'jQuery',
  'logger',
  './model.backyard',
  './view.backyard',
  'RentCommon'
], function($, logger, BackyardModel, BackyardView, RENT){
  var initialize = function(){
	  RENT.setLangRes(RENT.getLang(), ['rent_user']);
	  var model = new BackyardModel({});
	  $(function(){
		  logger.debug('new backyard view');
		  var view = new BackyardView({el:'#main',model:model}); 
		  view.render();
	  });
  }
  return {
    initialize: initialize
  };
});