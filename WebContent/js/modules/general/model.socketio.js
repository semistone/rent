define([
  'SocketIO',
  'RentCommon',
  'logger'
], function(io, RENT, logger) {
var socket;
socket = io.connect('/');
socket.on('alert', function (data) {
	logger.info('notify '+data.msg);
	alert(data.msg);
});		
return socket;
});