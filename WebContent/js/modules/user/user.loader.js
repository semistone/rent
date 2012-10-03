// Filename: user.js
define([
  'jQuery',
  'logger',
  'modules/user/model.user',
  'modules/user/view.user',
  'RentCommon'
], function($, logger, UserModel, RegisterView, RENT){
  var initialize = function(){
	  RENT.setLangRes(RENT.getLang(), ['rent_user']);
	  var model = new UserModel({});
	  $(function(){
		  logger.debug('new register view');
		  var register = new RegisterView({el:'#main',model:model}); 		  
	  });
  }
  return {
    initialize: initialize
  };
});