//
// step1 view
//
define([
  'jQuery',
  'RentCommon',
  'modules/user/model.user'
  ], function($,RENT) {
module("User");
RENT.CONSTANTS.APIs_BASE_DIR = '../';

asyncTest('test get null user and return 404', function(assert) {
	$.cookie("D", null,{path:'/'});
	var user = new RENT.user.model.UserModel();
	user.bind('error',function(model,resp){
		assert.equal(404, resp.status);
		start();
	});
	user.bind('change',function(){
		ok('', 'not expect execute success');
		start();
	});
	user.fetch();
});


asyncTest('test new device', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B9D688F00D39C706C082258C3C6C5A00F"
	$.cookie("D", test_device_cookie_value,{path:'/'});
	var user = new RENT.user.model.UserModel();
	options = {
		success:function(){
			ok(true, 'expect execute success');
			start();	
		},
		error:function(){
			ok('', 'expect execute success');
			start();
		}
	}
	user.save({mobilePhone:'886'+Math.ceil(new Date().getTime()/1000), countryCode:'886'},options);
});

});