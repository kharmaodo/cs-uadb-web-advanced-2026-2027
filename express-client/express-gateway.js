const express = require('express');
const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');

const app = express();
app.use(express.json());

// Load proto and initialize gRPC client
const packageDef = protoLoader.loadSync('../contrats/greeter.proto');
const greeterProto = grpc.loadPackageDefinition(packageDef).greeter;
const client = new greeterProto.Greeter('localhost:50051', grpc.credentials.createInsecure());

// Expose REST endpoint
app.get('/greet/:name', (req, res) => {
  client.SayHello({ name: req.params.name }, (err, response) => {
    if (err) {
      return res.status(500).json({ error: err.message });
    }
    res.json({ greeting: response.message });
  });
});

app.listen(3000, () => {
  console.log('Express gateway running on port 3000');
});
