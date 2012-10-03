// Filename: main.js

// Require.js allows us to configure shortcut alias
// There usage will become more apparent futher along in the tutorial.
require.config({
  paths: {
    jQuery: 'libs/jquery/jquery',
    Underscore: 'libs/underscore/underscore',
    Backbone: 'libs/backbone/backbone',
    jQueryUI: 'libs/jquery-ui/jquery-ui',
    Mustache: 'libs/mustache/mustache',
    logger:   'libs/log4javascript/log4javascript',
    RentCommon:  'libs/rent-common/rent-common'
  }

});

require([
  // Load our app module and pass it to our definition function
  'modules/backyard/backyard.loader',
  'order!libs/jquery/jquery-min',
  'order!jQueryUI',
  'order!libs/underscore/underscore-min',
  'order!libs/backbone/backbone-min',
  'order!libs/jquery-validate/jquery-validate',
  'order!libs/jquery-i18n/jquery-i18n'
], function(Backyard){
  // The "app" dependency is passed in as "App"
  // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
	Backyard.initialize();
});

