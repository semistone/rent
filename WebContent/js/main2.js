require.config({
	paths : {
		jQuery : 'libs/jquery/jquery',
		Underscore : 'libs/underscore/underscore',
		Backbone : 'libs/backbone/backbone',
		jQueryUI : 'libs/jquery-ui/jquery-ui',
		Mustache : 'libs/mustache/mustache',
		logger : 'libs/log4javascript/log4javascript',
		RentCommon : 'libs/rent-common/rent-common',
		Validator : 'libs/jquery-validate/jquery-validate',
		i18N : 'libs/jquery-i18n/jquery-i18n',
		Bootstrap : 'libs/bootstrap/bootstrap'
	},
	shim : {
		jQuery : {
			exports : "$"
		},
		Underscore : {
			exports : "_"
		},
		Mustache :{
			exports : "Mustache"
		},
		Backbone : {
			deps : [ "Underscore", "jQuery" ],
			exports : "Backbone"
		},
		Bootstrap : {
			deps : [ 'jQuery' ],
			exports : "Bootstrap"
		},
		i18N : {
			deps : [ 'jQuery' ],
			exports : "i18n"
		}
	}
});


require([
  // Load our app module and pass it to our definition function
  'modules/backyard/backyard.loader',
  'order!jQuery',
  'order!jQueryUI',
  'order!Underscore',
  'order!Backbone',
  'order!Validator',
  'order!i18N',
  'order!Bootstrap'
], function(Backyard){
  // The "app" dependency is passed in as "App"
  // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
	Backyard.initialize();
});

