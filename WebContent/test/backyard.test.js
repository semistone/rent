<<<<<<< HEAD
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

//insert into DEVICE values ('test new device','test list devices','','token','2','1','','','0','0');
//insert into DEVICE values ('test new device2','test list devices','','token','2','1','','','0','0');
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
//insert into USER values ('test sently callback','FE4DC1C73AE63F2BDE5C80E1E3B93BFC','','sently','','','','','','','0','1349743961','1349743961');
//insert into DEVICE values ('test sently','test sently callback','','DF7F50C99E6F7675A4BFA1114D332AB3','1','1','','','0','0');
asyncTest('test sently callback', function(assert) {
	var data = {from:'+886911826844',text:'R:123456'};
	$.get('../rest/sently_callback/123456789', data,function(data) {
		ok(true, 'expect execute success');
		start();
	});
});

// insert into MOBILE_AUTH_REQUEST values ('test_request','0','http://tw.yahoo.com',null,'test id',null,'12313131',null,null,'0');
asyncTest('test show_mobile_auth_request', function(assert) {
	var success = function(){
		ok(true, 'expect execute success');
		start();		
	};
	var error = function(){
		ok(false, 'expect execute success');
		start();		
	};

	var test = new RENT.backyard.model.MainModel();
	test.show_mobile_auth_request('test_request',{
		success:success,
		error:error
	});
});


=======
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

//insert into DEVICE values ('test new device','test list devices','','token','2','1','','','0','0');
//insert into DEVICE values ('test new device2','test list devices','','token','2','1','','','0','0');
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
//insert into USER values ('test sently callback','FE4DC1C73AE63F2BDE5C80E1E3B93BFC','','sently','','','','','','','0','1349743961','1349743961');
//insert into DEVICE values ('test sently','test sently callback','','DF7F50C99E6F7675A4BFA1114D332AB3','1','1','','','0','0');
asyncTest('test sently callback', function(assert) {
	var data = {from:'+886911826844',text:'R:123456'};
	$.get('../rest/sently_callback/123456789', data,function(data) {
		ok(true, 'expect execute success');
		start();
	});
});

// insert into MOBILE_AUTH_REQUEST values ('test_request','0','http://tw.yahoo.com',null,'test id',null,'12313131',null,null,'0');
asyncTest('test show_mobile_auth_request', function(assert) {
	var success = function(){
		ok(true, 'expect execute success');
		start();		
	};
	var error = function(){
		ok(false, 'expect execute success');
		start();		
	};

	var test = new RENT.backyard.model.MainModel();
	test.show_mobile_auth_request('test_request',{
		success:success,
		error:error
	});
});


>>>>>>> master
});