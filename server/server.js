var express = require('express');
var https = require('https');
var http = require('http');
var fs = require('fs');

var options = {
  key: fs.readFileSync('./zerossl/key.key'),
  cert: fs.readFileSync('./zerossl/crt.crt')
};

var app = express();

// http.createServer(app).listen(80);
https.createServer(options, app).listen(443);