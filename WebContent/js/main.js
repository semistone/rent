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
	//baseUrl:'http://ec2-54-251-33-245.ap-southeast-1.compute.amazonaws.com/js/',
	baseUrl:'/rent/js/',
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

require(['order!jQuery','order!Underscore','order!Backbone','order!RentCommon'],function($, _, Backbone){

	var backyard  = function(){
		require(['modules/backyard/backyard.loader'], function(Backyard) {
			Backyard.initialize();
		});
	};
	var main= function(subroute){
		require(['modules/user/user.loader'], function(User) {
			User.initialize(subroute);
		});
	};
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
			'main/*subroute': 'main',
			'backyard':'backyard',
			'*path/*subroute': 'defaultRoute',
			'*path':  'defaultRoute'
		}
	});
	var router = new MainRoute();
	router.on('route:defaultRoute', defaultRoute);
	router.on('route:backyard', backyard);
	router.on('route:main', main);
	Backbone.history.start();
});

