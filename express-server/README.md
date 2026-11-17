 # gRPC Server

 ## Libraries

### Packages 

```bash
 npm install @grpc/grpc-js @grpc/proto-loader
 npm install @grpc/reflection
 ```

### Proto with Protoc

```bash
mkdir -p descriptors

protoc \
  --proto_path=../contrats \
  --include_imports \
  --descriptor_set_out=descriptors/greeter.pb \
  greeter.proto
```

### List of services

```bash
grpcurl -plaintext localhost:50051 list
```


 
### Get from the services

```bash
grpcurl -plaintext \
  -d '{"name":"Maodo"}' \
  localhost:50051 \
  greeter.Greeter/SayHello
```

```bash
grpcurl -plaintext localhost:50051 describe greeter.Greeter.SayHello
```

```bash
grpcurl -plaintext localhost:50051 describe greeter.Greeter
```

```bash
grpcurl -plaintext localhost:50051 describe greeter.HelloRequest
```

```bash
grpcurl -plaintext localhost:50051 describe greeter.HelloReply
```


Dockerize 

```bash
 docker run --rm \
  --name gripmock \
  -p 4770:4770 \
  -p 4771:4771 \
  --mount type=bind,source=/home/dmaodo/cs-uadb-web-advanced-2026-2027/contrats,target=/proto,readonly \
  bavix/gripmock \
  /proto/greeter.proto
```





