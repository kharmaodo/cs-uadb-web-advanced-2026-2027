# cs-uadb-web-advanced-2026-2027

Only for pedagogical and academic courses (in the purpose of UADB curriculum).

# How to generate API clients with customization

The contract file `contrats.json` is an **OpenAPI 3.x** document, so we use
[OpenAPI Generator](https://openapi-generator.tech/) (not Swagger Codegen 2.x, which only supports Swagger 2.0).

## Prerequisites

- Java 11+
- `wget`
- `contrats.json` at the root of the project

## 0. Check the contract and download the generator

```bash
head -c 300 contrats.json
python3 -m json.tool contrats.json > /dev/null && echo "JSON OK"

wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/7.26.0/openapi-generator-cli-7.26.0.jar \
  -O openapi-generator-cli.jar
```

## 1. Backend Spring

```bash
java -jar openapi-generator-cli.jar generate \
  -i contrats.json \
  -g spring \
  --library spring-cloud \
  -o spring_api_client
```

Optional: add `-p interfaceOnly=true` to generate only the Feign interfaces.

## 2. Frontend TypeScript

Choose the generator that matches your frontend stack.

| Generator            | Use case                                      |
|----------------------|-----------------------------------------------|
| `typescript-node`    | Pure NodeJS (scripts, server-side)            |
| `typescript-axios`   | React, Vue, or any browser/Node project       |
| `typescript-angular` | Angular (uses `HttpClient` and RxJS)          |

### 2.1 NodeJS

```bash
java -jar openapi-generator-cli.jar generate \
  -i contrats.json \
  -g typescript-node \
  -o typescript_api_client
```

### 2.2 Axios (React, Vue, browser or Node)

`typescript-axios` is often more suitable than `typescript-node` for browser-based frontends.

```bash
java -jar openapi-generator-cli.jar generate \
  -i contrats.json \
  -g typescript-axios \
  -o typescript_axios_client
```

### 2.3 Angular

`typescript-angular` generates injectable services based on Angular's `HttpClient`.

```bash
java -jar openapi-generator-cli.jar generate \
  -i contrats.json \
  -g typescript-angular \
  -p ngVersion=17.0.0 \
  -o typescript_angular_client
```

Adjust `ngVersion` to the Angular version used by your project.

## 3. List all available generators

```bash
java -jar openapi-generator-cli.jar list
```