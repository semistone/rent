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
    RentCommon:  'libs/rent-common/rent-common',
    Validate: 'libs/jquery-validate/jquery-validate',
    i18N:'libs/jquery-i18n/jquery-i18n'
  }

});

require([
  // Load our app module and pass it to our definition function
  'modules/user/user.loader',
  'order!jQuery',
  'order!jQueryUI',
  'order!Underscore',
  'order!Backbone',
  'order!Validate',
  'order!i18N'
], function(User){
  // The "app" dependency is passed in as "App"
  // Again, the other dependencies passed in are not "AMD" therefore don't pass a parameter to this function
	User.initialize();
});

