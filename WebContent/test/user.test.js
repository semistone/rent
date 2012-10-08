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

//insert into DEVICE values ('test new device','test list devices','','token','2','1','','0','0');
//insert into DEVICE values ('test new device2','test list devices','','token','2','1','','0','0');
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
});