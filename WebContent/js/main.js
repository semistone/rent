<<<<<<< HEAD
// Filename: main.js

// Require.js allows us to configure shortcut alias
// There usage will become more apparent futher along in the tutorial.
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
		Bootstrap : 'libs/bootstrap/bootstrap',
		Facebook : 'libs/facebook/fb'
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
		},
		Facebook:{
			exports: 'FB'
		}
		
	}
});

require([
// Load our app module and pass it to our definition function
'modules/user/user.loader', 'order!jQuery',
		'order!Underscore', 'order!Backbone', 'order!i18N'], function(User) {
	// The "app" dependency is passed in as "App"
	// Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
	User.initialize();
});

=======
// Filename: main.js

// Require.js allows us to configure shortcut alias
// There usage will become more apparent futher along in the tutorial.
require.config({
	paths : {
		jQuery : 'libs/jquery/jquery-1.8.2',
		Underscore : 'libs/underscore/underscore-1.4.1',
		Backbone : 'libs/backbone/backbone-0.9.2',
		jQueryUI : 'libs/jquery-ui/jquery-ui',
		Mustache : 'libs/mustache/mustache-1.0.0',
		logger : 'libs/log4javascript/log4javascript',
		RentCommon : 'libs/rent-common/rent-common',
		Validator : 'libs/jquery-validate/jquery.validate-1.9.0',
		i18N: 'libs/jquery-i18n/jquery.i18n.properties',
		Bootstrap : 'libs/bootstrap/bootstrap.2.1.1',
		Facebook : '//connect.facebook.net/en_US/all',
		GoogleMap: 'libs/google/map',
		SocketIO :'../socket.io/socket.io',
		template: '../html'
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
			exports: '$.i18n'
		},
		Facebook:{
			exports: 'FB'
		},
		logger:{
			exports: 'logger'
		},
		Validator: {
			deps : [ 'jQuery' ],
			exports: '$.validate'
		}
		
	}
});

require(['jQuery','Underscore','Backbone', 'logger', 'RentCommon', 'Mustache'],function($, _, Backbone, logger){
	var defaultRoute = function(){
		var module , subroute = null;
		if (arguments.length == 2) {
			module = arguments[0];
			subroute = arguments[1];
		} else if (arguments.length == 1 && arguments[0] != '' ){
			module = arguments[0];
		} else {
			module = 'home';
		}
		require(['modules/'+module+'/'+module+'.loader'], function(Home) {
			Home.initialize(subroute);
		});	
	};	
	var MainRoute =  Backbone.Router.extend({
		routes: {
			':path/:subroute': 'defaultRoute',
			':path':  'defaultRoute'
		}
	});
	var router = new MainRoute();
	router.on('route:defaultRoute', defaultRoute);
	if (!Backbone.history.start()){
		router.trigger('route:defaultRoute');
	}
});

>>>>>>> master
