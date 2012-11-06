// define constants and common static function here
define([
  'jQuery',
  'RentCommon',
  'logger',
  'Underscore'
], function($, RENT, logger, _) {
	$.extend(RENT, {
		user : {
			model : {},
			view : {},
			collection : {},
			dotDone:function(mobileAuthRequestForm,response){
				//
				// redirect back to .done
				//		  
				if (mobileAuthRequestForm != null && mobileAuthRequestForm['done'] != undefined) {
					logger.info('done exist '+mobileAuthRequestForm['done']);
					var field = ['requestId','responseTime','status','sign'];
					var params = '?';
					$.each(response,function(key,value){
						if ($.inArray(key,field) >=0 ){
							params += key+'='+encodeURIComponent(value)+'&';
						}
					});
					window.location.replace(mobileAuthRequestForm['done']+ params);
					return;
				}		
			},
			checkRole:function(userModel, roleId, inRole, noRole){
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
		}
	});	
	return RENT;
});
