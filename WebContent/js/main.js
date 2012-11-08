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
require(['order!jQuery','order!Underscore','order!Backbone'],function($, _, Backbone){
	var current = null;

	var backyard  = function(){
		if (current == 'backyard') {
			return;
		}
		current = 'backyard';
		require(['modules/backyard/backyard.loader'], function(Backyard) {
			Backyard.initialize();
		});
	};
	var main= function(subroute){
		if (current == 'defaultRoute') {
			return;
		}
		current = 'defaultRoute';
		require(['modules/user/user.loader'], function(User) {
			User.initialize(subroute);
		});
	};
	var defaultRoute = function(subroute){
		main('');
	};	
	var MainRoute =  Backbone.Router.extend({
		routes: {
			'main/*subroute': 'main',
			'backyard':'backyard',
			'*path':  'defaultRoute'
		}
	});
	var router = new MainRoute();
	router.on('route:defaultRoute', defaultRoute);
	router.on('route:backyard', backyard);
	router.on('route:main', main);
	Backbone.history.start();
});

