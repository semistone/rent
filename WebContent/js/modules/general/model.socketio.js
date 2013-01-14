define([
  'SocketIO',
  'RentCommon',
  'logger'
], function(io, RENT, logger) {
var socket;
socket = io.connect('/');
socket.on('msg', function (data) {
	logger.info('notify '+data.content);
	alert(data.content);
});		
return socket;
});
