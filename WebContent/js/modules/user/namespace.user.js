// define constants and common static function here
define([
  'jQuery',
  'RentCommon',
  'logger',
  'Underscore'
], function($, RENT, logger, _) {
		var user;
		user = {
			model : {},
			view : {},
			collection : {},

			check_role:function(userModel, roleId, inRole, noRole){
				var user = userModel.get('user');
				if (user.roles == undefined) {
					userModel.get_roles({
						success:function(){
							if (user.roles && _.contains(user.roles,roleId)) {
								inRole();
							} else {
								noRole();
							}
						},
						error:function(model, resp){
							logger.error('get roles fail');
							RENT.simpleErrorDialog(resp,'');
						}
					});
				} else {
					if (user.roles && _.contains(user.roles,roleId)) {
						inRole();
					} else {
						noRole();
					}
				}

			}
	};
	return user;
});
