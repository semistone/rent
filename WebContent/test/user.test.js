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
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B9D688F00D39C706C082258C3C6C5A00F";
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
	};
	user.save({mobilePhone:'886'+Math.ceil(new Date().getTime()/1000), countryCode:'886'},options);
});

// insert into DEVICE values ('test new device','test delete device','','token','2','1','','0','0');
asyncTest('test delete device', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6BD37BF6ACC202E20BDCCCBED94AEF0F5C36E47F30B0FD14637616FD478DFE8267";
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
	};
	user.delete_device(options);
});

//insert into DEVICE values ('test new device','test list devices','','token','2','1','','','0','0');
//insert into DEVICE values ('test new device2','test list devices','','token','2','1','','','0','0');
asyncTest('test list devices', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B59AC9C0799BA776136A29B67846B5B9B12ED3F2CE1E88838AAEC6289A97A16D6";
	$.cookie("D", test_device_cookie_value,{path:'/'});
	var collection = new RENT.user.collection.UserCollection();
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

asyncTest('test list sessions', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B59AC9C0799BA776136A29B67846B5B9B12ED3F2CE1E88838AAEC6289A97A16D6";
	$.cookie("D", test_device_cookie_value,{path:'/'});
	var collection = new RENT.user.collection.SessionCollection();
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

asyncTest('test name device', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B59AC9C0799BA776136A29B67846B5B9B12ED3F2CE1E88838AAEC6289A97A16D6";
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
	};
	user.name_device('test',options);
});

//
//  insert into MOBILE_AUTH_REQUEST (REQUEST_ID,STATUS,TOKEN,USER_ID) values
//       ('test_verify','1','FE4DC1C73AE63F2BDE5C80E1E3B93BFC','test list devices');
//
asyncTest('test verify_mobile_auth_request_code', function(assert) {
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
	};
	user.set('requestId','test_verify');
	user.verify_mobile_auth_request_code('886911826844',options);
});
//
//insert into DEVICE values ('SSO','test sso','','token','5','1','','','0','0');
//insert into USER values ('test sso','234242424','','','','','','','','','1','1349743961','1349743961');
//insert into USER values ('test list devices','23432424','test@email.com','test list devices','','','','','','','1','1349743961','1349743961');
//
asyncTest('test mobile auth request', function(assert) {
	var test_device_cookie_value="B0559903B6F1A374BA0F1AEC8CBC6C6B59AC9C0799BA776136A29B67846B5B9B12ED3F2CE1E88838AAEC6289A97A16D6";
	$.cookie("D", test_device_cookie_value,{path:'/'});
	var requestId =new Date().getTime()/1000;
	var data = "{\"mobilePhone\":\"+886911826844\",\"requestFrom\":\"test sso\",\"requestId\":\""+requestId+"\",\"done\":\"http://done\"}";
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

asyncTest('test get_signature_of_mobile_auth_request', function(assert) {

	var success = function(){
		ok(true, 'expect execute success');
		start();		
	};
	var error = function(){
		ok(false, 'expect execute success');
		start();		
	};
	var data = {
		requestId:'a75ddb10-be9c-8b48-e95c-899cdc9fecd',
		mobilePhone:'8862222222222',
		forceReauth:true,
		done:'http://tw.yahoo.com',
		requestFrom:'test sso',
		requestTime:1350305838			
	};
	var test = new RENT.backyard.model.MainModel();
	test.get_signature_of_mobile_auth_request(data,{
		success:success,
		error:error
	});
});
});