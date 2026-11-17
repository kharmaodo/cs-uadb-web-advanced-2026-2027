
const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const { addReflection } = require('grpc-server-reflection');
const path = require('node:path');

const PROTO_PATH = path.resolve(
  __dirname,
  '../contrats/greeter.proto'
);

const DESCRIPTOR_PATH = path.resolve(
  __dirname,
  'descriptors/greeter.pb'
);

// Load protobuf definition
const packageDef = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true
});

const greeterProto =
  grpc.loadPackageDefinition(packageDef).greeter;

function sayHello(call, callback) {
  const name = call.request.name || 'World';

  callback(null, {
    message: `Hello, ${name}!`
  });
}

const server = new grpc.Server();

// Register Greeter service
server.addService(greeterProto.Greeter.service, {
  SayHello: sayHello
});

// Reflection expects the path to a binary descriptor set
addReflection(server, DESCRIPTOR_PATH);

// Start gRPC server
server.bindAsync(
  '0.0.0.0:50051',
  grpc.ServerCredentials.createInsecure(),
  (err, port) => {
    if (err) {
      console.error('gRPC server error:', err);
      process.exitCode = 1;
      return;
    }

    console.log(`gRPC server running on port ${port}`);
    console.log('gRPC reflection enabled');
  }
);