define([
  'jQuery',
  'Underscore',
  'Backbone',
  'logger'
  ], function($, _, Backbone, logger) {
var Map = Backbone.View.extend({
	whereami:function(){
		this.$el.attr('id', 'map_canvas');
		this.$el.attr('style','width:50%; height:500px');
		require(['modules/google/whereami'],function(func){
			func.initialize();
		});
	}	
});
return Map;
});