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
function fetch_callback(headers, id, fn){
    var client = http.createClient(registerPort, registerHost), 
        request ;
    headers['Content-Type']  = 'application/json';
    request = client.request('GET', '/rest/device/callbacks/'+id, headers);

    request.on('response', function(response) {
        if (response.statusCode != 200) {
            logger.debug('fetch callback error'+response.statusCode);
            fn(response.statusCode);
            return;
        } else {
            response.on('data', function (chunk) {
                logger.debug('fetch callbacks is '+chunk);
                fn(200, JSON.parse(chunk));
            });
        }
    });
    request.end();

};
function reset_callback(fn){
    var client = http.createClient(registerPort, registerHost), 
        request, headers ={} ;
    logger.info('reset callback');
    headers['Content-Type']  = 'application/json';
    request = client.request('GET', '/rest/device/callbacks/reset/'+callback, headers);

    request.on('response', function(response) {
        if (response.statusCode != 200) {
            logger.debug('reset callback error'+response.statusCode);
            fn(response.statusCode);
            return;
        } else {
            logger.info('reset callback success');
            fn();
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
    var id = socket.handshake.session.split(':')[2],
        headers = socket.handshake.headers;
    socket.join(id);
    logger.info('new connection join id is '+ id);
    register_connect(headers);
    socket.on('msg', function(data, fn){
        logger.debug('msg to '+data.to);
        fetch_callback(headers, data.to, function(status, callbackData){
             if (status == 200) {
                 for(var i in callbackData) {
                     logger.debug('callback in '+callbackData[i]);
                     if (callback == callbackData[i]) {
                         logger.debug('send alert to '+data.to+ ' and conetnt is '+data.content);
                         io.sockets.in(data.to).emit('msg', {content: data.content});
                     } else {
                         logger.debug('chat server not match to '+callbackData[i]);
                     }
                 }
             }   
        });
    });
    socket.on('disconnect', function(){
        logger.debug('disconnect '+id);
        socket.leave(id);
        if (io.sockets.in(id).clients().length == 1){
            logger.debug('disconnect all session '+id);
            register_disconnect(socket.handshake.headers);
        } else {
            logger.debug('disconnect id '+id+' client length is '+io.sockets.in(id).clients().length);
        }
    });
});
(function(){
    listenPort = process.argv[2];     
    callback = '127.0.0.1:'+listenPort;
    //
    // reset callback first.
    //
    reset_callback(function(){
        logger.info('listen port is ' + listenPort);
        app.listen(listenPort);
    });
})()
