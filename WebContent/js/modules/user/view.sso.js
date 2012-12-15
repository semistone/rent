define([
	'Underscore',
	'./view.user',
	'RentCommon',
	'logger'
  ], function(_, UserView, RENT, logger) {
var SSOView;
SSOView = UserView.extend({ 
	initialize:function(){
		logger.debug('SSOView initialize');
		this.constructor.__super__.initialize.apply(this);
		_.bindAll(this, 'dot_done', 'render', 'handle_mobile_auth_request_form');
		this.on('login_success', this.dot_done);
	},
	handle_mobile_auth_request_form:function(){
		this.mobileAuthRequestForm = RENT.getQueryVariables();
		var _this = this;
		if (this.mobileAuthRequestForm['requestId'] != null) {
			logger.debug('deal with request');
			this.model.mobile_auth_request(this.mobileAuthRequestForm,{
				success:function(model,response){
					logger.debug('success');					
					_this.model.set(model);
				},
				error:function(resp){
					if (resp != null && resp.status == 409) {
						_this.model.set({status:1});
					} else {
						_this.error(_this.model,resp);						
					}
				}
			});
			return true;
		} else {
			this.mobileAuthRequestForm = null; //reset to null
			return false;			
		}
	},
	dot_done:function(model){
		//
		// redirect back to .done
		//		  
		if (this.mobileAuthRequestForm != null && this.mobileAuthRequestForm['done'] != undefined) {
			logger.info('done exist '+this.mobileAuthRequestForm['done']);
			var field = ['requestId','responseTime','status','sign'];
			var params = '?';
			$.each(model.toJSON(), function(key,value){
				if ($.inArray(key,field) >=0 ){
					params += key+'='+encodeURIComponent(value)+'&';
				}
			});
			window.location.replace(this.mobileAuthRequestForm['done']+ params);
			return;
		}		
	},
	render:function(){
		logger.debug('sso view render');
		var status =this.model.get('status');
		if (this.mobileAuthRequestForm != null && 
				(status == undefined || status == 0) &&
				this.model.get('userId') != null
		) {
			logger.debug('set status = 1 to prevent step1');
			this.model.set({status:1}, {silent:true});
		}
		this.constructor.__super__.render.apply(this);
	}
	
});
return SSOView;
});