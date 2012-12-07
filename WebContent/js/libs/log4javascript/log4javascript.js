var lib_logger = null;
if (/debug=1/.exec(window.location.href)) {
	var lib_logger = 'libs/log4javascript/log4javascript-1.0.0';
}
define([lib_logger], function(){
	var logger = {
			debug: function(msg) {

			},
			error: function(msg) {

			},
			warn: function(msg) {

			},
			info: function(msg) {
				
			},
			fatal: function(msg) {
			
			},
			trace: function(msg){
			
			}
	}; 
	var setDebugLogger = function(){	
		if (typeof log4javascript == "undefined") {			
			return;
		} else {
			logger = log4javascript.getDefaultLogger();
			// enabled or disabled all logging
			log4javascript.setEnabled(true);		
		}
	};
	var debug = /debug=1/;
	if (debug.exec(window.location.href)) {
		setDebugLogger();
	}
	return logger;
});
