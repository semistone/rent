// define constants and common static function here
define([
  'jQuery',
  'RentCommon',
  'logger'
], function($,RENT,logger) {
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
			}			
		}
	});	
	return RENT;
});
