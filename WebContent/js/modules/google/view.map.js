define([
  'jQuery',
  'Underscore',
  'Backbone',
  'logger'
  ], function($, _, Backbone, logger) {
var Map = Backbone.View.extend({
	whereami:function(){
		this.$el.attr('id', 'map_canvas');
		require('modules/google/whereami',function(func){
			func.initialize();
		});
	}	
});
return Map;
});