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
	$.cookie("D", "test_device",{path:'/'});
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
