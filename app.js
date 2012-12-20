var app = require('http').createServer(handler),
    io = require('socket.io').listen(app),
    url = require('url'),
    crypto = require('crypto'),
    cookie = require('express/node_modules/cookie'),
    http = require('http'),
    key = 'thebestsecretkey',
    registerHost = 'localhost',
    registerPort = 8080,
    logger  = io.log,
    listenPort = 9090,
    callback;

listenPort = process.argv[2];     
callback = '127.0.0.1:'+listenPort;
logger.info('listen port is ' + listenPort);
app.listen(listenPort);

function handler(req, res) {
    var cmd = url.parse(req.url, true).pathname.substr(1), id, body = '';
    cmd = cmd.split('/');
    id = cmd[0]; 
    cmd = cmd[1]; 
    logger.debug('id is '+id + ' cmd is '+cmd);
    req.on('data', function(data){
        body += data;
    });
    req.on('end', function(){
        logger.debug('body is '+body);
	io.sockets.in(id).emit(cmd, JSON.parse(body));
        res.writeHead(200, {"Content-Type": "text/plain"});
        res.end('Hello '+ id + '\n' );
    });
}; 

function register_connect(headers) {
    var client = http.createClient(registerPort, registerHost), 
        request;
    headers['Content-Type']  = 'application/json';
    request = client.request('GET', '/rest/device/connect/'+callback, headers);

    request.on('response', function(response) {
        if (response.statusCode != 200) {
            logger.debug('regstier connection status is '+response.statusCode);
            return;
        } else {
            logger.debug('register connection ok');
        }
    });
    request.end();
};

function register_disconnect(headers) {
    var client = http.createClient(registerPort, registerHost), 
        request;
    headers['Content-Type']  = 'application/json';
    request = client.request('GET', '/rest/device/disconnect', headers);

    request.on('response', function(response) {
        if (response.statusCode != 200) {
            logger.debug('regstier disconnect status is '+response.statusCode);
            return;
        } else {
            logger.debug('register disconnect ok');
        }
    });
    request.end();


}
io.configure(function(){
    io.set('authorization', function(data, callback){
        var session, decipher;
        if (data.headers.cookie) {
            data.cookie = cookie.parse(data.headers.cookie);
            logger.debug(data.cookie['S']);
            if (data.cookie['S'] == undefined) {
                return callback('no session cookie' ,false);
            }
            decipher = crypto.createDecipher('aes-128-ecb', key);
            session = decipher.update(data.cookie['S'] ,'hex','utf8')
            session += decipher.final('utf8')
            if (cookie == null) {
                return callback('no cookie decrypted' ,false);
            } else {
                logger.debug('session is '+session);
                data.session = session;
            }
        } else {
            return callback('no cookie transmitted' ,false);
        }
        callback(null ,true);
    });
});
io.sockets.on('connection', function (socket) {
    var id = socket.handshake.session.split(':')[2];
    socket.join(id);
    logger.info('new connection join id is '+ id);
    register_connect(socket.handshake.headers);
    socket.on('disconnect', function(){
        register_disconnect(socket.handshake.headers);
	socket.leave(id);
    });
});
