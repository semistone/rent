//
// step1 view
//
define([
  'jQuery',
  'RentCommon',
  'modules/backyard/model.backyard'
  ], function($,RENT) {
module("Backyard");
RENT.CONSTANTS.APIs_BASE_DIR = '../';

//insert into DEVICE values ('test new device','test list devices','','token','2','1','','0','0');
//insert into DEVICE values ('test new device2','test list devices','','token','2','1','','0','0');
asyncTest('test list users', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B59AC9C0799BA776136A29B67846B5B9B12ED3F2CE1E88838AAEC6289A97A16D6";
	$.cookie("D", test_device_cookie_value,{path:'/'});
	var collection = new RENT.backyard.collection.UserCollection();
	options = {
		data: $.param({ limit: 2, offset:0}),
		success:function(){
			ok(true, 'expect execute success');
			start();	
		},
		error:function(){
			ok('', 'expect execute success');
			start();
		}
	};
	collection.fetch(options);
});

asyncTest('test sently callback', function(assert) {
	var data = {from:'+886911826844',text:'R:123456'};
	$.get('../rest/sently_callback/123456789', data,function(data) {
		ok(true, 'expect execute success');
		start();
	});
});
//
// insert into DEVICE values ('SSO','test sso','','token','5','1','','0','0');
// insert into USER values ('test sso','234242424','','','','','','','','','1','1349743961','1349743961');
// insert into USER values ('test list devices','23432424','test@email.com','test list devices','','','','','','','1','1349743961','1349743961');
//
asyncTest('test mobile auth request', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B59AC9C0799BA776136A29B67846B5B9B12ED3F2CE1E88838AAEC6289A97A16D6";
	$.cookie("D", test_device_cookie_value,{path:'/'});
	var data = "{\"mobilePhone\":\"+886911826844\",\"requestFrom\":\"test sso\",\"requestId\":\"test request id\",\"done\":\"http://done\"}";
	var success = function(){
		ok(true, 'expect execute success');
		start();		
	};
	var setting = {
	    url:'../rest/user/mobile_auth_request',
	    data: data,
	    dataType:'json',
	    type:'POST',
	    contentType: "application/json; charset=utf-8",
	    success:success
	};
	$.ajax(setting);

});


});