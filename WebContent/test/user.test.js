module("User");
asyncTest('test get null user and return 404', function(assert) {
	var user = new RENT.user.model.UserModel();
	user.bind('error',function(model,resp){
		assert.equal(404, resp.status);
		start();
	});
	user.fetch();
});

