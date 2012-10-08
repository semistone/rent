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



});