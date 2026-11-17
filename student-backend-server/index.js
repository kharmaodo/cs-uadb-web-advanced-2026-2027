const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const { ReflectionService } = require('@grpc/reflection');
const path = require('node:path');

// Load the Protocol Buffers contract
const PROTO_PATH = path.resolve(__dirname, '../contrats/students.proto');

const packageDef = protoLoader.loadSync(PROTO_PATH, {
  keepCase: true,
  longs: String,
  enums: String,
  defaults: true,
  oneofs: true,
});

// The package declared in students.proto is "uadb"
const studentsProto = grpc.loadPackageDefinition(packageDef).uadb;

// Mock database
const students = [
  {
    id: '1',
    matricule: '20260001',
    lastname: 'Diop',
    firstname: 'Amadou',
    idCard: 'SN123456789',
    idDriver: 'DRV001',
    telephone: '+221770000001',
    emailInstitutionnel: 'amadou.diop@uadb.edu.sn',
    ufr: 'SET',
  },
  {
    id: '2',
    matricule: '20260002',
    lastname: 'Fall',
    firstname: 'Fatou',
    idCard: 'SN987654321',
    idDriver: '',
    telephone: '+221770000002',
    emailInstitutionnel: 'fatou.fall@uadb.edu.sn',
    ufr: 'SES',
  },
];


// RPC implementation
function GetStudentByMatricule(call, callback) {
  const { matricule } = call.request;

  // Validate request
  if (!matricule || !matricule.trim()) {
    return callback({
      code: grpc.status.INVALID_ARGUMENT,
      message: 'Matricule is required',
    });
  }

  // Search student
  const student = students.find(
    (s) => s.matricule === matricule.trim()
  );

  if (!student) {
    return callback({
      code: grpc.status.NOT_FOUND,
      message: `Student with matricule ${matricule} not found`,
    });
  }

  // Return SearchStudentResponse
  return callback(null, student);
}

const server = new grpc.Server();
server.addService(studentsProto.Searcher.service, { GetStudentByMatricule });

// Enable gRPC Server Reflection
const reflection = new ReflectionService(packageDef);
reflection.addToServer(server);

const PORT = process.env.GRPC_PORT || 50051;

server.bindAsync(
  `0.0.0.0:${PORT}`,
  grpc.ServerCredentials.createInsecure(),
  (error, port) => {
    if (error) {
      console.error('Failed to start gRPC server:', error);
      process.exitCode = 1;
      return;
    }

    console.log(`UADB gRPC server running on port ${port}`);
  }
);
