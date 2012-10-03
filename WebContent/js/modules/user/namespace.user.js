// define constants and common static function here
define([
  'jQuery',
  'RentCommon'
], function($,RENT) {
	$.extend(RENT, {
		user : {
			model : {},
			view : {},
			collection : {}
		}
	});	
	return RENT;
});
