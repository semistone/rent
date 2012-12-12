// Filename: user.js
define([
  'jQuery',
  'logger',
  'RentCommon',
  './view.user',
  './model.user'
], function($, logger, RENT, RegisterView, UserModel){
	var main_view,
		model = new UserModel();
	$(function(){
		RENT.bindLoadingPage("#supersized-loader");
	});
	RENT.setLangRes(RENT.getLang(), ['rent_user']);
	main_view = new RegisterView({el:'#main', model:model});
	return main_view;
});