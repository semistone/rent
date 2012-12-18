var app = require('http').createServer(handler),
    io = require('socket.io').listen(app),
    url = require('url'),
    crypto = require('crypto'),
    cookie = require('express/node_modules/cookie'),
    pool = {},
    http = require('http'),
    key = 'thebestsecretkey',
    registerHost = 'localhost';
     
app.listen(9090);

function handler(req, res) {
    var cmd = url.parse(req.url, true).pathname.substr(1), id, body = '';
    cmd = cmd.split('/');
    id = cmd[0]; 
    cmd = cmd[1]; 
    console.log('id is '+id + ' cmd is '+cmd);
    req.on('data', function(data){
        body += data;
    });
    req.on('end', function(){
        if (pool[id] != undefined) {
            console.log('body is '+body);
            pool[id].emit(cmd, JSON.parse(body));
            res.writeHead(200, {"Content-Type": "text/plain"});
            res.end('Hello '+ id + '\n' );
        } else {
            console.log('id not exist');
            res.writeHead(401, {"Content-Type": "text/plain"});
            res.end( id + ' not found\n');
        }
    });
}; 

function register_connect(cookie) {
    var client = http.createClient(80, registerHost), 
        headers, request;

    headers = {
        'Cookie': cookie,
        'Content-Type': 'application/json'
    };
    request = client.request('GET', '/rest/device/connect', headers);

    var resString = '';
    request.on('response', function(response) {
        if (response.statusCode != 200) {
            console.log('regstier connection status is '+response.statusCode);
            return;
        } else {
            console.log('register connection ok');
        }
    });
    request.end();
};

io.configure(function(){
    io.set('authorization', function(data, callback){
        var session, decipher;
        if (data.headers.cookie) {
            data.cookie = cookie.parse(data.headers.cookie);
            console.log(data.cookie['S']);
            if (data.cookie['S'] == undefined) {
                return callback('no session cookie' ,false);
            }
            decipher = crypto.createDecipher('aes-128-ecb', key);
	    session = decipher.update(data.cookie['S'] ,'hex','utf8')
	    session += decipher.final('utf8')
            if (cookie == null) {
                return callback('no cookie decrypted' ,false);
            } else {
                console.log('session is '+session);
                data.session = session;
            }
        } else {
            return callback('no cookie transmitted' ,false);
        }
        callback(null ,true);
    });
});
io.sockets.on('connection', function (socket) {
    socket.emit('news', { hello: 'world' });
    socket.on('my other event', function (data) {
        console.log(data);
    });
    var id = socket.handshake.session.split(':')[0];
    pool[id] = socket;
    console.log('new connection sesion id is '+ id);
    register_connect(socket.handshake.headers.cookie);
    socket.on('disconnect', function(){
        delete pool[id];
    });
});
