const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);

const port = process.env.PORT || 8000;

// var options = {
//   key: fs.readFileSync('./zerossl/key.key'),
//   cert: fs.readFileSync('./zerossl/crt.crt')
// };

io.on('connection', function (socket) {
  console.log(socket.id);

  socket.on('toRon', function (data) {
    console.log('toRon');
    console.log(data);
    socket.broadcast.emit('toRon', data);
  });

  socket.on('toHermione', function (data) {
    console.log('toHermione');
    socket.broadcast.emit('toHermione', data);
  });
});

app.get('/', (req, res) => {
  res.send("test");
})

// http.createServer(app).listen(80);
// https.createServer(options, app).listen(8000);

app.listen(port, function() {
  console.log("Server running on port " + port);
})