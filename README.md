# cs-uadb-web-advanced-2026-2027
Only for pedagogical and academic courses ( in the purpose of UADB curriculum)


 # How to do with customization 
## Backend Spring

 ```bash

head -c 300 contrats.json
python3 -m json.tool contrats.json > /dev/null && echo "JSON OK"

wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/7.10.0/openapi-generator-cli-7.10.0.jar -O openapi-generator-cli.jar

java -jar openapi-generator-cli.jar generate \
  -i contrats.json \
  -g spring \
  --library spring-cloud \
  -o spring_api_client

```



## Frontend TypeScript NodeJS 

 ```bash

 git clone https://github.com/swagger-api/swagger-codegen
cd swagger-codegen
mvn clean package
java -jar swagger-codegen/modules/swagger-codegen-cli/target/swagger-codegen-cli.jar generate \
   -i contrats.json \
   -l typesecript \
   -o typescript_api_client
```